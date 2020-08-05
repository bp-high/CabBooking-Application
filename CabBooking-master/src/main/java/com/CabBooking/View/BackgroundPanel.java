package com.CabBooking.View;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Color;

/**
 * Code for launch window background
 */

public class BackgroundPanel extends JPanel {
    private int rComponent;
    private int bComponent;

    private boolean blackBackground;
    /**
     * BackgroundPanel to occupy whole screen
     */
    protected BackgroundPanel() {
        // Remove all restrictions on layout
        this.setLayout(null);
        this.setBounds(0, 0, 1200, 600); // Fill whole page
        rComponent = 139;
        bComponent = 139;
    }

    public BackgroundPanel(boolean b) {
        blackBackground = b;
        this.setLayout(null);
        this.setBounds(0, 0, 1200, 600); // Fill whole page
        setBackground(Color.BLACK);
        rComponent = 139;
        bComponent = 139;
    }

    /**
     * BackgroundPanel to occupy within given bounds
     */
    public BackgroundPanel(int topleftX, int topleftY, int length, int width) {
        this.setBounds(topleftX, topleftY, length, width);
        rComponent = 139;
        bComponent = 139;
    }

    private void changeColor() {
        rComponent++;
        bComponent++;
    }
    private void resetColor() {
        rComponent = 0;
        bComponent = 0;
    }
    public void animateAppearance() {
        resetColor();
        new Thread(() -> {
            for (int i = 0; i < 139; i++) {
                changeColor();
                repaint();
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Create and render a gradient
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!blackBackground) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            Color color1 = Color.BLACK;
            Color color2 = new Color(rComponent, 0, bComponent);
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}
