package com.CabBooking.Controller;

import com.CabBooking.Model.Customer;
import com.CabBooking.Model.mapgraphutilities.EdgeWeightedGraph;
import com.CabBooking.View.BackgroundPanel;
import com.CabBooking.View.MainFrame;

import java.awt.*;

/**
 * Code for user window
 */
public class UserPanel extends BackgroundPanel {
    private final MainFrame mainFrame;
    private final BackgroundPanel contentPanel;
    private final Customer customer;
    private final MenuPanel menuPanel;
    private CabBookedPanel cabBookedPanel;
    private CabBookingPanel cabBookingPanel;
    private ProfilePanel profilePanel;

    /**
     * Setup and display welcome page
     * @param c Current customer
     * @param graph Map
     */
    UserPanel(Customer c, EdgeWeightedGraph graph, MainFrame mf) {
        customer = c;
        mainFrame = mf;
        setLayout(null);
        setBounds(0, 0, 1200, 600); // Fill whole page
        contentPanel = new BackgroundPanel(200, 0, 1000, 600);
        menuPanel = new MenuPanel(c, contentPanel, this);
        contentPanel.setLayout(new CardLayout()); // CardLayout is used to allow user to pick off a panel where it was left, does not reinitialize JPanel

        setProfilePanel();
        add(menuPanel);
        add(contentPanel);
        setCabBookingPanel(graph);
        setWalletPanel();
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "bookingCard");
        cabBookingPanel.animateAppearance();
    }

    // Util methods
    public Customer getCustomer() {
        return customer;
    }
    void setProfilePanel() {
        if (profilePanel != null) {
            contentPanel.remove(profilePanel);
        }
        profilePanel = new ProfilePanel(customer);
        contentPanel.add("profileCard", profilePanel);
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "profileCard");
    }
    public CabBookedPanel getCabBookedPanel() {
        return cabBookedPanel;
    }
    void setCabBookedPanel(CabBookedPanel cbp) {
        cabBookedPanel = cbp;
        if (cabBookedPanel != null) {
            contentPanel.add("bookedCard", cabBookedPanel);
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "bookedCard");
        }
    }
    public CabBookingPanel getCabBookingPanel() {
        return cabBookingPanel;
    }
    void setCabBookingPanel(EdgeWeightedGraph graph) {
        cabBookingPanel = new CabBookingPanel(graph, this);
        contentPanel.add("bookingCard", cabBookingPanel);

        if (cabBookedPanel == null) {
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "bookingCard");
        }
    }
    MenuPanel getMenuPanel() {
        return menuPanel;
    }
    void setWalletPanel() {
        final WalletPanel walletPanel = new WalletPanel(mainFrame, customer);
        contentPanel.add("walletCard", walletPanel);
    }
}
