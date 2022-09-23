package delta.soundplayer.externals.ui;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.utils.Util;

/**
 * @author dmorcellet
 */
public class UiUtils
{
  public static String playingTime(AudioPlayer player, Track track) {
    if (player.isPlaying()) {
        return Util.samplesToTime(player.getCurrentSample(), player.getTrack().getSampleRate(), 0);
    }
    return null;
  }
}
