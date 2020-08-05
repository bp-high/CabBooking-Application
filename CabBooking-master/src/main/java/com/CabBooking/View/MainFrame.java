package com.CabBooking.View;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.Customer;
import com.CabBooking.Model.mapgraphutilities.Driver;
import com.CabBooking.Controller.SignInPanel;
import com.CabBooking.Utils.Auth;

import javax.swing.*;
import java.awt.*;

/**
 * Code for initial login page
 */

public class MainFrame extends JFrame {
    private boolean userLoggedIn;
    private SignInPanel signInPanel;

    public MainFrame() {
        super("Fantasy Cabs");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, CommonConstants.WINDOW_HEIGHT);
        signInPanel = new SignInPanel(this);
        add(signInPanel, BorderLayout.CENTER);

        // Fix size and center on screen
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        MainFrame mainFrame = this; // For usage in anonymous class

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                String msg = "Are you sure you want to close this application?";
                try {
                    if (signInPanel.getUserPanel().getCabBookedPanel().getTripWithoutPayStatus()) {
                        msg = "If you close the application, trip will automatically be completed\nand, amount will be deducted from wallet !\n" +
                                "Are you sure you want to close this application?";
                    }
                } catch (NullPointerException ex) {
                    System.err.println("INFO: No user riding");
                }

                Driver driver = null;
                // Define action on window close, if a driver was booked, they will be freed, if ride had finished and unpaid, amount will be deducted
                if (JOptionPane.showConfirmDialog(mainFrame,
                        msg, "Close Application?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    if (userLoggedIn) {
                        Customer customer = signInPanel.getUserPanel().getCustomer();
                        Auth.logoutUser(customer);
                        customer.changeLoggedInStatus(false);
                        driver = signInPanel.getUserPanel().getCabBookingPanel().getCurrentDriver();
                        if (driver != null && !(driver.getLocation() != -1 && signInPanel.getUserPanel().getCabBookingPanel().getDriverDBLocation() == -1)) {
                            int location = signInPanel.getUserPanel().getCabBookingPanel().getDriverLocation();
                            try {
                                if (signInPanel.getUserPanel().getCabBookedPanel().getTripWithoutPayStatus()) {
                                    location = signInPanel.getUserPanel().getCabBookedPanel().getFinalLocation();
                                }
                            } catch (NullPointerException ex) {
                                System.err.println("INFO: No ongoing ride");
                            }

                            signInPanel.getUserPanel().getCabBookingPanel().updateDriverLocation(location);
                        }
                        try {
                            if (signInPanel.getUserPanel().getCabBookedPanel().getTripWithoutPayStatus()) {
                                Customer c = signInPanel.getUserPanel().getCustomer();
                                c.setMoney(c.getMoney() - signInPanel.getUserPanel().getCabBookedPanel().getPrice());
                                Auth.updateCustomerWallet(c);
                            }
                        } catch (NullPointerException ex) {
                            System.err.println("INFO: No ongoing ride");
                        }
                    }
                    System.exit(0);
                } else {
                    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                }
            }

        });

    }
    public void setUserLoggedIn(boolean b) {
        userLoggedIn = b;
    }
    public SignInPanel getSignInPanel() {
        return  signInPanel;
    }
}
