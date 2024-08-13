package delta.soundplayer.externals.codecs;

import java.util.ArrayList;

import delta.soundplayer.api.SoundFormat;
import delta.soundplayer.externals.codecs.oggvorbis.OggVorbisFileIdentifier;
import delta.soundplayer.externals.codecs.pcm.WavFileIdentifier;
import delta.soundplayer.externals.utils.Util;

/**
 * Factory for audio files identifiers.
 * @author DAM
 */
public class Identifiers
{
  private static ArrayList<AudioFileIdentifier> readers;

  static
  {
    readers=new ArrayList<AudioFileIdentifier>();
    readers.add(new OggVorbisFileIdentifier());
    readers.add(new WavFileIdentifier());
  }

  /**
   * Get a decoder suitable for the given sound format.
   * @param format Sound format.
   * @return An identifier or <code>null</code>.
   */
  public static AudioFileIdentifier getIdentifier(SoundFormat format)
  {
    if (format==SoundFormat.OGG_VORBIS) return new OggVorbisFileIdentifier();
    if (format==SoundFormat.WAV) return new WavFileIdentifier();
    return null;
  }

  /**
   * Get a decoder suitable for the given file name.
   * @param fileName Filename.
   * @return An identifier or <code>null</code> if not found.
   */
  public static AudioFileIdentifier getIdentifier(String fileName)
  {
    String ext=Util.getFileExt(fileName);
    for(AudioFileIdentifier reader:readers)
    {
      if (reader.isFileSupported(ext))
      {
        return reader;
      }
    }
    return null;
  }
}
