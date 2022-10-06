package delta.soundplayer.externals.player;

/**
 * Player event.
 * @author DAM
 */
public class PlayerEvent
{
  /**
   * Player event codes.
   * @author DAM
   */
  public enum PlayerEventCode
  {
    /**
     * File opened.
     */
    FILE_OPENED,
    /**
     * Audio playing started.
     */
    PLAYING_STARTED,
    /**
     * Paused.
     */
    PAUSED,
    /**
     * Stopped.
     */
    STOPPED,
    /**
     * Seek finished.
     */
    SEEK_FINISHED
  }

  private PlayerEventCode _eventCode;

  /**
   * Constructor.
   * @param eventCode Event code.
   */
  public PlayerEvent(PlayerEventCode eventCode)
  {
    _eventCode=eventCode;
  }

  /**
   * Get the player event code.
   * @return a player event code.
   */
  public PlayerEventCode getEventCode()
  {
    return _eventCode;
  }
}
