package com.CabBooking.Controller;

import com.CabBooking.View.MainFrame;

/**
 * Code starts from here
 * Creates and launches the login window
 */
public class Launcher {
    public static void main(String[] args) {
        // Create and display GUI from event dispatching thread (enhances thread safety)
        javax.swing.SwingUtilities.invokeLater(MainFrame::new);
    }
}
