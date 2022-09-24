package delta.soundplayer.externals.data;

import java.io.File;

import delta.soundplayer.externals.utils.Util;

/**
 * Gather descriptive data about an audio file.
 * @author DAM
 */
public class Track
{
  private File _inputFile;
  private String _format;
  private int _channels;
  private int _sampleRate;
  private long _totalSamples;
  private int _bitRate;
  // Runtime
  private String _length;

  /**
   * Constructor.
   * @param input Input file.
   * @param format Format.
   */
  public Track(File input, String format)
  {
    _inputFile=input;
    _format=format;
  }

  /**
   * Get the managed file.
   * @return the managed file.
   */
  public File getFile()
  {
    return _inputFile;
  }

  /**
   * Get the format of the audio file.
   * @return a format identifier.
   */
  public String getFormat()
  {
    return _format;
  }

  /**
   * Get the number of channels.
   * @return a channels count.
   */
  public int getChannels()
  {
    return _channels;
  }

  /**
   * Get a displayable label for the number of channels.
   * @return a displayable label.
   */
  public String getChannelsAsString()
  {
    switch (getChannels())
    {
      case 1:
        return "Mono";
      case 2:
        return "Stereo";
      default:
        return getChannels()+" ch";
    }
  }

  /**
   * Set the channels count.
   * @param channels Number of channels to set.
   */
  public void setChannels(int channels)
  {
    _channels=channels;
  }

  /**
   * Get the sample rate.
   * @return A number of samples per second.
   */
  public int getSampleRate()
  {
    return _sampleRate;
  }

  /**
   * Set the sample rate.
   * @param sampleRate Rate to set (samples/second).
   */
  public void setSampleRate(int sampleRate)
  {
    _sampleRate=sampleRate;
  }

  /**
   * Get the total number of samples.
   * @return A samples count.
   */
  public long getTotalSamples()
  {
    return _totalSamples;
  }

  /**
   * Set the total number of samples.
   * @param totalSamples Samples count to set.
   */
  public void setTotalSamples(long totalSamples)
  {
    _totalSamples=totalSamples;
    _length=null;
  }

  /**
   * Get a displayable label for the duration of the audio file.
   * @return A displayable duration label.
   */
  public String getLength()
  {
    if (_length==null)
    {
      _length=Util.samplesToTime(_totalSamples,_sampleRate,0);
    }
    return _length;
  }

  /**
   * Get the current bit rate.
   * @return A bit rate (kilobits per second).
   */
  public int getBitrate()
  {
    return _bitRate;
  }

  /**
   * Set the current bit rate.
   * @param bitrate Value to set (kilobits per second).
   */
  public void setBitrate(int bitrate)
  {
    _bitRate=bitrate;
  }
}
