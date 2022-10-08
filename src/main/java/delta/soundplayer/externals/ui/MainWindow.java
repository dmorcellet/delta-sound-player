package delta.soundplayer.externals.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;

import delta.soundplayer.externals.player.AudioPlayer;

/**
 * Main window for this player.
 * @author DAM
 */
public class MainWindow extends JFrame
{
  /**
   * Constructor.
   * @param player the managed player.
   */
  public MainWindow(AudioPlayer player)
  {
    ControlPanel controlPanel=new ControlPanel(player);
    StatusBar statusBar=new StatusBar(player);
    add(controlPanel,BorderLayout.NORTH);
    add(statusBar,BorderLayout.SOUTH);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    Rectangle r=new Rectangle(50,0,600,230);
    setLocation((int)r.getX(),(int)r.getY());
    setSize((int)r.getWidth(),(int)r.getHeight());
    setExtendedState(0);
  }

  /**
   * Shutdown.
   */
  public void shutdown()
  {
    setVisible(false);
  }
}
