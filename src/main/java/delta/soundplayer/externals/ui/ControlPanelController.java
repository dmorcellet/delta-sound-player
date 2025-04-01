package delta.soundplayer.externals.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.icons.IconsManager;
import delta.common.ui.swing.panels.AbstractPanelController;
import delta.soundplayer.externals.codecs.AudioFileIdentifier;
import delta.soundplayer.externals.codecs.Identifiers;
import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.player.PlayerEvent;
import delta.soundplayer.externals.player.PlayerListener;

/**
 * Control panel for the audio player.
 * @author DAM
 */
public class ControlPanelController extends AbstractPanelController
{
  // Audio player
  private AudioPlayer _player;
  // UI
  private JFileChooser _chooser;
  private JToggleButton _pauseButton;
  private JButton _playButton;
  private JButton _chooseButton;
  private JLabel _statusLabel;
  private JButton _stopButton;
  // Controllers
  private VolumeController _volume;
  private ProgressController _progress;

  /**
   * Constructor.
   * @param player the managed player.
   * @param useChooser Use file chooser or not.
   */
  public ControlPanelController(AudioPlayer player, boolean useChooser)
  {
    _player=player;
    _volume=new VolumeController(player.getAudioOutput());
    _progress=new ProgressController(player);
    setPanel(buildPanel(useChooser));
  }

  private void initPlayerListeners()
  {
    final Timer timer=new Timer(1000,new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _progress.handleTick();
        if (_player.isPlaying())
        {
          updateStatus();
        }
      }
    });
    timer.start();

    _player.addListener(new PlayerListener()
    {
      public void onEvent(PlayerEvent e)
      {
        _pauseButton.setSelected(_player.isPaused());
        switch (e.getEventCode())
        {
          case PLAYING_STARTED:
            timer.start();
          break;
          case PAUSED:
            timer.stop();
          break;
          case STOPPED:
            timer.stop();
            _progress.handleStop();
            _statusLabel.setText("--:--");
          break;
          case FILE_OPENED:
            Track track=_player.getTrack();
            _progress.handleNewTrack(track);
            updateStatus();
          break;
          case SEEK_FINISHED:
            _progress.handleSeekFinished();
          break;
        }
      }
    });
  }

  private void updateStatus()
  {
    String text=UiUtils.playingTime(_player,_player.getTrack());
    _statusLabel.setText(text);
  }

  private void choose()
  {
    _chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int ok=_chooser.showDialog(null,"Choose");
    File file=null;
    if (ok==JFileChooser.APPROVE_OPTION)
    {
      file=_chooser.getSelectedFile();
    }
    if (file==null)
    {
      return;
    }
    Track track=null;
    AudioFileIdentifier identifier=Identifiers.getIdentifier(file.getName());
    if (identifier!=null)
    {
      track=identifier.identify(file);
      _player.open(track);
    }
  }

  private void initButtonListeners()
  {
    _chooseButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        choose();
      }
    });
    _playButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _player.play();
      }
    });
    _stopButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        _player.stop();
      }
    });
    _pauseButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _player.pause();
      }
    });
  }

  private JPanel buildPanel(boolean useChooser)
  {
    JPanel ret=GuiFactory.buildPanel(new GridBagLayout());
    JToolBar jToolBar1=new JToolBar();
    _stopButton=GuiFactory.buildButton("");
    _playButton=GuiFactory.buildButton("");
    _chooseButton=GuiFactory.buildButton("");
    _pauseButton=new JToggleButton();
    _statusLabel=GuiFactory.buildLabel("--:--");

    ret.setFocusable(false);

    jToolBar1.setOpaque(false);
    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);
    jToolBar1.setBorderPainted(false);
    jToolBar1.setFocusable(false);

    if (useChooser)
    {
      _chooseButton.setFocusable(false);
      _chooseButton.setHorizontalTextPosition(SwingConstants.CENTER);
      _chooseButton.setMargin(new java.awt.Insets(2,3,2,3));
      _chooseButton.setVerticalTextPosition(SwingConstants.BOTTOM);
      jToolBar1.add(_chooseButton);
      _chooser=new JFileChooser();
    }

    _stopButton.setIcon(IconsManager.getIcon("/resources/gui/icons/stop.png"));
    _stopButton.setFocusable(false);
    _stopButton.setHorizontalTextPosition(SwingConstants.CENTER);
    _stopButton.setMargin(new java.awt.Insets(2,3,2,3));
    _stopButton.setVerticalTextPosition(SwingConstants.BOTTOM);
    jToolBar1.add(_stopButton);

    _playButton.setIcon(IconsManager.getIcon("/resources/gui/icons/play.png"));
    _playButton.setFocusable(false);
    _playButton.setHorizontalTextPosition(SwingConstants.CENTER);
    _playButton.setMargin(new java.awt.Insets(2,3,2,3));
    _playButton.setVerticalTextPosition(SwingConstants.BOTTOM);
    jToolBar1.add(_playButton);

    _pauseButton.setIcon(IconsManager.getIcon("/resources/gui/icons/pause.png"));
    _pauseButton.setFocusable(false);
    _pauseButton.setHorizontalTextPosition(SwingConstants.CENTER);
    _pauseButton.setMargin(new java.awt.Insets(2,3,2,3));
    _pauseButton.setVerticalTextPosition(SwingConstants.BOTTOM);
    jToolBar1.add(_pauseButton);

    JSlider volumeSlider=_volume.getSlider();
    JSlider progressSlider=_progress.getSlider();

    GridBagConstraints c=new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    ret.add(jToolBar1,c);
    c=new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    ret.add(volumeSlider,c);
    c=new GridBagConstraints(2,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0);
    ret.add(progressSlider,c);
    c=new GridBagConstraints(3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
    ret.add(_statusLabel,c);

    initButtonListeners();
    initPlayerListeners();
    return ret;
  }
}
