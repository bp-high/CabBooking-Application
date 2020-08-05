package com.CabBooking.Controller;

import com.CabBooking.Model.Customer;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.*;
import com.CabBooking.View.Button;
import com.CabBooking.View.TextField;
import com.CabBooking.View.MainFrame;
import com.mongodb.MongoSecurityException;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Class to allow user to edit contact number, name and password
 */
class EditProfilePanel extends BackgroundPanel {
    private final String actionCommand;
    private final Customer customer;
    private final Button confirmButton;
    private final PasswordField oldPassTextField;
    private final PasswordField newPassTextField;
    private final PasswordField confirmPasswordField;
    private final TextField newTextField;
    private final TextLabel passwordMismatchLabel;
    private final TextLabel passwordFormatLabel;
    private final String emptyFieldError = "This Field Should Not be Empty!";
    private String passwordError = "Password Do Not Match!";
    private final String passwordFormatError = "<html>Password must be at least 8 characters and contain at least one uppercase and lowercase letter and digit.</html>";

    EditProfilePanel(String ac, Customer c, ProfilePanel p) {
        setLayout(null);
        actionCommand = ac;
        customer = c;
        Button backButton = new Button(400, 500, 99, 50, "Back", Color.BLACK, Color.WHITE);
        confirmButton = new Button(501, 500, 99, 50, "Confirm", Color.BLACK, Color.WHITE);
        oldPassTextField = new PasswordField(500, 100, 300, 50);
        newPassTextField = new PasswordField(500, 225, 300, 50);
        newTextField = new TextField(500, 225, 300, 50, Color.BLACK, Color.WHITE, Color.WHITE, 25);
        TextLabel confirmPasswordLabel = new TextLabel(100, 350, 400, 50, Color.WHITE, "Confirm password : ", 35);
        confirmPasswordField = new PasswordField(500, 350, 300, 50);
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordMismatchLabel = new TextLabel(500, 400, 300, 30, Color.RED, passwordError, 10);
        passwordFormatLabel = new TextLabel(500, 280, 300, 30, Color.RED, passwordFormatError, 10);
        add(backButton);
        add(confirmButton);
        add(confirmPasswordLabel);
        add(confirmPasswordField);
        add(passwordMismatchLabel);
        add(passwordFormatLabel);

        passwordMismatchLabel.setVisible(false);
        passwordFormatLabel.setVisible(false);

        addUIComponents();
        addActionListeners(backButton);
        addDocumentListeners();
    }

    /**
     * Methods to add and display UI components
     */
    private void addUIComponents() {
        TextLabel previousInfo;
        switch (actionCommand) {
            case "name":
                passwordError = "";
                previousInfo = new TextLabel(500, 100, 400, 50, Color.WHITE, customer.getName(), 35);
                displayComponents(previousInfo);
                break;
            case "number":
                passwordError = "";
                previousInfo = new TextLabel(500, 100, 400, 50, Color.WHITE, customer.getMobile(), 35);
                displayComponents(previousInfo);
                break;
            case "password":
                passwordMismatchLabel.setText(passwordError);
                displayPasswordChangeUI();
                break;
        }
    }

    private void displayComponents(TextLabel previousInfo) {
        TextLabel previousLabel = new TextLabel(100, 100, 400, 50, Color.WHITE, "Previous " + actionCommand + " : ", 35);
        TextLabel newLabel = new TextLabel(100, 225, 400, 50, Color.WHITE, "New " + actionCommand + " : ", 35);
        previousLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        newLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(previousLabel);
        add(previousInfo);
        add(newLabel);
        add(newTextField);
    }

    private void displayPasswordChangeUI() {
        TextLabel previousLabel = new TextLabel(100, 100, 400, 50, Color.WHITE, "Previous " + actionCommand + " : ", 35);
        TextLabel newLabel = new TextLabel(100, 225, 400, 50, Color.WHITE, "New " + actionCommand + " : ", 35);
        previousLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        newLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(previousLabel);
        add(newLabel);
        add(oldPassTextField);
        add(newPassTextField);
    }

    private void addActionListeners(Button backButton) {
        backButton.addActionListener(e -> SwingUtilities.invokeLater(() -> ((UserPanel)this.getParent().getParent()).setProfilePanel()));

        Bson filter = eq("username", customer.getUsername());
        confirmButton.addActionListener(e -> {
            Bson update;
            switch (actionCommand) {
                case "name":
                    update = combine(set("userInfo.name", newTextField.getText()));
                    try {
                        Auth.updateUserInfo(filter, update, customer.getUsername(), new String(confirmPasswordField.getPassword()));
                        customer.setName(newTextField.getText());
                        SwingUtilities.invokeLater(() -> ((UserPanel)this.getParent().getParent()).setProfilePanel());

                    } catch (MongoSecurityException ex) {
                        JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Invalid password");
                    }
                    break;
                case "number":
                    update = combine(set("userInfo.mobile", newTextField.getText()));
                    try {
                        if (Auth.mobileMatchesFormat(newTextField.getText())) {
                            Auth.updateUserInfo(filter, update, customer.getUsername(), new String(confirmPasswordField.getPassword()));
                            customer.setMobile(newTextField.getText());
                            SwingUtilities.invokeLater(() -> ((UserPanel)this.getParent().getParent()).setProfilePanel());
                        } else {
                            JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Invalid mobile number.");
                        }
                    } catch (MongoSecurityException ex) {
                        JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Invalid password");
                    }
                    break;
                case "password":
                    try {
                        if (!Auth.passwordMismatchesFormat(new String(newPassTextField.getPassword())) &&
                                new String(confirmPasswordField.getPassword()).equals(new String(newPassTextField.getPassword()))) {
                            Auth.updateUserPassword(customer.getUsername(), new String(oldPassTextField.getPassword()), new String(newPassTextField.getPassword()));
                            SwingUtilities.invokeLater(() -> ((UserPanel)this.getParent().getParent()).setProfilePanel());
                        } else {
                            JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Invalid new password");
                        }
                    } catch (MongoSecurityException ex) {
                        JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Invalid old password");
                    }
            }
        });
    }

    private void addDocumentListeners() {
        newPassTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (Auth.passwordMismatchesFormat(new String(newPassTextField.getPassword()))) {
                    passwordFormatLabel.setVisible(true);
                } else {
                    passwordFormatLabel.setVisible(false);
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (Auth.passwordMismatchesFormat(new String(newPassTextField.getPassword()))) {
                    passwordFormatLabel.setVisible(true);
                } else {
                    passwordFormatLabel.setVisible(false);
                }
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Implementation not needed
            }
        });
        confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                passwordMismatchLabel.setText(passwordError);
                if (new String(confirmPasswordField.getPassword()).equals(new String(newPassTextField.getPassword()))) {
                    passwordMismatchLabel.setVisible(false);
                } else {
                    passwordMismatchLabel.setVisible(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                passwordMismatchLabel.setText(passwordError);
                if (new String(confirmPasswordField.getPassword()).equals(new String(newPassTextField.getPassword()))) {
                    passwordMismatchLabel.setVisible(false);
                } else {
                    passwordMismatchLabel.setVisible(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Implementation not needed
            }
        });
    }

    void setDefaultButton() {
        ((MainFrame)SwingUtilities.getRoot(this)).getRootPane().setDefaultButton(confirmButton);
    }
}
