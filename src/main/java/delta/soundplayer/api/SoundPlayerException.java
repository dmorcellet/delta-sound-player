package delta.soundplayer.api;

/**
 * Sound player exception.
 * @author DAM
 */
public class SoundPlayerException extends Exception
{
  /**
   * Constructor.
   * @param message Message.
   * @param cause Cause.
   */
  public SoundPlayerException(String message, Throwable cause)
  {
    super(message,cause);
  }
}
