package delta.soundplayer.api;

/**
 * Interface of the sound player.
 * @author DAM
 */
public interface SoundPlayer
{
  /**
   * Set the sound.
   * @param sound Sound to set.
   */
  void setSound(Sound sound);

  /**
   * Start play-back.
   */
  void start();

  /**
   * Stop play-back.
   */
  void stop();
}
