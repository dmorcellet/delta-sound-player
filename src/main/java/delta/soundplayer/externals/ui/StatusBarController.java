package delta.soundplayer.externals.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.panels.AbstractPanelController;
import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.player.PlayerEvent;
import delta.soundplayer.externals.player.PlayerListener;

/**
 * Status bar.
 * @author DAM
 */
public class StatusBarController extends AbstractPanelController
{
  // Audio player
  private AudioPlayer _player;
  // UI
  private JLabel _info;

  /**
   * Constructor.
   * @param player the managed player.
   */
  public StatusBarController(AudioPlayer player)
  {
    _player=player;
    _info=new JLabel("Stopped");
    setPanel(buildPanel());
  }

  private JPanel buildPanel()
  {
    JPanel ret=GuiFactory.buildPanel(new BorderLayout());
    ret.setPreferredSize(new Dimension(10,23));
    ret.add(_info,BorderLayout.CENTER);
    buildListeners();
    return ret;
  }

  private void buildListeners()
  {
    final Timer timer=new Timer(1000,new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (_player.isPlaying())
        {

          Track track=_player.getTrack();
          String codec=track.getFormat();
          int bitrate=track.getBitrate();
          int sampleRate=track.getSampleRate();
          String channels=track.getChannelsAsString();
          String length=track.getLength();
          String playingTime=UiUtils.playingTime(_player,_player.getTrack());
          String fullPlayingTime=(playingTime!=null)?playingTime+" / "+length:"";
          String text=codec+" | "+bitrate+" kbps | "+sampleRate+" Hz | "+channels+" | "+fullPlayingTime;
          _info.setText(text);
        }
      }
    });
    timer.start();

    _player.addListener(new PlayerListener()
    {
      public void onEvent(PlayerEvent e)
      {
        switch (e.getEventCode())
        {
          case PLAYING_STARTED:
            timer.start();
          break;
          case STOPPED:
            _info.setText("Stopped");
          case PAUSED:
            timer.stop();
          default:
          break;
        }
      }
    });
  }
}
