package com.CabBooking.Controller;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.Customer;
import com.CabBooking.Model.mapgraphutilities.EdgeWeightedGraph;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.Button;
import com.CabBooking.View.TextField;
import com.CabBooking.View.*;

import com.google.gson.Gson;
import com.mongodb.MongoSecurityException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Code for sign in window
 */

public class SignInPanel  extends BackgroundPanel {
    private final MainFrame mainFrame;
    private UserPanel userPanel;

    public SignInPanel(MainFrame mf) {
        super(true);
        mainFrame = mf;
        setLayout(null);
        setSize(1200, 600);
        createUIComponents();
    }

    /**
     * Create and add all launch window components
     */
    private void createUIComponents() {
        final Color fieldColor = Color.decode("#4c0080");
        final TextField usernameTextField = new TextField(CommonConstants.LAUNCH_WINDOW_LEFT, CommonConstants.LAUNCH_WINDOW_Y_REF, CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH,
                CommonConstants.LAUNCH_WINDOW_WIDTH, fieldColor, Color.WHITE, Color.BLACK);
        final PasswordField passwordField = new PasswordField(CommonConstants.LAUNCH_WINDOW_LEFT, CommonConstants.LAUNCH_WINDOW_Y_REF + 100, CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH,
                CommonConstants.LAUNCH_WINDOW_WIDTH);
        passwordField.setBackground(fieldColor);
        final Button loginButton = new Button(CommonConstants.LAUNCH_WINDOW_LEFT, CommonConstants.LAUNCH_WINDOW_Y_REF + 200, CommonConstants.LAUNCH_WINDOW_Y_REF + 49,
                CommonConstants.LAUNCH_WINDOW_WIDTH, "Login", fieldColor, Color.WHITE);
        final Button signupButton = new Button(CommonConstants.LAUNCH_WINDOW_LEFT + 201, CommonConstants.LAUNCH_WINDOW_Y_REF + 200,
                CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH / 2, CommonConstants.LAUNCH_WINDOW_WIDTH, "Signup", fieldColor, Color.WHITE);
        final TextLabel welcomeMessageText = new TextLabel(200, 150, 400,
                50, Color.WHITE, "Fantasy Cabs", CommonConstants.LAUNCH_MSG_FONT_SIZE);
        final TextLabel userNameTextLabel = new TextLabel(CommonConstants.LAUNCH_WINDOW_LEFT + 2, CommonConstants.LAUNCH_WINDOW_Y_REF - 30, CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH,
                CommonConstants.LAUNCH_WINDOW_WIDTH / 2, Color.WHITE, "Username", 15);
        final TextLabel passwordTextLabel = new TextLabel(CommonConstants.LAUNCH_WINDOW_LEFT + 2, CommonConstants.LAUNCH_WINDOW_Y_REF + 70, CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH,
                CommonConstants.LAUNCH_WINDOW_WIDTH / 2, Color.WHITE, "Password", 15);
        final TextLabel emptyUsernameTextLabel = new TextLabel(CommonConstants.LAUNCH_WINDOW_LEFT + 2, CommonConstants.LAUNCH_WINDOW_Y_REF + 44, CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH,
                CommonConstants.LAUNCH_WINDOW_WIDTH / 2, Color.RED, "Username field cannot be empty!", 14);
        final TextLabel emptyPasswordTextLabel = new TextLabel(CommonConstants.LAUNCH_WINDOW_LEFT + 2, CommonConstants.LAUNCH_WINDOW_Y_REF + 144, CommonConstants.LAUNCH_WINDOW_CREDS_LENGTH,
                CommonConstants.LAUNCH_WINDOW_WIDTH / 2, Color.RED, "Password field cannot be empty!", 14);
        try {
            BufferedImage image = ImageIO.read(new File("src/main/resources/Cab.png"));
            Image carImage = image.getScaledInstance(500, 250, Image.SCALE_SMOOTH);
            final JLabel imageLabel = new JLabel(new ImageIcon(carImage));
            imageLabel.setBounds(100, 200, 500, 250);
            add(imageLabel);
        } catch (IOException ie) {
            System.out.println("Image not Found");
        }


        emptyUsernameTextLabel.setVisible(false);
        emptyPasswordTextLabel.setVisible(false);

        welcomeMessageText.setFont(new Font("SansSerif", Font.BOLD, 45));

        add(emptyUsernameTextLabel);
        add(emptyPasswordTextLabel);
        add(passwordTextLabel);
        add(welcomeMessageText);
        add(usernameTextField);
        add(passwordField);
        add(loginButton);
        add(signupButton);
        add(userNameTextLabel);

        mainFrame.getRootPane().setDefaultButton(loginButton);

        // Define action for login button: Go to user window
        loginButton.addActionListener(e -> {
            // Check for empty username or password field
            if (usernameTextField.getText().equals("") || new String(passwordField.getPassword()).equals("")) {
                setEmptyLabels(usernameTextField, passwordField, emptyUsernameTextLabel, emptyPasswordTextLabel);
            } else {
                // Login user if username and password are correct
                try {
                    MongoDatabase cabBookingDB = Auth.loadUserData(usernameTextField.getText(), passwordField.getPassword());
                    MongoCollection<Document> users = cabBookingDB.getCollection("Users");
                    MongoCollection<Document> map = cabBookingDB.getCollection("Graphs");

                    // Retrieve user information from database and create Customer object through JSON and GSON format
                    Document userInfo = Auth.getUserInfo(usernameTextField.getText(), users);
                    Gson gson = new Gson();
                    Customer customer = gson.fromJson(userInfo.toJson(), Customer.class);
                    EdgeWeightedGraph roadMap = gson.fromJson(Objects.requireNonNull(map.find().first()).toJson(), EdgeWeightedGraph.class);
                    if (customer.isLoggedIn()) {
                        JOptionPane.showMessageDialog(mainFrame, "User already logged in on another device.\nKindly Log Out first.", "Already logged in", JOptionPane.WARNING_MESSAGE);
                    } else {
                        Auth.loginUser(customer, usernameTextField.getText(), passwordField.getPassword());
                        userPanel = new UserPanel(customer, roadMap, mainFrame);
                        mainFrame.setContentPane(userPanel);
                        mainFrame.setUserLoggedIn(true);
                        invalidate();
                        validate();
                    }
                } catch (MongoSecurityException mse) {
                    JOptionPane.showMessageDialog(mainFrame, "Invalid username or password", "Invalid credentials", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Define action for sign up button: Go to sign up window
        signupButton.addActionListener(e -> {
            mainFrame.setContentPane(new SignUpPanel(mainFrame));
            invalidate();
            validate();
        });
        // Aesthetics
        usernameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                emptyUsernameTextLabel.setVisible(false);
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
                emptyPasswordTextLabel.setVisible(false);
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
    }

    /**
     * Method to display suitable error labels
     */
    private void setEmptyLabels(TextField usernameTextField, PasswordField passwordField, TextLabel emptyUsernameTextLabel, TextLabel emptyPasswordTextLabel) {
        if (usernameTextField.getText().equals("")) {
            emptyUsernameTextLabel.setVisible(true);
        }
        if (new String(passwordField.getPassword()).equals("")) {
            emptyPasswordTextLabel.setVisible(true);
        }
    }

    public UserPanel getUserPanel() {
        return userPanel;
    }
    public void setUserPanel(UserPanel up) {
        userPanel = up;
    }
}
