package delta.soundplayer.externals.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import delta.soundplayer.externals.codecs.AudioFileIdentifier;
import delta.soundplayer.externals.codecs.Identifiers;
import delta.soundplayer.externals.data.Track;
import delta.soundplayer.externals.player.AudioOutput;
import delta.soundplayer.externals.player.AudioPlayer;
import delta.soundplayer.externals.player.PlayerEvent;
import delta.soundplayer.externals.player.PlayerListener;
import delta.soundplayer.externals.utils.Util;

/**
 * Control panel for the audio player.
 * @author DAM
 */
public class ControlPanel extends javax.swing.JPanel
{
  private Application app=Application.getInstance();
  private AudioPlayer player=app.getPlayer();
  private AudioOutput output=player.getAudioOutput();
  private Popup popup;
  private JToolTip toolTip;
  private PopupFactory popupFactory=PopupFactory.getSharedInstance();
  private JFileChooser chooser;

  private boolean isSeeking=false;
  private boolean progressEnabled=false;
  private MouseAdapter progressMouseListener;

  /**
   * Creates new form ControlBar
   */
  public ControlPanel()
  {
    initComponents();
    initButtonListeners();
    initSliders();
    initPlayerListeners();
    updateUI();
  }

  private void initPlayerListeners()
  {
    final Timer timer=new Timer(1000,new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (progressEnabled&&player.isPlaying()&&!isSeeking)
        {
          progressSlider.setValue((int)player.getCurrentSample());
        }
        if (player.isPlaying()) updateStatus();
      }
    });
    timer.start();

    player.addListener(new PlayerListener()
    {
      public void onEvent(PlayerEvent e)
      {
        pauseButton.setSelected(player.isPaused());
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
            progressEnabled=false;
            progressSlider.setValue(progressSlider.getMinimum());
            statusLabel.setText(null);
          break;
          case FILE_OPENED:
            Track track=player.getTrack();
            if (track!=null)
            {
              int max=(int)track.getTotalSamples();
              if (max==-1)
              {
                progressEnabled=false;
              }
              else
              {
                progressEnabled=true;
                progressSlider.setMaximum(max);
              }
            }
            progressSlider.setValue((int)player.getCurrentSample());
            updateStatus();
          break;
          case SEEK_FINISHED:
            isSeeking=false;
          break;
        }
      }
    });
  }

  private void updateStatus()
  {
    String text=UiUtils.playingTime(player,player.getTrack());
    statusLabel.setText(text);
  }

  private void initSliders()
  {
    toolTip=progressSlider.createToolTip();

    volumeSlider.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        float volume=volumeSlider.getValue()/100f;
        output.setVolume(volume);
      }
    });

    volumeSlider.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mousePressed(MouseEvent e)
      {
        volumeSlider.setValue(getSliderValueForX(volumeSlider,e.getX()));
      }
    });

    volumeSlider.addMouseWheelListener(new MouseWheelListener()
    {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        int value=volumeSlider.getValue();
        if (e.getWheelRotation()>0)
          value-=5;
        else
          value+=5;
        volumeSlider.setValue(value);
      }
    });

    progressSlider.addMouseMotionListener(new MouseMotionAdapter()
    {
      @Override
      public void mouseDragged(MouseEvent e)
      {
        if (!progressEnabled) return;
        hideToolTip();
        showToolTip(e);
      }
    });

    progressMouseListener=new MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        if (!progressEnabled) return;
        hideToolTip();
        player.seek(progressSlider.getValue());
      }

      public void mousePressed(MouseEvent e)
      {
        if (!progressEnabled) return;
        isSeeking=true;
        progressSlider.setValue(getSliderValueForX(progressSlider,e.getX()));
        hideToolTip();
        showToolTip(e);
      }
    };
    progressSlider.addMouseListener(progressMouseListener);

    progressSlider.addMouseMotionListener(new MouseMotionAdapter()
    {
      @Override
      public void mouseDragged(MouseEvent e)
      {
        if (!progressEnabled) return;
        progressSlider.setValue(getSliderValueForX(progressSlider,e.getX()));
      }
    });
  }

  private int getSliderValueForX(JSlider slider, int x)
  {
    return ((BasicSliderUI)slider.getUI()).valueForXPosition(x);
  }

  private void showToolTip(MouseEvent e)
  {
    Track s=player.getTrack();
    if (s!=null)
    {
      toolTip.setTipText(Util.samplesToTime(progressSlider.getValue()-progressSlider.getMinimum(),s.getSampleRate()));
      int x=e.getXOnScreen();
      x=Math.max(x,progressSlider.getLocationOnScreen().x);
      x=Math.min(x,progressSlider.getLocationOnScreen().x+progressSlider.getWidth()-toolTip.getWidth());
      popup=popupFactory.getPopup(progressSlider,toolTip,x,progressSlider.getLocationOnScreen().y+25);
      popup.show();
    }
  }

  private void hideToolTip()
  {
    if (popup!=null)
    {
      popup.hide();
      popup=null;
    }
  }

  private void choose()
  {
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setCurrentDirectory(new File("D:\\dev\\git\\lotro-tools"));
    int ok=chooser.showDialog(null,"Choose");
    File file=null;
    if (ok==JFileChooser.APPROVE_OPTION)
    {
      file=chooser.getSelectedFile();
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
      player.open(track);
    }
  }

  private void initButtonListeners()
  {
    chooseButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        choose();
      }
    });
    playButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        player.play();
      }
    });
    stopButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        player.stop();
      }
    });
    pauseButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        player.pause();
      }
    });
  }

  @Override
  public void updateUI()
  {
    super.updateUI();
    fixSliderWidth();
  }

  private void fixSliderWidth()
  {
    if (progressSlider!=null)
    {
      boolean windowsLaF=true;
      progressSlider.setPaintTicks(windowsLaF);
      volumeSlider.setPaintTicks(windowsLaF);

      SwingUtilities.invokeLater(new Runnable()
      {
        @Override
        public void run()
        {
          for(MouseListener ml:progressSlider.getMouseListeners())
          {
            progressSlider.removeMouseListener(ml);
          }
          progressSlider.addMouseListener(progressMouseListener);
        }
      });
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated
  // Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    javax.swing.JToolBar jToolBar1=new javax.swing.JToolBar();
    stopButton=new javax.swing.JButton();
    playButton=new javax.swing.JButton();
    chooseButton=new javax.swing.JButton();
    pauseButton=new javax.swing.JToggleButton();
    volumeSlider=new javax.swing.JSlider();
    progressSlider=new javax.swing.JSlider();
    statusLabel=new javax.swing.JLabel();

    setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0,0,1,0,java.awt.Color.gray),
        javax.swing.BorderFactory.createEmptyBorder(0,5,0,5)));
    setFocusable(false);
    setPreferredSize(new java.awt.Dimension(669,32));

    jToolBar1.setFloatable(false);
    jToolBar1.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
    jToolBar1.setRollover(true);
    jToolBar1.setBorderPainted(false);
    jToolBar1.setFocusable(false);

    // playButton.setIcon(new
    // javax.swing.ImageIcon(getClass().getResource("play.png"))); // NOI18N
    chooseButton.setFocusable(false);
    chooseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    chooseButton.setMargin(new java.awt.Insets(2,3,2,3));
    chooseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(chooseButton);
    chooser=new JFileChooser();

    stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("stop.png"))); // NOI18N
    stopButton.setFocusable(false);
    stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    stopButton.setMargin(new java.awt.Insets(2,3,2,3));
    stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(stopButton);

    playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("play.png"))); // NOI18N
    playButton.setFocusable(false);
    playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    playButton.setMargin(new java.awt.Insets(2,3,2,3));
    playButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(playButton);

    pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("pause.png"))); // NOI18N
    pauseButton.setFocusable(false);
    pauseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    pauseButton.setMargin(new java.awt.Insets(2,3,2,3));
    pauseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(pauseButton);

    volumeSlider.setValue((int)(output.getVolume()*100));
    volumeSlider.setFocusable(false);

    progressSlider.setValue(0);
    progressSlider.setFocusable(false);

    statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getStyle()|java.awt.Font.BOLD));

    javax.swing.GroupLayout layout=new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup()
        .addComponent(jToolBar1,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(4,4,4).addComponent(volumeSlider,javax.swing.GroupLayout.PREFERRED_SIZE,119,javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(progressSlider,javax.swing.GroupLayout.DEFAULT_SIZE,233,Short.MAX_VALUE).addGap(9,9,9).addComponent(statusLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)));
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(volumeSlider,javax.swing.GroupLayout.PREFERRED_SIZE,31,javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(progressSlider,javax.swing.GroupLayout.PREFERRED_SIZE,31,javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(statusLabel,javax.swing.GroupLayout.PREFERRED_SIZE,31,javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jToolBar1,javax.swing.GroupLayout.PREFERRED_SIZE,31,javax.swing.GroupLayout.PREFERRED_SIZE));
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.JToggleButton pauseButton;
  javax.swing.JButton playButton;
  javax.swing.JButton chooseButton;
  javax.swing.JSlider progressSlider;
  javax.swing.JLabel statusLabel;
  javax.swing.JButton stopButton;
  javax.swing.JSlider volumeSlider;
  // End of variables declaration//GEN-END:variables
}
