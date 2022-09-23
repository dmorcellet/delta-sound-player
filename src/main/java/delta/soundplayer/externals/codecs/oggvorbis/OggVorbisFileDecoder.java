package delta.soundplayer.externals.codecs.oggvorbis;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.VorbisFile;

import delta.soundplayer.externals.codecs.AudioFileDecoder;
import delta.soundplayer.externals.data.Track;

/**
 * Decoder for Ogg/Vorbis audio files.
 * @author DAM
 */
public class OggVorbisFileDecoder implements AudioFileDecoder
{
  private static final Logger logger=LoggerFactory.getLogger(OggVorbisFileDecoder.class);

  private VorbisFile _vorbisFile;
  private AudioFormat _audioFormat;
  private Track _track;

  public boolean open(Track track)
  {
    File input=track.getFile();
    try
    {
      logger.debug("Opening file: {}", input);
      _track=track;
      _vorbisFile=new VorbisFile(input.getAbsolutePath());
      Info info=_vorbisFile.getInfo()[0];
      track.setSampleRate(info.rate);
      track.setChannels(info.channels);
      _audioFormat=new AudioFormat(info.rate,16,info.channels,true,false);
    }
    catch (Exception e)
    {
      logger.error("Got exception when opening track: "+input, e);
      return false;
    }
    return true;
  }

  @Override
  public AudioFormat getAudioFormat()
  {
    return _audioFormat;
  }

  @Override
  public void seekSample(long sample)
  {
    int ok=_vorbisFile.pcm_seek(sample);
    if (ok!=0)
    {
      File input=_track.getFile();
      logger.error("Got error when seeking track: {} at sample {} => {}", input, Long.valueOf(sample), Integer.valueOf(ok));
    }
  }

  @Override
  public int decode(byte[] buf)
  {
    int ret=_vorbisFile.read(buf,buf.length);
    if (ret<=0)
    {
      return -1;
    }
    // Update bit rate
    _track.setBitrate(_vorbisFile.bitrate_instant()/1000);
    return ret;
  }

  public void close()
  {
    try
    {
      if (_vorbisFile!=null)
      {
        _vorbisFile.close();
      }
    }
    catch (IOException e)
    {
      File input=_track.getFile();
      logger.error("Got exception when closing track: "+input, e);
    }
  }
}
