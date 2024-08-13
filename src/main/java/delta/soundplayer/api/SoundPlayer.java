package delta.soundplayer.api;

import java.io.File;

/**
 * Interface of the sound player.
 * @author DAM
 */
public interface SoundPlayer
{
  /**
   * Start playing a sound file..
   * @param soundFile Sound file.
   * @param format Sound format.
   */
  void start(File soundFile, SoundFormat format);

  /**
   * Stop play-back.
   */
  void stop();
}
