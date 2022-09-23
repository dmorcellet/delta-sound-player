package delta.soundplayer.externals.data;

import java.io.File;

import delta.soundplayer.externals.utils.Util;

public class Track
{
  private File _inputFile;
  private String _format;
  private int _channels;
  private int _sampleRate;
  private long _totalSamples;
  private int _bitRate;

  // runtime stuff
  private String length;

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

  public File getFile()
  {
    return _inputFile;
  }

  public String getFormat()
  {
    return _format;
  }

  public int getChannels()
  {
    return _channels;
  }

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

  public void setChannels(int channels)
  {
    _channels=channels;
  }

  public int getSampleRate()
  {
    return _sampleRate;
  }

  public void setSampleRate(int sampleRate)
  {
    _sampleRate=sampleRate;
  }

  public long getTotalSamples()
  {
    return _totalSamples;
  }

  public void setTotalSamples(long totalSamples)
  {
    _totalSamples=totalSamples;
    length=null;
  }

  public String getLength()
  {
    if (length==null)
    {
      length=Util.samplesToTime(_totalSamples,_sampleRate,0);
    }
    return length;
  }

  public int getBitrate()
  {
    return _bitRate;
  }

  public void setBitrate(int bitrate)
  {
    _bitRate=bitrate;
  }
}
