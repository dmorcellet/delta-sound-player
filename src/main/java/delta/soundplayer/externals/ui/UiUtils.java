package delta.soundplayer.externals.ui;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.utils.Util;

/**
 * UI utilities.
 * @author DAM
 */
public class UiUtils
{
  /**
   * Get a string to display the current playing time.
   * @param player Player to use.
   * @param track Track to use.
   * @return A displayable string.
   */
  public static String playingTime(AudioPlayer player, Track track)
  {
    if (player.isPlaying())
    {
      long sample=player.getCurrentSample();
      int sampleRate=player.getTrack().getSampleRate();
      return Util.samplesToTime(sample,sampleRate);
    }
    return null;
  }
}
