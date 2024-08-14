package delta.soundplayer.externals.player;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.PlayerEvent.PlayerEventCode;
import delta.soundplayer.externals.utils.AudioMath;

/**
 * A thread to play audio.
 * @author DAM
 */
public class PlayingRunnable extends Actor implements Runnable
{
  private static final Logger logger=LoggerFactory.getLogger(PlayingRunnable.class);

  private static final int BUFFER_SIZE=AudioOutput.BUFFER_SIZE;

  private AudioFormat _format;
  private AudioPlayer _player;
  private Buffer _buffer;
  private final Object _lock=new Object();
  private AudioOutput _output=new AudioOutput();
  private Track _currentTrack;
  private long _currentByte;
  private boolean _active=false;

  /**
   * Constructor.
   * @param player Associated player.
   * @param buffer Associated buffer.
   */
  public PlayingRunnable(AudioPlayer player, Buffer buffer)
  {
    _player=player;
    _buffer=buffer;
  }

  @Override
  public void process(Message message)
  {
    switch (message)
    {
      case PAUSE:
        setState(!_active);
      break;
      case PLAY:
        setState(true);
      break;
      case STOP:
        stop();
      break;
      case FLUSH:
        _output.flush();
      break;
      default:
      break;
    }
  }

  private void stop()
  {
    _output.flush();
    setState(false);
    _output.close();
    _player.fireEvent(PlayerEventCode.STOPPED);
  }

  private void setState(boolean newState)
  {
    if (_active!=newState)
    {
      _active=newState;
      synchronized (_lock)
      {
        _lock.notifyAll();
      }
    }
  }

  @Override
  public void run()
  {
    byte[] buf=new byte[BUFFER_SIZE];
    while (true)
    {
      synchronized (_lock)
      {
        try
        {
          while (!_active)
          {
            if (_output.isOpen())
            {
              _player.fireEvent(PlayerEventCode.PAUSED);
            }
            _output.stop();
            System.gc();
            _lock.wait();
          }

          _output.start();
          _player.fireEvent(PlayerEventCode.PLAYING_STARTED);
          out: while (_active)
          {
            int len=_buffer.read(buf,BUFFER_SIZE);
            while (len==-1)
            {
              if (!openNext())
              {
                stop();
                break out;
              }
              len=_buffer.read(buf,BUFFER_SIZE);
            }
            _currentByte+=len;
            _output.write(buf,0,len);
          }
        }
        catch (Exception e)
        {
          logger.warn("Exception while playing. Stopping now",e);
          _currentTrack=null;
          stop();
        }
      }
    }
  }

  private boolean openNext()
  {
    try
    {
      logger.debug("Getting next track");
      Buffer.NextEntry nextEntry=_buffer.pollNextTrack();
      if (nextEntry._track==null)
      {
        return false;
      }
      _currentTrack=nextEntry._track;
      if (nextEntry._forced)
      {
        _output.flush();
      }
      _format=nextEntry._format;
      _output.init(_format);
      if (nextEntry._startSample>=0)
      {
        _currentByte=AudioMath.samplesToBytes(nextEntry._startSample,_format.getFrameSize());
        _player.fireEvent(PlayerEventCode.SEEK_FINISHED);
      }
      else
      {
        _currentByte=0;
        _player.fireEvent(PlayerEventCode.FILE_OPENED);
      }
      return true;
    }
    catch (Exception e)
    {
      logger.warn("Could not open next track",e);
      return false;
    }
  }

  /**
   * Get the current track.
   * @return the current track.
   */
  public Track getCurrentTrack()
  {
    return _currentTrack;
  }

  /**
   * Get the audio output.
   * @return the audio output.
   */
  public AudioOutput getOutput()
  {
    return _output;
  }

  /**
   * Indicates if this thread is active or not.
   * @return <code>true</code> if it is, code>false</code> otherwise.
   */
  public boolean isActive()
  {
    return _active;
  }

  /**
   * Get the current sample position.
   * @return the current sample position.
   */
  public long getCurrentSample()
  {
    if (_format!=null)
    {
      return AudioMath.bytesToSamples(_currentByte,_format.getFrameSize());
    }
    return 0;
  }
}
