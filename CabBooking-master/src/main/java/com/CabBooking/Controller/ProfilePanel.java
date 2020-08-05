package com.CabBooking.Controller;

import com.CabBooking.Model.Customer;
import com.CabBooking.View.BackgroundPanel;
import com.CabBooking.View.TextLabel;
import com.CabBooking.View.Button;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Code for profile page
 * Mostly aesthetic stuff
 */
class ProfilePanel extends BackgroundPanel {
    private Customer customer;
    ProfilePanel(Customer c) {
        customer = c;
        final String editText = "<html><U>Edit</U></html>";
        final TextLabel name = new TextLabel(100, 150, 200, 50, Color.WHITE, "Name", 35);
        final TextLabel username = new TextLabel(100, 250, 200, 50, Color.WHITE, "Username", 35);
        final TextLabel mobile = new TextLabel(100, 350, 200, 50, Color.WHITE, "Mobile No.", 35);
        final TextLabel customerName = new TextLabel(400, 150, 350, 50, Color.WHITE, customer.getName(), 35);
        final TextLabel customerUsername = new TextLabel(400, 250, 350, 50, Color.WHITE, customer.getUsername(), 35);
        final TextLabel customerMobile = new TextLabel(400, 350, 350, 50, Color.WHITE, customer.getMobile(), 35);
        final TextLabel colon1 = new TextLabel(350, 150, 50, 50, Color.WHITE, ":", 35);
        final TextLabel colon2 = new TextLabel(350, 250, 50, 50, Color.WHITE, ":", 35);
        final TextLabel colon3 = new TextLabel(350, 350, 50, 50, Color.WHITE, ":", 35);
        final Button changeName = new Button(700, 150, 100, 50, editText, Color.BLACK, Color.WHITE, 12);
        final Button changeNumber = new Button(700, 350, 100, 50, editText, Color.BLACK, Color.WHITE, 12);
        final Button changePassword = new Button(400, 470, 200, 50, "Change password", Color.BLACK, Color.WHITE, 15);
        changeName.setOpaque(false);
        changeNumber.setOpaque(false);
        changeName.setActionCommand("name");
        changeNumber.setActionCommand("number");
        changePassword.setActionCommand("password");
        EditActionListener editActionListener = new EditActionListener(this);
        changeName.addActionListener(editActionListener);
        changeNumber.addActionListener(editActionListener);
        changePassword.addActionListener(editActionListener);

        add(name);
        add(username);
        add(mobile);
        add(customerName);
        add(customerUsername);
        add(customerMobile);
        add(colon1);
        add(colon2);
        add(colon3);
        add(changeName);
        add(changeNumber);
        add(changePassword);
        setLayout(new BorderLayout());
        setVisible(true);
    }

    /**
     * ActionListener to display edit UI when buttons are pressed
     */
    class EditActionListener implements ActionListener {
        private ProfilePanel profilePanel;
        EditActionListener(ProfilePanel p) {
            profilePanel = p;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            BackgroundPanel currentPanel = (BackgroundPanel)((Button)e.getSource()).getParent().getParent();
            EditProfilePanel editProfilePanel = new EditProfilePanel(((Button)e.getSource()).getActionCommand(), customer, profilePanel);
            currentPanel.add("editProfilePanel", editProfilePanel);
            editProfilePanel.setDefaultButton();
            ((CardLayout)currentPanel.getLayout()).show(profilePanel.getParent(), "editProfilePanel");
            currentPanel.revalidate();
        }
    }
}
