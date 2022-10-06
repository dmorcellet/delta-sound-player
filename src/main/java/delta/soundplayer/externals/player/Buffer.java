package delta.soundplayer.externals.player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.data.Track;

/**
 * Buffer.
 * @author DAM
 */
public class Buffer
{
  private static final Logger logger=LoggerFactory.getLogger(Buffer.class);

  private RingBuffer _buffer;
  private BlockingQueue<NextEntry> _trackQueue;
  private Queue<Integer> _when;
  private int _bytesLeft;

  /**
   * Constructor.
   * @param size Buffer size.
   */
  public Buffer(int size)
  {
    _buffer=new RingBuffer(size);
    _trackQueue=new LinkedBlockingDeque<NextEntry>();
    _when=new LinkedList<Integer>();
    _bytesLeft=0;
  }

  /**
   * Write some bytes.
   * @param buffer Buffer to read from.
   * @param offset Offset to read from.
   * @param length Length of data to write.
   */
  public void write(byte[] buffer, int offset, int length)
  {
    _buffer.put(buffer,offset,length);
  }

  /**
   * Add a track.
   * @param track Track to add.
   * @param format Audio format.
   * @param startSample Start sample.
   * @param forced Forced.
   */
  public void addNextTrack(Track track, AudioFormat format, long startSample, boolean forced)
  {
    int bytesLeft=available();
    for(Integer left:_when)
    {
      bytesLeft-=left.intValue();
    }
    if (_trackQueue.isEmpty())
    {
      _bytesLeft=bytesLeft;
    }
    else
    {
      _when.add(Integer.valueOf(bytesLeft));
    }
    _trackQueue.add(new NextEntry(track,format,startSample,forced));
  }

  /**
   * Wait for a next track entry.
   * @return the next track entry or <code>null</code>.
   */
  public NextEntry pollNextTrack()
  {
    NextEntry nextEntry=null;
    try
    {
      nextEntry=_trackQueue.take();
      _buffer.setEOF(false);
    }
    catch (InterruptedException e)
    {
      logger.warn("Interrupted exception!", e);
    }

    if (!_when.isEmpty())
    {
      _bytesLeft=_when.poll().intValue();
    }
    else
    {
      _bytesLeft=-1;
    }
    return nextEntry;
  }

  /**
   * Read some bytes.
   * @param b Buffer for read bytes.
   * @param len Buffer length.
   * @return The number of bytes read
   */
  public int read(byte[] b, int len)
  {
    if (_bytesLeft>0)
    {
      if (_bytesLeft<len)
      {
        len=_bytesLeft;
      }
      _bytesLeft-=len;
    }
    else if (_bytesLeft==0)
    {
      return -1;
    }
    return _buffer.get(b,0,len);
  }

  /**
   * Get the number of available bytes.
   * @return a number of bytes.
   */
  public synchronized int available()
  {
    return _buffer.getAvailable();
  }

  /**
   * Get the size of the managed buffer. 
   * @return a size in bytes.
   */
  public int size()
  {
    return _buffer.size();
  }

  /**
   * Flush.
   */
  public void flush()
  {
    _buffer.empty();
  }

  /**
   * Next entry.
   * @author DAM
   */
  public class NextEntry
  {
    /**
     * Track.
     */
    public Track _track;
    /**
     * Format.
     */
    public AudioFormat _format;
    /**
     * Start sample.
     */
    public long _startSample;
    /**
     * Forced.
     */
    public boolean _forced;

    NextEntry(Track track, AudioFormat format, long startSample, boolean forced)
    {
      _track=track;
      _format=format;
      _startSample=startSample;
      _forced=forced;
    }
  }
}
