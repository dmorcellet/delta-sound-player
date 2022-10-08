package delta.soundplayer.externals.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.player.PlayerEvent;
import delta.soundplayer.externals.player.PlayerListener;

/**
 * Status bar.
 * @author DAM
 */
public class StatusBar extends JPanel
{
  // Audio player
  private AudioPlayer _player;
  // UI
  private JLabel _info;

  /**
   * Constructor.
   * @param player the managed player.
   */
  public StatusBar(AudioPlayer player)
  {
    _player=player;
    _info=new JLabel("Stopped");

    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(10,23));
    setBackground(new Color(238,238,238));
    setBorder(BorderFactory.createMatteBorder(2,0,0,0,Color.lightGray));

    Box box=new Box(BoxLayout.X_AXIS);
    box.add(_info);
    box.add(Box.createGlue());
    box.add(Box.createHorizontalStrut(10));
    add(box);

    buildListeners();
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
