package delta.soundplayer.externals.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Main window for this player.
 * @author DAM
 */
public class MainWindow extends JFrame
{
  private Application app=Application.getInstance();

  /**
   * Constructor.
   */
  public MainWindow()
  {
    ControlPanel controlPanel=new ControlPanel();
    StatusBar statusBar=new StatusBar();
    add(controlPanel,BorderLayout.NORTH);
    add(statusBar,BorderLayout.SOUTH);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    Rectangle r=new Rectangle(50,0,600,230);
    setLocation((int)r.getX(),(int)r.getY());
    setSize((int)r.getWidth(),(int)r.getHeight());
    setExtendedState(0);

    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        app.exit();
      }
    });
  }

  /**
   * Shutdown.
   */
  public void shutdown()
  {
    setVisible(false);
  }
}
