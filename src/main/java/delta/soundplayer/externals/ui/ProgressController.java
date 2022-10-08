package delta.soundplayer.externals.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.utils.Util;

/**
 * Controller for a progress bar of an audio track.
 * @author DAM
 */
public class ProgressController
{
  // Managed audio player
  private AudioPlayer _player;
  // UI
  private JSlider _progressSlider;
  private Popup _popup;
  private JToolTip _toolTip;
  private PopupFactory _popupFactory;
  // State
  private boolean _isSeeking;
  private boolean _progressEnabled;

  /**
   * Constructor.
   * @param player Managed audio player.
   */
  public ProgressController(AudioPlayer player)
  {
    _player=player;
    _progressSlider=new JSlider();
    _popupFactory=PopupFactory.getSharedInstance();
    _isSeeking=false;
    _progressEnabled=false;
    init();
  }

  /**
   * Get the managed slider.
   * @return the managed slider.
   */
  public JSlider getSlider()
  {
    return _progressSlider;
  }

  private void init()
  {
    _progressSlider.setPaintTicks(false);
    _progressSlider.setValue(0);
    _progressSlider.setFocusable(false);
    _toolTip=_progressSlider.createToolTip();

    _progressSlider.addMouseMotionListener(new MouseMotionAdapter()
    {
      @Override
      public void mouseDragged(MouseEvent e)
      {
        if (!_progressEnabled)
        {
          return;
        }
        hideToolTip();
        showToolTip(e);
        _progressSlider.setValue(SlidersUtils.getSliderValueForX(_progressSlider,e.getX()));
      }
    });

    MouseListener progressMouseListener=new MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        if (!_progressEnabled)
        {
          return;
        }
        hideToolTip();
        _player.seek(_progressSlider.getValue());
      }

      public void mousePressed(MouseEvent e)
      {
        if (!_progressEnabled)
        {
          return;
        }
        _isSeeking=true;
        _progressSlider.setValue(SlidersUtils.getSliderValueForX(_progressSlider,e.getX()));
        hideToolTip();
        showToolTip(e);
      }
    };
    _progressSlider.addMouseListener(progressMouseListener);
  }

  private void showToolTip(MouseEvent e)
  {
    Track s=_player.getTrack();
    if (s!=null)
    {
      _toolTip.setTipText(Util.samplesToTime(_progressSlider.getValue()-_progressSlider.getMinimum(),s.getSampleRate()));
      int x=e.getXOnScreen();
      x=Math.max(x,_progressSlider.getLocationOnScreen().x);
      x=Math.min(x,_progressSlider.getLocationOnScreen().x+_progressSlider.getWidth()-_toolTip.getWidth());
      _popup=_popupFactory.getPopup(_progressSlider,_toolTip,x,_progressSlider.getLocationOnScreen().y+25);
      _popup.show();
    }
  }

  private void hideToolTip()
  {
    if (_popup!=null)
    {
      _popup.hide();
      _popup=null;
    }
  }

  /**
   * Handle a timer tick during audio play.
   */
  public void handleTick()
  {
    if (_progressEnabled&&_player.isPlaying()&&!_isSeeking)
    {
      _progressSlider.setValue((int)_player.getCurrentSample());
    }
  }

  /**
   * Handle the "seek finished" event.
   */
  public void handleSeekFinished()
  {
    _isSeeking=false;
  }

  /**
   * Handle the stop event.
   */
  public void handleStop()
  {
    _progressEnabled=false;
    _progressSlider.setValue(_progressSlider.getMinimum());
  }

  /**
   * Handle the "new track" event.
   * @param track Track.
   */
  public void handleNewTrack(Track track)
  {
    if (track!=null)
    {
      int max=(int)track.getTotalSamples();
      if (max==-1)
      {
        _progressEnabled=false;
      }
      else
      {
        _progressEnabled=true;
        _progressSlider.setMaximum(max);
      }
    }
    _progressSlider.setValue((int)_player.getCurrentSample());
  }
}
