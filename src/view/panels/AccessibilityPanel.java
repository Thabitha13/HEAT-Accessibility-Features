package view.panels;

import accessibility.FontSizeManager;
import managers.WindowManager;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

/**
 * Accessibility toolbar panel for font size control.
 * Provides quick access to increase/decrease font size with large visible buttons.
 * Target users: partially sighted who need larger text without opening Options.
 */
public class AccessibilityPanel extends JPanel {

    private JButton decreaseButton;
    private JButton increaseButton;
    private JLabel fontSizeLabel;
    private JSlider fontSizeSlider;
    private boolean sliderChanging = false;

    public AccessibilityPanel() {
        initializeComponents();
    }

    private void initializeComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setPreferredSize(new Dimension(500, 60));
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Accessibility: Font Size Control",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP
        ));

        // Decrease button (A-)
        decreaseButton = new JButton("A−");
        decreaseButton.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        decreaseButton.setPreferredSize(new Dimension(60, 45));
        decreaseButton.setMaximumSize(new Dimension(60, 45));
        decreaseButton.setToolTipText("Decrease font size (Cmd/Ctrl + Minus)");
        decreaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDecreaseFontSize();
            }
        });

        // Increase button (A+)
        increaseButton = new JButton("A+");
        increaseButton.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        increaseButton.setPreferredSize(new Dimension(60, 45));
        increaseButton.setMaximumSize(new Dimension(60, 45));
        increaseButton.setToolTipText("Increase font size (Cmd/Ctrl + Plus)");
        increaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onIncreaseFontSize();
            }
        });

        // Font size label
        fontSizeLabel = new JLabel("Size: 12pt");
        fontSizeLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        fontSizeLabel.setPreferredSize(new Dimension(90, 30));
        fontSizeLabel.setMaximumSize(new Dimension(90, 30));
        fontSizeLabel.setHorizontalAlignment(JLabel.CENTER);

        // Font size slider
        fontSizeSlider = new JSlider(
            JSlider.HORIZONTAL,
            FontSizeManager.getMinFontSize(),
            FontSizeManager.getMaxFontSize(),
            FontSizeManager.getEditorFontSize()
        );
        fontSizeSlider.setMajorTickSpacing(4);
        fontSizeSlider.setMinorTickSpacing(1);
        fontSizeSlider.setPaintTicks(true);
        fontSizeSlider.setPaintLabels(true);
        fontSizeSlider.setPreferredSize(new Dimension(250, 50));
        fontSizeSlider.setMaximumSize(new Dimension(250, 50));
        fontSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onSliderChanged();
            }
        });

        // Add components with spacing
        this.add(Box.createHorizontalStrut(10));
        this.add(decreaseButton);
        this.add(Box.createHorizontalStrut(8));
        this.add(increaseButton);
        this.add(Box.createHorizontalStrut(15));
        this.add(fontSizeLabel);
        this.add(Box.createHorizontalStrut(15));
        this.add(fontSizeSlider);
        this.add(Box.createHorizontalGlue());
        this.add(Box.createHorizontalStrut(10));

        updateDisplay();
    }

    /**
     * Handle decrease button click
     */
    private void onDecreaseFontSize() {
        if (FontSizeManager.canDecrease()) {
            FontSizeManager.decreaseFontSize();
            updateDisplay();
        }
    }

    /**
     * Handle increase button click
     */
    private void onIncreaseFontSize() {
        if (FontSizeManager.canIncrease()) {
            FontSizeManager.increaseFontSize();
            updateDisplay();
        }
    }

    /**
     * Handle slider movement
     */
    private void onSliderChanged() {
        if (!sliderChanging) {
            int size = fontSizeSlider.getValue();
            FontSizeManager.setFontSize(size);
            updateDisplay();
        }
    }

    /**
     * Update the displayed font size and button states
     */
    public void updateDisplay() {
        int currentSize = FontSizeManager.getEditorFontSize();
        fontSizeLabel.setText("Size: " + currentSize + "pt");
        
        sliderChanging = true;
        fontSizeSlider.setValue(currentSize);
        sliderChanging = false;
        
        decreaseButton.setEnabled(FontSizeManager.canDecrease());
        increaseButton.setEnabled(FontSizeManager.canIncrease());
    }
}