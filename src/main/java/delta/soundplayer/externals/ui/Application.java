package delta.soundplayer.externals.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.player.AudioOutput;
import delta.soundplayer.externals.player.AudioPlayer;

public class Application
{
  private static final Logger logger=LoggerFactory.getLogger(Application.class);

  private static Application ourInstance=new Application();

  private AudioPlayer player;
  private MainWindow mainWindow;

  public static Application getInstance()
  {
    return ourInstance;
  }

  private Application()
  {
  }

  public void load()
  {
    player=new AudioPlayer();
    loadSettings();
  }

  private void loadSettings()
  {
    try
    {
      String laf="com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
      UIManager.setLookAndFeel(laf);
    }
    catch (Exception e)
    {
      System.err.println("Could not load LaF: "+e.getCause());
    }
  }

  public void start()
  {
    try
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          if (mainWindow!=null)
          {
            mainWindow.shutdown();
            mainWindow=null;
          }

          mainWindow=new MainWindow();
          mainWindow.setVisible(true);
        }
      });
    }
    catch (Exception e)
    {
      logger.warn("Exception in Application.start()", e);
    }
  }

  public void exit()
  {
    player.stop();

    if (mainWindow!=null)
    {
      mainWindow.shutdown();
    }

    System.exit(0);
  }

  public AudioPlayer getPlayer()
  {
    return player;
  }
}