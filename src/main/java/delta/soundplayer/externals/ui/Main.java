package delta.soundplayer.externals.ui;

/**
 * Main for the player app.
 * @author DAM
 */
public class Main
{
  /**
   * Main method for this application.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    Application app=Application.getInstance();
    app.load();
    app.start();
  }
}
