package delta.soundplayer.externals.utils;

/**
 * Misc. utilities.
 * @author DAM
 */
public class Util
{
  /**
   * Remove extension from a file name.
   * @param s Input file name.
   * @return the file name without its extension (if any).
   */
  public static String removeExt(String s)
  {
    int index=s.lastIndexOf(".");
    if (index==-1)
    {
      index=s.length();
    }
    return s.substring(0,index);
  }

  /**
   * Get the file extension.
   * @param fileName Input file name.
   * @return An extension (without ".") or empty string if none.
   */
  public static String getFileExt(String fileName)
  {
    int pos=fileName.lastIndexOf(".");
    if (pos==-1) return "";
    return fileName.substring(pos+1).toLowerCase();
  }

  /**
   * Get a displayable time position from samples count and samples rate.
   * @param samples Samples count.
   * @param sampleRate Samples rate.
   * @return A displayable time.
   */
  public static String samplesToTime(long samples, int sampleRate)
  {
    if (samples<=0)
    {
      return "-:--";
    }
    double seconds=AudioMath.samplesToMillis(samples,sampleRate)/1000f;
    return formatSeconds(seconds);
  }

  private static String formatSeconds(double seconds)
  {
    int min=(int)((Math.round(seconds))/60);
    int hrs=min/60;
    if (min>0) seconds-=min*60;
    if (seconds<0) seconds=0;
    if (hrs>0) min-=hrs*60;
    int days=hrs/24;
    if (days>0) hrs-=days*24;
    int weeks=days/7;
    if (weeks>0) days-=weeks*7;

    StringBuilder builder=new StringBuilder();
    if (weeks>0) builder.append(weeks).append("wk ");
    if (days>0) builder.append(days).append("d ");
    if (hrs>0) builder.append(hrs).append(":");
    if (hrs>0&&min<10) builder.append("0");
    builder.append(min).append(":");
    int sec=(int)seconds;
    if (sec<10) builder.append("0");
    builder.append(Math.round(sec));
    return builder.toString();
  }
}
