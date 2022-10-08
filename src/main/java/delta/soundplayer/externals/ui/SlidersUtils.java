package delta.soundplayer.externals.ui;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Utility methods related to sliders.
 * @author DAM
 */
public class SlidersUtils
{
  /**
   * Get the value of the slider.
   * @param slider Slider.
   * @param x X position to use.
   * @return A slider value.
   */
  public static int getSliderValueForX(JSlider slider, int x)
  {
    return ((BasicSliderUI)slider.getUI()).valueForXPosition(x);
  }
}
