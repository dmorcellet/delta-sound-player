package delta.soundplayer.externals.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import delta.soundplayer.externals.player.AudioOutput;

/**
 * Volume controller.
 * @author DAM
 */
public class VolumeController
{
  // Audio output
  private AudioOutput _output;
  // UI
  private JSlider _volumeSlider;

  /**
   * Constructor.
   * @param output Managed audio output.
   */
  public VolumeController(AudioOutput output)
  {
    _output=output;
    _volumeSlider=new JSlider();
    init();
  }

  /**
   * Get the managed slider.
   * @return the managed slider.
   */
  public JSlider getSlider()
  {
    return _volumeSlider;
  }

  private void init()
  {
    _volumeSlider.setPaintTicks(true);
    _volumeSlider.setValue((int)(_output.getVolume()*100));
    _volumeSlider.setFocusable(false);

    _volumeSlider.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        float volume=_volumeSlider.getValue()/100f;
        _output.setVolume(volume);
      }
    });

    _volumeSlider.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mousePressed(MouseEvent e)
      {
        _volumeSlider.setValue(SlidersUtils.getSliderValueForX(_volumeSlider,e.getX()));
      }
    });

    _volumeSlider.addMouseWheelListener(new MouseWheelListener()
    {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        int value=_volumeSlider.getValue();
        if (e.getWheelRotation()>0)
        {
          value-=5;
        }
        else
        {
          value+=5;
        }
        _volumeSlider.setValue(value);
      }
    });
  }
}
