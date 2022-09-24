package delta.soundplayer.externals.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for udio output.
 * @author DAM
 */
public class AudioOutput
{
  private static final Logger logger=LoggerFactory.getLogger(AudioOutput.class);

  public static final int BUFFER_SIZE=(int)(Math.pow(2,15)/24)*24;

  private SourceDataLine _line;
  private FloatControl _volumeControl;
  private Mixer _mixer;
  private float _volume;
  private boolean _linearVolume;

  /**
   * Constructor.
   */
  public AudioOutput() {
    _volume=1f;
    _linearVolume=false;
  }

  /**
   * Initialized output for the given audio format.
   * @param audioFormat
   * @throws LineUnavailableException
   */
  public void init(AudioFormat audioFormat) throws LineUnavailableException
  {
    // If it is same format and the line is opened, do nothing
    if (_line!=null&&_line.isOpen())
    {
      if (!_line.getFormat().matches(audioFormat))
      {
        _line.drain();
        _line.close();
        _line=null;
      }
      else
      {
        return;
      }
    }
    logger.debug("Audio format: {}", audioFormat);
    DataLine.Info info=new DataLine.Info(SourceDataLine.class,audioFormat,BUFFER_SIZE);
    logger.debug("Dataline info: {}", info);
    if (_mixer!=null&&_mixer.isLineSupported(info))
    {
      _line=(SourceDataLine)_mixer.getLine(info);
      logger.debug("Mixer: {}", _mixer.getMixerInfo().getDescription());
    }
    else
    {
      _line=AudioSystem.getSourceDataLine(audioFormat);
      _mixer=null;
    }
    logger.debug("Line: {}", _line);
    _line.open(audioFormat,BUFFER_SIZE);
    _line.start();
    if (_line.isControlSupported(FloatControl.Type.VOLUME))
    {
      _volumeControl=(FloatControl)_line.getControl(FloatControl.Type.VOLUME);
      _volumeControl.setValue(_volume*_volumeControl.getMaximum());
      _linearVolume=true;
    }
    else if (_line.isControlSupported(FloatControl.Type.MASTER_GAIN))
    {
      _volumeControl=(FloatControl)_line.getControl(FloatControl.Type.MASTER_GAIN);
      _volumeControl.setValue(linearToDb(_volume));
      _linearVolume=false;
    }
  }

  /**
   * Stop.
   */
  public void stop()
  {
    if (_line!=null&&_line.isOpen())
     {
      _line.stop();
     }
  }

  /**
   * Start.
   */
  public void start()
  {
    if (_line!=null&&_line.isOpen())
    {
      _line.start();
    }
  }

  /**
   * Close.
   */
  public void close()
  {
    if (_line!=null)
    {
      _line.close();
    }
  }

  /**
   * Indicates if the managed line is opened or not.
   * @return <code>true</code> if it is, <code>false</code> otherwise.
   */
  public boolean isOpen()
  {
    return ((_line!=null) && (_line.isOpen()));
  }

  /**
   * Flush.
   */
  public void flush()
  {
    if (_line!=null&&_line.isOpen())
    {
      _line.flush();
    }
  }

  /**
   * Write some audio data.
   * @param buffer Buffer to write.
   * @param offset Offset of the data to write in this buffer.
   * @param length Length of the data to write.
   */
  public void write(byte[] buffer, int offset, int length)
  {
    _line.write(buffer,offset,length);
  }

  /**
   * Set the volume.
   * @param volume Volume to set.
   */
  public void setVolume(float volume)
  {
    _volume=volume;
    if (_volumeControl!=null)
    {
      if (_linearVolume)
        _volumeControl.setValue(_volumeControl.getMaximum()*volume);
      else
        _volumeControl.setValue(linearToDb(volume));
    }
  }

  public float getVolume()
  {
    if (_volumeControl!=null)
    {
      if (_linearVolume) return this._volumeControl.getValue()/_volumeControl.getMaximum();
      return dbToLinear(_volumeControl.getValue());
    }
    return _volume;
  }

  private static float linearToDb(double volume)
  {
    return (float)(20*Math.log10(volume));
  }

  private static float dbToLinear(double volume)
  {
    return (float)Math.pow(10,volume/20);
  }
}
