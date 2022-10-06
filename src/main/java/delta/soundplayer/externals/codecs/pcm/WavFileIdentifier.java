package delta.soundplayer.externals.codecs.pcm;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.codecs.AudioFileIdentifier;
import delta.soundplayer.externals.data.Track;

/**
 * Identifier for WAV audio files.
 * @author DAM
 */
public class WavFileIdentifier extends AudioFileIdentifier
{
  private static final Logger logger=LoggerFactory.getLogger(WavFileIdentifier.class);

  @Override
  public Track identify(File input)
  {
    Track track=new Track(input,"WAV");
    try
    {
      AudioFileFormat format=AudioSystem.getAudioFileFormat(input);
      AudioFormat audioFormat=format.getFormat();
      track.setSampleRate((int)audioFormat.getSampleRate());
      track.setTotalSamples(format.getFrameLength());
      track.setChannels(audioFormat.getChannels());
      if (format.getFrameLength()>0)
      {
        int byteLength=format.getByteLength();
        int frameLength=format.getFrameLength();
        float sampleRate=audioFormat.getSampleRate(); // samples/s
        float sampleBits=(((float)byteLength)/frameLength)*8; // bits/sample
        int bitRate=(int)((sampleBits*sampleRate)/1000); // kbit/s
        track.setBitrate(bitRate);
      }
    }
    catch (Exception e)
    {
      logger.warn("Couldn't read file: "+track.getFile(),e);
    }
    return track;
  }

  public boolean isFileSupported(String ext)
  {
    return ext.equalsIgnoreCase("wav");
  }
}
