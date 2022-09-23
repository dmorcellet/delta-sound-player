package delta.soundplayer.externals.codecs;

import javax.sound.sampled.AudioFormat;

import delta.soundplayer.externals.data.Track;

/**
 * Audio file decoder.
 */
public interface AudioFileDecoder
{
  /**
   * Open the file and prepare for decoding.
   * @param track The Track to open.
   * @return <code>true</code> if successful, <code>false</code> otherwise;
   */
  boolean open(Track track);

  /**
   * Get format of the PCM data.<br>
   * Usually it is 44100 kHz, 16 bit, signed, little or big endian.
   * @return the audio format of PCM data.
   */
  AudioFormat getAudioFormat();

  /**
   * Seek a sample.
   * @param sample Sample to seek.
   */
  void seekSample(long sample);

  /**
   * Decode a chunk of PCM data and write it to the given buffer.
   * @param buffer Buffer for data.
   * @return <code>true</code> if success, <code>false</code> otherwise.
   */
  int decode(byte[] buffer);

  /**
   * Close.
   */
  void close();
}
