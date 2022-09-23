package delta.soundplayer.externals.player;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.Actor.Message;

public class AudioPlayer
{
  private static final Logger logger=LoggerFactory.getLogger(AudioPlayer.class);

  private static final int BUFFER_SIZE=(int)Math.pow(2,18);

  private PlayingThread _playingThread;
  private BufferingThread _bufferingThread;
  private ArrayList<PlayerListener> _listeners=new ArrayList<PlayerListener>();

  /**
   * Constructor.
   */
  public AudioPlayer()
  {
    Buffer buffer=new Buffer(BUFFER_SIZE);
    _playingThread=new PlayingThread(this,buffer);
    Thread t1=new Thread(_playingThread,"Playing Thread");
    t1.setPriority(Thread.MAX_PRIORITY);
    t1.start();
    _bufferingThread=new BufferingThread(buffer,_playingThread);
    new Thread(_bufferingThread,"Buffer Thread").start();
  }

  public void open(Track track)
  {
    _bufferingThread.send(Message.OPEN,track);
  }

  public void play()
  {
    if (!isPaused())
    {
      Track track=getTrack();
      if (track!=null)
      {
        _bufferingThread.send(Message.OPEN,track);
      }
    }
  }

  public void pause()
  {
    _playingThread.send(Message.PAUSE);
  }

  public void seek(long sample)
  {
    _bufferingThread.send(Message.SEEK,Long.valueOf(sample));
  }

  public void stop()
  {
    _bufferingThread.send(Message.STOP);
  }

  public AudioOutput getAudioOutput()
  {
    return _playingThread.getOutput();
  }

  public void addListener(PlayerListener listener)
  {
    _listeners.add(listener);
  }

  public void removeListener(PlayerListener listener)
  {
    _listeners.remove(listener);
  }

  public long getCurrentSample()
  {
    return _playingThread.getCurrentSample();
  }

  public Track getTrack()
  {
    return _playingThread.getCurrentTrack();
  }

  public boolean isPlaying()
  {
    return _playingThread.isActive()&&getTrack()!=null;
  }

  public boolean isPaused()
  {
    return !isPlaying()&&!isStopped();
  }

  public boolean isStopped()
  {
    return !_bufferingThread.isActive();
  }

  synchronized void fireEvent(PlayerEvent.PlayerEventCode event)
  {
    logger.debug("Player Event: {}", event);
    PlayerEvent e=new PlayerEvent(event);
    for(PlayerListener listener:_listeners)
    {
      listener.onEvent(e);
    }
  }
}
