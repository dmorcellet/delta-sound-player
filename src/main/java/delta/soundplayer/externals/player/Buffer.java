package delta.soundplayer.externals.player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.data.Track;

public class Buffer
{
  private static final Logger logger=LoggerFactory.getLogger(Buffer.class);

  private RingBuffer _buffer;
  private BlockingQueue<NextEntry> _trackQueue=new LinkedBlockingDeque<NextEntry>();
  private Queue<Integer> _when=new LinkedList<Integer>();
  private int _bytesLeft=0;

  public Buffer(int size)
  {
    _buffer=new RingBuffer(size);
  }

  public void write(byte[] b, int off, int len)
  {
    _buffer.put(b,off,len);
  }

  public void addNextTrack(Track track, AudioFormat format, long startSample, boolean forced)
  {
    int bytesLeft=available();
    for(Integer left:_when)
    {
      bytesLeft-=left.intValue();
    }
    if (_trackQueue.isEmpty())
      this._bytesLeft=bytesLeft;
    else
      _when.add(Integer.valueOf(bytesLeft));
    _trackQueue.add(new NextEntry(track,format,startSample,forced));
  }

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

  public int read(byte[] b, int off, int len)
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
    return _buffer.get(b,off,len);
  }

  public synchronized int available()
  {
    return _buffer.getAvailable();
  }

  public int size()
  {
    return _buffer.size();
  }

  public void flush()
  {
    _buffer.empty();
  }

  public class NextEntry
  {
    public Track _track;
    public AudioFormat _format;
    public long _startSample;
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
