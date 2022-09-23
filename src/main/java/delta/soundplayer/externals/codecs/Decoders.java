package delta.soundplayer.externals.codecs;

import java.io.File;
import java.util.HashMap;

import delta.soundplayer.externals.codecs.oggvorbis.OggVorbisFileDecoder;
import delta.soundplayer.externals.codecs.pcm.WavFileDecoder;
import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.utils.Util;

/**
 * Factory for audio files decoders.
 * @author DAM
 */
public class Decoders
{
  private static HashMap<String,AudioFileDecoder> decoders=new HashMap<String,AudioFileDecoder>();

  static
  {
    decoders.put("ogg",new OggVorbisFileDecoder());
    decoders.put("wav",new WavFileDecoder());
  }

  /**
   * Get a decoder suitable for the given track.
   * @param track Track to use.
   * @return A decoder or <code>null</code> if not found.
   */
  public static AudioFileDecoder getDecoder(Track track)
  {
    File location=track.getFile();
    String ext=Util.getFileExt(location.getName()).toLowerCase();
    return decoders.get(ext);
  }
}
