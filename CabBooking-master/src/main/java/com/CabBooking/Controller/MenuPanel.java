package com.CabBooking.Controller;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.Customer;
import com.CabBooking.Model.mapgraphutilities.Driver;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.BackgroundPanel;
import com.CabBooking.View.MainFrame;
import com.CabBooking.View.TextLabel;
import com.CabBooking.View.Button;

import javax.swing.*;
import java.awt.*;

/**
 * Container class to display menu
 */
class MenuPanel extends BackgroundPanel {
    private final Customer customer;
    private final BackgroundPanel contentPanel;
    private final UserPanel parentUserPanel;
    private Button[] menuButtons;

    MenuPanel(Customer c, BackgroundPanel content, UserPanel userPanel) {
        customer = c;
        contentPanel = content;
        parentUserPanel = userPanel;
        setBounds(0, 0, 200, 600);
        setLayout(new GridLayout(5, 1, 0, 1));

        String welcome;
        try {
            String name = customer.getName();
            welcome = "<html><center>Welcome,<br>" + name.substring(0, name.indexOf(' ')) + "</center></html>";
        } catch (StringIndexOutOfBoundsException ex) {
            welcome = "<html><center>Welcome,<br>" + customer.getName() + "</center></html>";
        }
        TextLabel nameLabel = new TextLabel(-1, -1, -1, -1, Color.WHITE, welcome, 20);
        nameLabel.setBackground(Color.decode("#5e0052"));
        nameLabel.setOpaque(true);
        menuButtons = new Button[4];
        menuButtons[0] = new Button(-1, -1, -1, -1, "Profile", Color.BLACK, Color.WHITE);
        menuButtons[1] = new Button(-1, -1, -1, -1, "Book A Ride", Color.BLACK, Color.WHITE);
        menuButtons[2] = new Button(-1, -1, -1, -1, "Wallet", Color.BLACK, Color.WHITE);
        menuButtons[3] = new Button(-1, -1, -1, -1, "Log Out", Color.BLACK, Color.WHITE);

        final Font font = new Font(CommonConstants.FONT, Font.PLAIN, 20);
        nameLabel.setFont(font);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        for (Button button : menuButtons) {
            button.setFont(font);
        }
        menuButtons[1].setBackground(Color.decode("#36002f"));

        addActionListeners();

        add(nameLabel);
        for (Button button : menuButtons) {
            add(button);
        }
    }

    /**
     * Define actions for each of the 4 menu buttons
     */
    private void addActionListeners() {
        menuButtons[3].addActionListener(e -> {
            updateDriverLocation();

            // Create and display GUI from event dispatching thread (enhances thread safety)
            SwingUtilities.invokeLater(() -> {

                ((MainFrame)SwingUtilities.getRoot(this)).setUserLoggedIn(false);

                Auth.logoutUser(customer);
                ((MainFrame)SwingUtilities.getRoot(this)).setContentPane(new SignInPanel(((MainFrame)SwingUtilities.getRoot(this))));
                invalidate();
                validate();
            });
        });

        menuButtons[0].addActionListener(e -> {
            // Create and display GUI from event dispatching thread (enhances thread safety)
            SwingUtilities.invokeLater(() -> {
                menuButtons[0].setBackground(Color.decode("#36002f"));
                menuButtons[1].setBackground(Color.BLACK);
                menuButtons[2].setBackground(Color.BLACK);

                ((CardLayout) contentPanel.getLayout()).show(contentPanel, "profileCard");
            });
        });

        menuButtons[1].addActionListener(e -> {
            // Create and display GUI from event dispatching thread (enhances thread safety)
            SwingUtilities.invokeLater(() -> {
                menuButtons[1].setBackground(Color.decode("#36002f"));
                menuButtons[0].setBackground(Color.BLACK);
                menuButtons[2].setBackground(Color.BLACK);

                if (parentUserPanel.getCabBookedPanel() == null) {
                    ((CardLayout) contentPanel.getLayout()).show(contentPanel, "bookingCard");
                } else {
                    ((CardLayout) contentPanel.getLayout()).show(contentPanel, "bookedCard");
                }
            });
        });

        menuButtons[2].addActionListener(e -> {
            // Create and display GUI from event dispatching thread (enhances thread safety)
            SwingUtilities.invokeLater(() -> {
                menuButtons[2].setBackground(Color.decode("#36002f"));
                menuButtons[1].setBackground(Color.BLACK);
                menuButtons[0].setBackground(Color.BLACK);
                ((CardLayout) contentPanel.getLayout()).show(contentPanel, "walletCard");
            });
        });
    }

    private void updateDriverLocation() {
        try {
            Driver driver = parentUserPanel.getCabBookingPanel().getCurrentDriver();
            if (!(driver.getLocation() != -1 && parentUserPanel.getCabBookingPanel().getDriverDBLocation() == -1)) {
                parentUserPanel.getCabBookingPanel().updateDriverLocation(parentUserPanel.getCabBookingPanel().getDriverLocation());
            }
        } catch (NullPointerException ex) {
            System.err.println("INFO: No ongoing ride");
        }
    }

    void disableButtons() {
        menuButtons[3].setEnabled(false);
    }
    void enableButtons() {
        menuButtons[3].setEnabled(true);
    }
}
