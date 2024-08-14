package delta.soundplayer.externals.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.codecs.AudioFileDecoder;
import delta.soundplayer.externals.codecs.Decoders;
import delta.soundplayer.externals.data.Track;

/**
 * Buffering thread.
 * @author DAM
 */
public class BufferingRunnable extends Actor implements Runnable
{
  private static final Logger logger=LoggerFactory.getLogger(BufferingRunnable.class);

  private final Object lock=new Object();
  private Track currentTrack;
  private AudioFileDecoder decoder;
  private boolean active;

  private Buffer _buffer;
  private PlayingRunnable _playingRunnable;

  /**
   * Buffering thread.
   * @param buffer Managed buffer.
   * @param playingRunnable Playing runnable.
   */
  public BufferingRunnable(Buffer buffer, PlayingRunnable playingRunnable)
  {
    _buffer=buffer;
    _playingRunnable=playingRunnable;
  }

  @Override
  public void process(Message message)
  {
    Object[] params=message.getParams();
    switch (message)
    {
      case OPEN:
        if (params.length>0&&params[0] instanceof Track)
        {
          Track track=(Track)params[0];
          pause(true);
          open(track);
        }
      break;
      case SEEK:
        if (params.length>0&&params[0] instanceof Long)
        {
          Long sample=(Long)params[0];
          seek(sample.longValue());
        }
      break;
      case STOP:
        stop(true);
      break;
      default:
      break;
    }
  }

  @Override
  public void run()
  {
    byte[] buf=new byte[65536];
    int len;
    while (true)
    {
      synchronized (lock)
      {
        try
        {
          while (!active)
          {
            lock.wait();
          }
          if (decoder==null)
          {
            stop(false);
            continue;
          }

          while (active)
          {
            len=decoder.decode(buf);

            if (len==-1)
            {
              stop(false);
              continue;
            }

            _buffer.write(buf,0,len);
          }
        }
        catch (Exception e)
        {
          logger.warn("Exception in BufferingThread.run()",e);
        }
      }
    }
  }

  /**
   * Stop.
   * @param flush Flush or not.
   */
  public void stop(boolean flush)
  {
    logger.debug("Stop buffering");
    pause(flush);
    _buffer.addNextTrack(null,null,-1,false);
    if (decoder!=null)
    {
      decoder.close();
    }
    decoder=null;
  }

  private void pause(boolean flush)
  {
    active=false;
    if (flush) _buffer.flush();
    synchronized (lock)
    {
      // Nothing!
    }
    if (flush) _buffer.flush();
  }

  private void start()
  {
    active=true;
    synchronized (lock)
    {
      lock.notifyAll();
    }
  }

  private synchronized void open(Track track)
  {
    if (decoder!=null)
    {
      decoder.close();
    }

    if (track!=null)
    {
      logger.debug("Opening track {}",track.getFile());

      if (!track.getFile().exists())
      {
        stop(false);
        return;
      }
      decoder=Decoders.getDecoder(track);
      currentTrack=track;

      if (decoder==null||!decoder.open(track))
      {
        currentTrack=null;
        stop(false);
        return;
      }

      _buffer.addNextTrack(currentTrack,decoder.getAudioFormat(),-1,true);

      start();
      logger.debug("Finished opening track");
      _playingRunnable.send(Message.FLUSH);
      _playingRunnable.send(Message.PLAY);
    }
  }

  /**
   * Seek.
   * @param sample Sample to seek.
   */
  public void seek(long sample)
  {
    boolean oldState=active;
    pause(true);

    if (decoder!=null)
    {
      decoder.seekSample(sample);
      _buffer.addNextTrack(currentTrack,decoder.getAudioFormat(),sample,true);
      if (oldState)
      {
        start();
      }
    }
  }

  /**
   * Indicates if this thread is active or not.
   * @return <code>true</code> if it is, <code>false</code> otherwise.
   */
  public boolean isActive()
  {
    return active;
  }
}
