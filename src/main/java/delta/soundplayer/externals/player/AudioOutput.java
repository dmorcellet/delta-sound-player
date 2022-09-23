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

public class AudioOutput
{
  private static final Logger logger=LoggerFactory.getLogger(AudioOutput.class);

  public static final int BUFFER_SIZE=(int)(Math.pow(2,15)/24)*24;

  private SourceDataLine line;
  private FloatControl volumeControl;
  private boolean mixerChanged;
  private Mixer mixer;
  private float _volume=1f;
  private boolean linearVolume=false;

  public AudioOutput() {
    
  }

  public void init(AudioFormat fmt) throws LineUnavailableException
  {
    // if it is same format and the line is opened, do nothing
    if (line!=null&&line.isOpen())
    {
      if (mixerChanged||!line.getFormat().matches(fmt))
      {
        mixerChanged=false;
        line.drain();
        line.close();
        line=null;
      }
      else
      {
        return;
      }
    }
    logger.debug("Audio format: {}", fmt);
    DataLine.Info info=new DataLine.Info(SourceDataLine.class,fmt,BUFFER_SIZE);
    logger.debug("Dataline info: {}", info);
    if (mixer!=null&&mixer.isLineSupported(info))
    {
      line=(SourceDataLine)mixer.getLine(info);
      logger.debug("Mixer: {}"+mixer.getMixerInfo().getDescription());
    }
    else
    {
      line=AudioSystem.getSourceDataLine(fmt);
      mixer=null;
    }
    logger.debug("Line: {}", line);
    line.open(fmt,BUFFER_SIZE);
    line.start();
    if (line.isControlSupported(FloatControl.Type.VOLUME))
    {
      volumeControl=(FloatControl)line.getControl(FloatControl.Type.VOLUME);
      volumeControl.setValue(_volume*volumeControl.getMaximum());
      linearVolume=true;
    }
    else if (line.isControlSupported(FloatControl.Type.MASTER_GAIN))
    {
      volumeControl=(FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
      volumeControl.setValue(linearToDb(_volume));
      linearVolume=false;
    }
  }

  public void stop()
  {
    if (line!=null&&line.isOpen()) line.stop();
  }

  public void start()
  {
    if (line!=null&&line.isOpen()) line.start();
  }

  public void close()
  {
    if (line!=null)
    {
      line.close();
    }
  }

  public boolean isOpen()
  {
    return line!=null&&line.isOpen();
  }

  public void flush()
  {
    if (line!=null&&line.isOpen()) line.flush();
  }

  public void write(byte[] buf, int offset, int len)
  {
    line.write(buf,offset,len);
  }

  public void setVolume(float volume)
  {
    this._volume=volume;
    if (volumeControl!=null)
    {
      if (linearVolume)
        volumeControl.setValue(volumeControl.getMaximum()*volume);
      else
        volumeControl.setValue(linearToDb(volume));
    }
  }

  public float getVolume(boolean actual)
  {
    if (actual&&volumeControl!=null)
    {
      if (linearVolume) return this.volumeControl.getValue()/volumeControl.getMaximum();
      return dbToLinear(volumeControl.getValue());
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
