package delta.soundplayer.externals.player;

/**
 * Interface of a listener for player events.
 * @author DAM
 */
public interface PlayerListener
{
  /**
   * Method called when a player event occurred.
   * @param e Event.
   */
  void onEvent(PlayerEvent e);
}
