package delta.soundplayer.api;

/**
 * Sound description.
 * @author DAM
 */
public interface Sound
{
  /**
   * Get the name of this sound.
   * @return a displayable name.
   */
  String getName();

  /**
   * Get the duration of this sound.
   * @return a duration in milliseconds.
   */
  long getDuration();
}
