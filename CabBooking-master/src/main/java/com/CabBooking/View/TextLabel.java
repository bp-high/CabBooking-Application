package com.CabBooking.View;

import javax.swing.*;
import java.awt.*;

/**
 * Wrapper around javax.swing.JLabel class
 * For use to create a new JLabel
 */

public class TextLabel extends JLabel implements UIInterfaceObject {
    public TextLabel(int topLeftX, int topLeftY, int length, int breadth, Color foregroundColor, String text, int fontSize) {
        initializeInterfaceObject(topLeftX, topLeftY, length, breadth, text, fontSize, foregroundColor);
    }

    @Override
    public void initializeInterfaceObject(int topLeftX, int topLeftY, int length, int breadth, String text, int fontSize, Color... colors) {
        setForeground(colors[0]);
        setText(text);
        setFont(new Font("SansSerif", Font.BOLD, fontSize));
        setBounds(topLeftX, topLeftY, length, breadth);
    }
}
