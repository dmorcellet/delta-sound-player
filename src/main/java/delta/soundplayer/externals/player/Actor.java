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

    /**
     * Set the message parameters.
     * @param params Parameters to set.
     */
    public void setParams(Object[] params)
    {
      _params=params;
    }
  }

  private BlockingQueue<Message> queue=new LinkedBlockingDeque<Message>();

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

  /**
   * Send a message with parameters.
   * @param message Message to send.
   * @param params Associated parameters.
   */
  public synchronized void send(Message message, Object... params)
  {
    message.setParams(params);
    queue.add(message);
  }

  protected abstract void process(Message message);
}
