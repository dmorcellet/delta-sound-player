package delta.soundplayer.externals.codecs;

import java.io.File;

import delta.soundplayer.externals.data.Track;

/**
 * Audio file identifier.
 * <br>
 * Reads an audio file to load track data:
 * <ul>
 * <li>channels count,
 * <li>sample rate,
 * <li>total samples count,
 * <li>optionally: bit rate (if constant).
 * </ul>>
 * @author DAM
 */
public abstract class AudioFileIdentifier
{
  /**
   * Identify an audio file.
   * @param input Input file.
   * @return the identified track.
   */
  public abstract Track identify(File input);

  /**
   * Indicates if this identifier supports files with the given extension.
   * @param extension File extension.
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  public abstract boolean isFileSupported(String extension);
}
