package delta.soundplayer.externals.player;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.Actor.Message;

/**
 * Audio player.
 * @author DAM
 */
public class AudioPlayer
{
  private static final Logger logger=LoggerFactory.getLogger(AudioPlayer.class);

  private static final int BUFFER_SIZE=(int)Math.pow(2,18);

  private PlayingThread _playingThread;
  private BufferingThread _bufferingThread;
  private ArrayList<PlayerListener> _listeners;

  /**
   * Constructor.
   */
  public AudioPlayer()
  {
    _listeners=new ArrayList<PlayerListener>();
    Buffer buffer=new Buffer(BUFFER_SIZE);
    _playingThread=new PlayingThread(this,buffer);
    Thread t1=new Thread(_playingThread,"Playing Thread");
    t1.setPriority(Thread.MAX_PRIORITY);
    t1.start();
    _bufferingThread=new BufferingThread(buffer,_playingThread);
    new Thread(_bufferingThread,"Buffer Thread").start();
  }

  /**
   * Open a track.
   * @param track Track to open.
   */
  public void open(Track track)
  {
    _bufferingThread.send(Message.OPEN,track);
  }

  /**
   * Play.
   */
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

  /**
   * Pause.
   */
  public void pause()
  {
    _playingThread.send(Message.PAUSE);
  }

  /**
   * Seek.
   * @param sample Sample to seek.
   */
  public void seek(long sample)
  {
    _bufferingThread.send(Message.SEEK,Long.valueOf(sample));
  }

  /**
   * Stop.
   */
  public void stop()
  {
    _bufferingThread.send(Message.STOP);
  }

  /**
   * Get the audio output.
   * @return the audio output.
   */
  public AudioOutput getAudioOutput()
  {
    return _playingThread.getOutput();
  }

  /**
   * Add a listener for player events.
   * @param listener Listener to add.
   */
  public void addListener(PlayerListener listener)
  {
    _listeners.add(listener);
  }

  /**
   * Remove a listener for player events.
   * @param listener Listener to remove.
   */
  public void removeListener(PlayerListener listener)
  {
    _listeners.remove(listener);
  }

  /**
   * Get the current sample.
   * @return the current sample.
   */
  public long getCurrentSample()
  {
    return _playingThread.getCurrentSample();
  }

  /**
   * Get the current track.
   * @return the current track.
   */
  public Track getTrack()
  {
    return _playingThread.getCurrentTrack();
  }

  /**
   * Indicates if this player is playing or not.
   * @return <code>true</code> if it is playing, <code>false</code> otherwise.
   */
  public boolean isPlaying()
  {
    return _playingThread.isActive()&&getTrack()!=null;
  }

  /**
   * Indicates if this player is paused or not.
   * @return <code>true</code> if it is paused, <code>false</code> otherwise.
   */
  public boolean isPaused()
  {
    return !isPlaying()&&!isStopped();
  }

  /**
   * Indicates if this player is stopped or not.
   * @return <code>true</code> if it is stopped, <code>false</code> otherwise.
   */
  public boolean isStopped()
  {
    return !_bufferingThread.isActive();
  }

  /**
   * Fire an event.
   * @param event Event to fire.
   */
  synchronized void fireEvent(PlayerEvent.PlayerEventCode event)
  {
    logger.debug("Player Event: {}",event);
    PlayerEvent e=new PlayerEvent(event);
    for(PlayerListener listener:_listeners)
    {
      listener.onEvent(e);
    }
  }
}
