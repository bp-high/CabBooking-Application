package com.CabBooking.View;

import com.CabBooking.Utils.CommonConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Code for password field on login window
 */

public class PasswordField extends JPasswordField implements UIInterfaceObject {
    public PasswordField(int topLeftX, int topLeftY, int length, int breadth) {
        initializeInterfaceObject(topLeftX, topLeftY, length, breadth, "", CommonConstants.FONT_SIZE, Color.BLACK, Color.WHITE);
    }

    @Override
    public void initializeInterfaceObject(int topLeftX, int topLeftY, int length, int breadth, String text, int fontSize, Color... colors) {
        setBackground(colors[0]);
        setForeground(colors[1]);
        setDisabledTextColor(colors[1]);
        setSelectedTextColor(colors[1]);
        setBounds(topLeftX, topLeftY, length, breadth);
    }
}
