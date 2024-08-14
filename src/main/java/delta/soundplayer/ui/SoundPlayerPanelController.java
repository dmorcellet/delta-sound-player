package delta.soundplayer.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JPanel;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.panels.AbstractPanelController;
import delta.soundplayer.api.SoundFormat;
import delta.soundplayer.api.SoundPlayer;
import delta.soundplayer.externals.codecs.AudioFileIdentifier;
import delta.soundplayer.externals.codecs.Identifiers;
import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.ui.ControlPanelController;
import delta.soundplayer.externals.ui.StatusBarController;

/**
 * Controller for a sound player panel.
 * @author DAM
 */
public class SoundPlayerPanelController extends AbstractPanelController implements SoundPlayer
{
  private AudioPlayer _player;

  /**
   * Constructor.
   */
  public SoundPlayerPanelController()
  {
    _player=new AudioPlayer();
    setPanel(buildPanel());
  }

  private JPanel buildPanel()
  {
    JPanel ret=GuiFactory.buildPanel(new GridBagLayout());
    GridBagConstraints c=new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
    ControlPanelController controlPanel=new ControlPanelController(_player,false);
    ret.add(controlPanel.getPanel(),c);
    StatusBarController statusBar=new StatusBarController(_player);
    c=new GridBagConstraints(0,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
    ret.add(statusBar.getPanel(),c);
    return ret;
  }

  @Override
  public void start(File soundFile, SoundFormat format)
  {
    Track track=buildTrack(soundFile,format);
    if (track!=null)
    {
      _player.open(track);
    }
  }

  @Override
  public void stop()
  {
    _player.stop();
  }

  private Track buildTrack(File soundFile, SoundFormat soundFormat)
  {
    Track track=null;
    AudioFileIdentifier identifier=Identifiers.getIdentifier(soundFormat);
    if (identifier!=null)
    {
      track=identifier.identify(soundFile);
    }
    return track;
  }

  @Override
  public void dispose()
  {
    super.dispose();
    if (_player!=null)
    {
      _player.stop();
      _player=null;
    }
  }
}
