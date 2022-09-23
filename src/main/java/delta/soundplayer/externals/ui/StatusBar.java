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

public class StatusBar extends JPanel {
    private JLabel info;

    private Application app = Application.getInstance();
    private AudioPlayer player = app.getPlayer();

    public StatusBar() {
        info = new JLabel("Stopped");

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(10, 23));
        setBackground(new Color(238, 238, 238));
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.lightGray));

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(info);
        box.add(Box.createGlue());
        box.add(Box.createHorizontalStrut(10));
        add(box);

        buildListeners();
    }

    private void buildListeners() {
        final Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player.isPlaying()) {

                  Track track = player.getTrack();
                  String codec = track.getFormat();
                  int bitrate = track.getBitrate();
                  int sampleRate = track.getSampleRate();
                  String channels = track.getChannelsAsString();
                  String length = track.getLength();
                  String playingTime=UiUtils.playingTime(player, player.getTrack());
                  String fullPlayingTime = (playingTime!=null) ? playingTime+" / "+length : "";
                  String text = codec+" | "+bitrate+" kbps | "+sampleRate+" Hz | "+channels+" | "+fullPlayingTime;
                  info.setText(text);
                }
            }
        });
        timer.start();

        player.addListener(new PlayerListener() {
            public void onEvent(PlayerEvent e) {
                switch (e.getEventCode()) {
                    case PLAYING_STARTED:
                        timer.start();
                        break;
                    case STOPPED:
                        info.setText("Stopped");
                    case PAUSED:
                        timer.stop();
                    default:
                      break;
                }
            }
        });
    }
}