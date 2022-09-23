package delta.soundplayer.externals.codecs.pcm;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.codecs.AudioFileDecoder;
import delta.soundplayer.externals.data.Track;

/**
 * Decoder for PCM audio files (WAV).
 * @author DAM
 */
public class WavFileDecoder implements AudioFileDecoder
{
  private static final Logger logger=LoggerFactory.getLogger(WavFileDecoder.class);

  private AudioInputStream _audioInputStream;
  private Track _track;

  @Override
  public boolean open(Track track)
  {
    File input=track.getFile();
    try
    {
      logger.debug("Opening file: {}", input);
      _track=track;
      _audioInputStream=AudioSystem.getAudioInputStream(input);
      AudioFormat oldAudioFormat=_audioInputStream.getFormat();
      float sampleRate=oldAudioFormat.getSampleRate();
      int sampleSizeInBits=oldAudioFormat.getSampleSizeInBits();
      int channels=oldAudioFormat.getChannels();
      AudioFormat audioFormat=new AudioFormat(sampleRate,sampleSizeInBits,channels,true,false);
      _audioInputStream=AudioSystem.getAudioInputStream(audioFormat,_audioInputStream);
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
    return _audioInputStream.getFormat();
  }

  @Override
  public void seekSample(long sample)
  {
    open(_track);
    try
    {
      long toSkip=sample*_audioInputStream.getFormat().getFrameSize();
      long skipped=0;
      while (skipped<toSkip)
      {
        long b=_audioInputStream.skip(toSkip-skipped);
        if (b==0) break;
        skipped+=b;
      }
    }
    catch (IOException e)
    {
      File input=_track.getFile();
      logger.error("Got exception when seeking track: "+input+" at sample "+sample, e);
    }
  }

  @Override
  public int decode(byte[] buf)
  {
    try
    {
      return _audioInputStream.read(buf,0,buf.length);
    }
    catch (IOException e)
    {
      File input=_track.getFile();
      logger.error("Got exception when decoding track: "+input, e);
    }
    return -1;
  }

  @Override
  public void close()
  {
    try
    {
      if (_audioInputStream!=null)
      {
        _audioInputStream.close();
      }
    }
    catch (IOException e)
    {
      File input=_track.getFile();
      logger.error("Got exception when closing track: "+input, e);
    }
  }
}
