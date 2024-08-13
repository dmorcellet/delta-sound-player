package delta.soundplayer.externals.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.soundplayer.externals.player.AudioPlayer;

/**
 * Audio player application.
 * @author DAM
 */
public class Application
{
  private static final Logger logger=LoggerFactory.getLogger(Application.class);

  private AudioPlayer _player;
  private MainWindow _mainWindow;

  /**
   * Constructor.
   */
  public Application()
  {
    _player=new AudioPlayer();
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
      logger.warn("Could not load LaF:",e);
    }
  }

  /**
   * Start application.
   */
  public void start()
  {
    try
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          _mainWindow=new MainWindow(_player);
          _mainWindow.setVisible(true);

          _mainWindow.addWindowListener(new WindowAdapter()
          {
            @Override
            public void windowClosing(WindowEvent e)
            {
              exit();
            }
          });
        }
      });
    }
    catch (Exception e)
    {
      logger.warn("Exception in Application.start()",e);
    }
  }

  /**
   * Exit application.
   */
  public void exit()
  {
    _player.stop();

    if (_mainWindow!=null)
    {
      _mainWindow.shutdown();
    }

    System.exit(0);
  }
}
