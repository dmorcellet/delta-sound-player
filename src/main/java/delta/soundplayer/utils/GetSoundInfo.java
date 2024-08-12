package delta.soundplayer.utils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

/**
 * Dump sound system info.
 * @author DAM
 */
public class GetSoundInfo
{
  private void doIt()
  {
    showMixers();
    showLines();
  }

  private void showMixers()
  {
    Mixer.Info[] mixerInfos=AudioSystem.getMixerInfo();
    if (mixerInfos!=null)
    {
      for(Mixer.Info mixerInfo:mixerInfos)
      {
        System.out.println("Mixer:");
        String name=mixerInfo.getName();
        System.out.println("\tName: "+name);
        String vendor=mixerInfo.getVendor();
        System.out.println("\tVendor: "+vendor);
        String version=mixerInfo.getVersion();
        System.out.println("\tVersion: "+version);
        String description=mixerInfo.getDescription();
        System.out.println("\tDescription: "+description);
        Mixer mixer=AudioSystem.getMixer(mixerInfo);
        showMixer(mixer);
      }
    }
  }

  private void showMixer(Mixer mixer)
  {
    Line.Info[] infos=mixer.getTargetLineInfo();
    for(Line.Info info:infos)
    {
      System.out.println("Target line: "+info);
    }
    Line.Info[] sourceLineInfos=mixer.getSourceLineInfo();
    for(Line.Info info:sourceLineInfos)
    {
      System.out.println("Source line: "+info);
    }
  }

  private void showLines()
  {
    Line.Info template=new Line.Info(DataLine.class);
    Line.Info[] infos=AudioSystem.getTargetLineInfo(template);
    for(Line.Info info:infos)
    {
      System.out.println(info);
    }
    /*
     * Port.Info.SPEAKER; Port.Info.LINE_OUT; Port.Info.HEADPHONE;
     */
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    new GetSoundInfo().doIt();
  }
}
