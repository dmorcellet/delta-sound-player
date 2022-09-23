package delta.soundplayer.externals.player;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for buffering and plying threads.
 */
public abstract class Actor
{
  private static final Logger logger=LoggerFactory.getLogger(Actor.class);

  /**
   * Message types.
   */
  public enum Message
  {
    // player messages
    /**
     * Play.
     */
    PLAY,
    /**
     * Pause.
     */
    PAUSE,
    /**
     * Stop.
     */
    STOP,
    /**
     * Flush.
     */
    FLUSH,
    // buffer messages
    /**
     * Open.
     */
    OPEN,
    /**
     * Seek.
     */
    SEEK;

    private Object[] _params;

    /**
     * Get message parameters.
     * @return message parameters (may be <code>null</code>).
     */
    public Object[] getParams()
    {
      return _params;
    }

    public void setParams(Object[] params)
    {
      this._params=params;
    }
  }

  private BlockingQueue<Message> queue=new LinkedBlockingDeque<Message>();

  public synchronized void send(Message message, Object... params)
  {
    message.setParams(params);
    queue.add(message);
  }

  protected Actor()
  {
    Thread messageThread=new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        while (true)
        {
          Message message=null;
          try
          {
            message=queue.take();
            process(message);
          }
          catch (InterruptedException e)
          {
            logger.warn("Interrupted exception!", e);
            break;
          }
          catch (Exception e)
          {
            logger.warn("Error processing message "+message,e);
          }
        }
      }
    },"Actor Thread");
    messageThread.start();
  }

  protected abstract void process(Message message);
}
