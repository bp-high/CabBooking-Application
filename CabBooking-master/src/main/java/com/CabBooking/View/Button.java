package com.CabBooking.View;

import com.CabBooking.Utils.CommonConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Wrapper around javax.swing.JButton class
 * For use to create a new JButton
 */

public class Button extends JButton implements UIInterfaceObject {
    public Button(int topLeftX, int topLeftY, int length, int breadth, String text, Color backGroundColor, Color foregroundColor) {
        initializeInterfaceObject(topLeftX, topLeftY, length, breadth, text, CommonConstants.FONT_SIZE, backGroundColor, foregroundColor);
    }

    public Button(int topLeftX, int topLeftY, int length, int breadth, String text, Color backGroundColor, Color foregroundColor, int fontSize) {
        initializeInterfaceObject(topLeftX, topLeftY, length, breadth, text, fontSize, backGroundColor, foregroundColor);
    }

    @Override
    public void initializeInterfaceObject(int topLeftX, int topLeftY, int length, int breadth, String text, int fontSize, Color... colors) {
        setText(text);
        setBackground(colors[0]);
        setForeground(colors[1]);
        setOpaque(true);
        setBorderPainted(false);
        if (topLeftX != -1 && topLeftY != -1 && length != -1 && breadth != -1) {
            setBounds(topLeftX, topLeftY, length, breadth);
        }
        setFont(new Font(CommonConstants.FONT, Font.BOLD, fontSize));
    }
}
