package delta.soundplayer.externals.player;

public class PlayerEvent
{
  /**
   * Player event codes.
   * @author DAM
   */
  public enum PlayerEventCode
  {
    FILE_OPENED, PLAYING_STARTED, PAUSED, STOPPED, SEEK_FINISHED
  }

  private PlayerEventCode _eventCode;

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
