package delta.soundplayer.externals.codecs;

import java.util.HashMap;

import delta.soundplayer.externals.codecs.oggvorbis.OggVorbisFileDecoder;
import delta.soundplayer.externals.codecs.pcm.WavFileDecoder;
import delta.soundplayer.externals.data.Track;

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
    String format=track.getFormat().toLowerCase();
    return decoders.get(format);
  }
}
