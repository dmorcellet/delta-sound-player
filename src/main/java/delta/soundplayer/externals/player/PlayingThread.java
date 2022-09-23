package delta.soundplayer.externals.player;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.PlayerEvent.PlayerEventCode;
import delta.soundplayer.externals.utils.AudioMath;

public class PlayingThread extends Actor implements Runnable
{
  private static final Logger logger=LoggerFactory.getLogger(PlayingThread.class);

  private static final int BUFFER_SIZE=AudioOutput.BUFFER_SIZE;

  private AudioFormat format;
  private AudioPlayer _player;
  private Buffer _buffer;
  private final Object lock=new Object();
  private AudioOutput output=new AudioOutput();
  private Track currentTrack;
  private long currentByte;
  private boolean active=false;

  public PlayingThread(AudioPlayer player, Buffer buffer)
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
        setState(!active);
      break;
      case PLAY:
        setState(true);
      break;
      case STOP:
        stop();
      break;
      case FLUSH:
        output.flush();
      break;
      default:
      break;
    }
  }

  private void stop()
  {
    output.flush();
    setState(false);
    output.close();
    _player.fireEvent(PlayerEventCode.STOPPED);
  }

  private void setState(boolean newState)
  {
    if (active!=newState)
    {
      active=newState;
      synchronized (lock)
      {
        lock.notifyAll();
      }
    }
  }

  @Override
  public void run()
  {
    byte[] buf=new byte[BUFFER_SIZE];
    while (true)
    {
      synchronized (lock)
      {
        try
        {
          while (!active)
          {
            if (output.isOpen())
            {
              _player.fireEvent(PlayerEventCode.PAUSED);
            }
            output.stop();
            System.gc();
            lock.wait();
          }

          output.start();
          _player.fireEvent(PlayerEventCode.PLAYING_STARTED);
          out: while (active)
          {
            int len=_buffer.read(buf,0,BUFFER_SIZE);
            while (len==-1)
            {
              if (!openNext())
              {
                stop();
                break out;
              }
              len=_buffer.read(buf,0,BUFFER_SIZE);
            }
            currentByte+=len;
            output.write(buf,0,len);
          }
        }
        catch (Exception e)
        {
          logger.warn("Exception while playing. Stopping now",e);
          currentTrack=null;
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
      currentTrack=nextEntry._track;
      if (nextEntry._forced)
      {
        output.flush();
      }
      format=nextEntry._format;
      output.init(format);
      if (nextEntry._startSample>=0)
      {
        currentByte=AudioMath.samplesToBytes(nextEntry._startSample,format.getFrameSize());
        _player.fireEvent(PlayerEventCode.SEEK_FINISHED);
      }
      else
      {
        currentByte=0;
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

  public Track getCurrentTrack()
  {
    return currentTrack;
  }

  public AudioOutput getOutput()
  {
    return output;
  }

  public boolean isActive()
  {
    return active;
  }

  public long getCurrentSample()
  {
    if (format!=null)
    {
      return AudioMath.bytesToSamples(currentByte,format.getFrameSize());
    }
    return 0;
  }
}
