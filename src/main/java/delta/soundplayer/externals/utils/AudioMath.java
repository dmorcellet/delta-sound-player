package delta.soundplayer.externals.utils;

/**
 * Math methods for audio.
 * @authorDAM
 */
public class AudioMath
{
  /**
   * Get a samples count from a bytes count.
   * @param bytes Bytes count.
   * @param frameSize Frame size (in bytes).
   * @return A samples count.
   */
  public static long bytesToSamples(long bytes, int frameSize)
  {
    return bytes/frameSize;
  }

  /**
   * Get a bytes count from a samples count.
   * @param samples Samples count.
   * @param frameSize Frame size (in bytes).
   * @return A bytes count.
   */
  public static long samplesToBytes(long samples, int frameSize)
  {
    return samples*frameSize;
  }

  /**
   * Get a duration in milliseconds from a samples count and a sample rate.
   * @param samples Samples count.
   * @param sampleRate Sample rate (samples/second).
   * @return A duration in milliseconds.
   */
  public static double samplesToMillis(long samples, int sampleRate)
  {
    return (double)samples/sampleRate*1000;
  }
}
