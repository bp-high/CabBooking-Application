package com.CabBooking.View;

import com.CabBooking.Utils.CommonConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Wrapper around javax.swing.JTextField class
 * For use to create a new JTextField
 */

public class TextField extends JTextField implements UIInterfaceObject {
    public TextField(int topLeftX, int topLeftY, int length, int breadth, Color backgroundColor, Color foregroundColor, Color textColor) {
        initializeInterfaceObject(topLeftX, topLeftY, length, breadth, "", 15, backgroundColor, foregroundColor, textColor);
    }

    public TextField(int topLeftX, int topLeftY, int length, int breadth, Color backgroundColor, Color foregroundColor, Color textColor, int fontSize) {
        initializeInterfaceObject(topLeftX, topLeftY, length, breadth, "", fontSize, backgroundColor, foregroundColor, textColor);
    }

    @Override
    public void initializeInterfaceObject(int topLeftX, int topLeftY, int length, int breadth, String text, int fontSize, Color... colors) {
        setBackground(colors[0]);
        setForeground(colors[1]);
        setCaretColor(colors[1]);
        setDisabledTextColor(colors[2]);
        setSelectedTextColor(colors[2]);
        setBounds(topLeftX, topLeftY, length, breadth);
        setFont(new Font(CommonConstants.FONT, Font.BOLD, fontSize));
    }
}
