package com.CabBooking.Controller;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.Customer;
import com.CabBooking.Model.mapgraphutilities.EdgeWeightedGraph;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.Button;
import com.CabBooking.View.TextField;
import com.CabBooking.View.*;

import com.google.gson.Gson;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Objects;

/**
 * Code for sign up window
 */

class SignUpPanel extends BackgroundPanel {
    private final MainFrame mainFrame;
    private TextField nameTextField;
    private TextField usernameTextField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField userMobileTextField;
    private TextLabel noNameLabel;
    private TextLabel noUsernameLabel;
    private TextLabel noPasswordLabel;
    private TextLabel passwordMismatchLabel;
    private TextLabel noMobileLabel;
    private TextLabel passwordFormatLabel;
    private TextLabel mobileFormatLabel;
    private final String emptyFieldError = "This Field Should Not be Empty!";
    private final String passwordError = "Password Do Not Match!";
    private final String passwordFormatError = "<html>Password must contain between 8 and 16 characters and contain at least one uppercase and lowercase letter and digit.</html>";
    private final String mobileFormatError = "Invalid mobile number";

    SignUpPanel(MainFrame mF) {
        super(true);
        mainFrame = mF;
        setLayout(null);
        setSize(1200, 600);
        createUIComponents();
        setVisible(true);
    }

    /**
     * Create and display UI components
     */
    private void createUIComponents() {
        Color fieldColor = Color.decode("#4c0080");
        final TextLabel welcomeMessageText = new TextLabel(300, 60, 800, 50, Color.WHITE, "Please provide your details to Sign Up", CommonConstants.LAUNCH_MSG_FONT_SIZE + 10);
        final TextLabel nameText = new TextLabel(200, 160, 300, 40, Color.WHITE, "Name", CommonConstants.LAUNCH_MSG_FONT_SIZE - 5);
        final TextLabel usernameText = new TextLabel(700, 160, 300, 40, Color.WHITE, "Username", CommonConstants.LAUNCH_MSG_FONT_SIZE - 5);
        final TextLabel passwordText = new TextLabel(200, 280, 300, 40, Color.WHITE, "Password", CommonConstants.LAUNCH_MSG_FONT_SIZE - 5);
        final TextLabel confirmPasswordText = new TextLabel(700, 280, 300, 40, Color.WHITE, "Confirm Password", CommonConstants.LAUNCH_MSG_FONT_SIZE - 5);
        final TextLabel userMobileText = new TextLabel(200, 410, 300, 30, Color.WHITE, "Mobile number", CommonConstants.LAUNCH_MSG_FONT_SIZE - 5);

        nameTextField = new TextField(200, 200, 300, 50, fieldColor, Color.WHITE, Color.GRAY);
        usernameTextField = new TextField(700, 200, 300, 50, fieldColor, Color.WHITE, Color.GRAY);
        passwordField = new PasswordField(200, 320, 300, 50);
        confirmPasswordField = new PasswordField(700, 320, 300, 50);
        userMobileTextField = new TextField(200, 440, 300, 50, fieldColor, Color.WHITE, Color.BLACK);
        passwordField.setBackground(fieldColor);
        confirmPasswordField.setBackground(fieldColor);

        final Button backButton = new Button(750, 500, 150, 50, "Back", Color.LIGHT_GRAY, Color.BLACK);
        final Button signUpButton = new Button(910, 500, 150, 50, "Sign Up", Color.LIGHT_GRAY, Color.BLACK);

        noNameLabel = new TextLabel(200, 244, 300, 30, Color.RED, emptyFieldError, 15);
        noUsernameLabel = new TextLabel(700, 244, 300, 30, Color.RED, emptyFieldError, 15);
        noPasswordLabel = new TextLabel(200, 364, 300, 30, Color.RED, emptyFieldError, 15);
        passwordMismatchLabel = new TextLabel(700, 364, 300, 30, Color.RED, passwordError, 15);
        noMobileLabel = new TextLabel(200, 486, 300, 30, Color.RED, emptyFieldError, 15);
        passwordFormatLabel = new TextLabel(200, 364, 300, 50, Color.RED, passwordFormatError, 10);
        mobileFormatLabel = new TextLabel(200, 486, 300, 30, Color.RED, mobileFormatError, 15);

        add(welcomeMessageText);
        add(nameText);
        add(passwordText);
        add(userMobileText);
        add(confirmPasswordText);
        add(nameTextField);
        add(passwordField);
        add(userMobileTextField);
        add(confirmPasswordField);
        add(signUpButton);
        add(backButton);
        add(usernameText);
        add(usernameTextField);

        addDocumentListeners();

        add(noNameLabel);
        add(noUsernameLabel);
        add(noMobileLabel);
        add(noPasswordLabel);
        add(passwordMismatchLabel);
        add(noMobileLabel);
        add(passwordFormatLabel);
        add(mobileFormatLabel);

        noNameLabel.setVisible(false);
        noUsernameLabel.setVisible(false);
        noPasswordLabel.setVisible(false);
        passwordMismatchLabel.setVisible(false);
        noMobileLabel.setVisible(false);
        passwordFormatLabel.setVisible(false);
        mobileFormatLabel.setVisible(false);

        mainFrame.getRootPane().setDefaultButton(signUpButton);

        // Define action for back button: Go back to login window
        backButton.addActionListener(e -> {
            mainFrame.setContentPane(new SignInPanel(mainFrame));
            invalidate();
            validate();
        });

        signUpButton.addActionListener(e -> checkSignUpValidityAndRegister(usernameTextField, nameTextField, userMobileTextField, passwordField, confirmPasswordField));

    }

    /**
     * Method to check if all details have been entered on sign up window, display appropriate messages if not
     */
    private void checkSignUpValidityAndRegister(TextField unt, TextField ut, TextField umt, PasswordField pf, PasswordField cpf) {
        String username = unt.getText();
        String name = ut.getText();
        String mobile = umt.getText();
        String password = new String(pf.getPassword());
        String confirmPassword = new String(cpf.getPassword());
        boolean readyToAdd = true;

        noNameLabel.setVisible(false);
        noUsernameLabel.setVisible(false);
        noPasswordLabel.setVisible(false);
        passwordMismatchLabel.setVisible(false);
        noMobileLabel.setVisible(false);
        passwordFormatLabel.setVisible(false);
        mobileFormatLabel.setVisible(false);

        if (username.equals("")) {
            noUsernameLabel.setVisible(true);
            readyToAdd = false;
        }
        if (name.equals("")) {
            noNameLabel.setVisible(true);
            readyToAdd = false;
        }
        // Check if mobile number is empty, if not, then if it is a valid number
        if (mobile.equals("")) {
            noMobileLabel.setVisible(true);
            readyToAdd = false;
        } else if (!Auth.mobileMatchesFormat(userMobileTextField.getText())) {
            mobileFormatLabel.setVisible(true);
            readyToAdd = false;
        }
        // Check if password is empty, if not, then if it is a strong password
        if (password.equals("")) {
            noPasswordLabel.setVisible(true);
            readyToAdd = false;
        } else if (Auth.passwordMismatchesFormat(new String(passwordField.getPassword()))) {
            passwordFormatLabel.setVisible(true);
            readyToAdd = false;
        }
        // Check for validity of password fields
        if (!password.equals(confirmPassword)) {
            passwordMismatchLabel.setText(passwordError);
            passwordMismatchLabel.setVisible(true);
            readyToAdd = false;
        }
        if (password.equals(confirmPassword) && password.equals("")) {
            passwordMismatchLabel.setText(emptyFieldError);
            passwordMismatchLabel.setVisible(true);
            readyToAdd = false;
        }

        // If all information has been provided, create a new user, register and login
        if (readyToAdd) {
            try {
                Auth.registerUser(username, password, name, mobile);

                MongoDatabase userDatabase = Auth.loadUserData(username, password.toCharArray());
                MongoCollection<Document> userList = userDatabase.getCollection("Users");
                MongoCollection<Document> map = userDatabase.getCollection("Graphs");
                Document userInfo = Auth.getUserInfo(username, userList);
                Gson gson = new Gson();
                Customer newCustomer = gson.fromJson(userInfo.toJson(), Customer.class);
                EdgeWeightedGraph roadMap = gson.fromJson(Objects.requireNonNull(map.find().first()).toJson(), EdgeWeightedGraph.class);

                UserPanel userPanel = new UserPanel(newCustomer, roadMap, mainFrame);
                mainFrame.getSignInPanel().setUserPanel(userPanel);
                mainFrame.setContentPane(userPanel);
//                mainFrame.getSignInPanel().setUserPanel(userPanel);
                mainFrame.setUserLoggedIn(true);
            } catch (MongoCommandException e) {
                JOptionPane.showMessageDialog(mainFrame, "User '" + username + "' is already registered.\nPlease try logging in.", "Please try logging in", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Some unexpected error occurred!\n Please try later.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    /**
     * Method to add document listeners to display correct error messages
     */
    private void addDocumentListeners() {
        usernameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                noUsernameLabel.setVisible(false);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                // Implementation not needed
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Implementation not needed
            }
        });
        userMobileTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                noMobileLabel.setVisible(false);
                mobileFormatLabel.setVisible(false);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                mobileFormatLabel.setVisible(false);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Implementation not needed
            }
        });
        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                noNameLabel.setVisible(false);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                // Implementation not needed
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Implementation not needed
            }
        });
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                noPasswordLabel.setVisible(false);
                if (Auth.passwordMismatchesFormat(new String(passwordField.getPassword()))) {
                    passwordFormatLabel.setVisible(true);
                } else {
                    passwordFormatLabel.setVisible(false);
                }
                if (confirmPasswordField.getPassword().length > 0) {
                    passwordMismatchLabel.setText(passwordError);
                    if (new String(confirmPasswordField.getPassword()).equals(new String(passwordField.getPassword()))) {
                        passwordMismatchLabel.setVisible(false);
                    } else {
                        passwordMismatchLabel.setVisible(true);
                    }
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                noPasswordLabel.setVisible(false);
                if (Auth.passwordMismatchesFormat(new String(passwordField.getPassword()))) {
                    passwordFormatLabel.setVisible(true);
                } else {
                    passwordFormatLabel.setVisible(false);
                }
                if (confirmPasswordField.getPassword().length > 0) {
                    passwordMismatchLabel.setText(passwordError);
                    if (new String(confirmPasswordField.getPassword()).equals(new String(passwordField.getPassword()))) {
                        passwordMismatchLabel.setVisible(false);
                    } else {
                        passwordMismatchLabel.setVisible(true);
                    }
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
                if (new String(confirmPasswordField.getPassword()).equals(new String(passwordField.getPassword()))) {
                    passwordMismatchLabel.setVisible(false);
                } else {
                    passwordMismatchLabel.setVisible(true);
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                passwordMismatchLabel.setText(passwordError);
                if (new String(confirmPasswordField.getPassword()).equals(new String(passwordField.getPassword()))) {
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
}
