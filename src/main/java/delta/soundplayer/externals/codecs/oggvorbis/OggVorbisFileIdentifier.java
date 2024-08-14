package delta.soundplayer.externals.codecs.oggvorbis;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.VorbisFile;

import delta.soundplayer.externals.codecs.AudioFileIdentifier;
import delta.soundplayer.externals.data.Track;

/**
 * Identifier for OGG/Vorbis audio files.
 * @author DAM
 */
public class OggVorbisFileIdentifier extends AudioFileIdentifier
{
  private static final Logger logger=LoggerFactory.getLogger(OggVorbisFileIdentifier.class);

  @Override
  public Track identify(File input)
  {
    Track track=new Track(input,"OGG");
    try
    {
      VorbisFile vorbisFile=new VorbisFile(input.getAbsolutePath());
      Info info=vorbisFile.getInfo()[0];
      track.setSampleRate(info.rate);
      track.setChannels(info.channels);
      long totalSamples=vorbisFile.pcm_total(-1);
      track.setTotalSamples(totalSamples);
      vorbisFile.close();
    }
    catch (Exception e)
    {
      logger.warn("Error during identification of OGG/Vorbis file: "+input,e);
      track=null;
    }
    return track;
  }

  public boolean isFileSupported(String ext)
  {
    return ext.equalsIgnoreCase("ogg");
  }
}
