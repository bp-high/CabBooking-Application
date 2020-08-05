package com.CabBooking.Controller;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.Customer;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.BackgroundPanel;
import com.CabBooking.View.MainFrame;
import com.CabBooking.View.TextLabel;
import com.CabBooking.View.TextField;
import com.CabBooking.View.Button;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.DecimalFormat;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

/**
 * Code for managing wallet
 */

class WalletPanel extends BackgroundPanel {
    private final Customer customer;
    private final TextLabel balanceLabel;
    private final TextField addBalanceField;
    private final TextLabel newBalanceLabel;
    private final Button plusButton;
    private final Button minusButton;
    private final Button addButton;
    private final Button hundredButton;
    private final Button twoHundredButton;
    private final Button fiveHundredButton;
    private final Button thousandButton;

    /**
     * Create and display wallet info and action interface
     * @param mf Parent frame
     * @param c Current customer
     */
    WalletPanel(MainFrame mf, Customer c) {
        customer = c;
        setLayout(null);

        TextLabel currentBalance = new TextLabel(300, 50, 400, 50, Color.WHITE, "Current Balance", 35);
        balanceLabel = new TextLabel(300, 100, 400, 50, Color.WHITE, "₹ " + ((customer.getMoney() == 0) ? "0" : "") + new DecimalFormat("#.00").format(customer.getMoney()), 35);
        TextLabel plusLabel = new TextLabel(450, 150, 100, 75, Color.WHITE, "+", 35);
        addBalanceField = new TextField(400, 225, 200, 100, Color.BLACK, Color.WHITE, Color.WHITE, 35);
        minusButton = new Button(300, 225, 100, 100, "-", Color.BLACK, Color.WHITE, 35);
        plusButton = new Button(600, 225, 100, 100, "+", Color.BLACK, Color.WHITE, 35);
        TextLabel arrowLabel = new TextLabel(450, 325, 100, 75, Color.WHITE, "↓", 35);
        newBalanceLabel = new TextLabel(300, 400, 400, 50, Color.WHITE, "₹ "  + ((customer.getMoney() == 0) ? "0" : "") + new DecimalFormat("#.00").format(customer.getMoney()), 35);
        hundredButton = new Button(300, 475, 99, 50, "₹ 100", Color.BLACK, Color.WHITE);
        twoHundredButton = new Button(400, 475, 99, 50, "₹ 200", Color.BLACK, Color.WHITE);
        fiveHundredButton = new Button(500, 475, 99, 50, "₹ 500", Color.BLACK, Color.WHITE);
        thousandButton = new Button(600, 475, 100, 50, "₹ 1000", Color.BLACK, Color.WHITE);
        addButton = new Button(400, 545, 200, 50, "Add Money", Color.BLACK, Color.WHITE);
        addButton.setFont(new Font(CommonConstants.FONT, Font.PLAIN, 17)); // Can set font size according to system to maintain consistency

        addBalanceField.setText("0");
        currentBalance.setHorizontalAlignment(SwingConstants.CENTER);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        plusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addBalanceField.setHorizontalAlignment(SwingConstants.CENTER);
        arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newBalanceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(currentBalance);
        add(balanceLabel);
        add(plusLabel);
        add(addBalanceField);
        add(plusButton);
        add(minusButton);
        add(addButton);
        add(arrowLabel);
        add(hundredButton);
        add(twoHundredButton);
        add(fiveHundredButton);
        add(thousandButton);
        add(newBalanceLabel);
        setLayout(new BorderLayout());
        setVisible(true);

        mf.getRootPane().setDefaultButton(addButton);
        addButtonActionListeners();
    }

    /**
     * Method to add all action listeners to change fields when amount added is changed
     */
    private void addButtonActionListeners() {
        addBalanceField.getDocument().addDocumentListener(new DocumentListener() {
            // Update field if amount added is changed
            @Override
            public void insertUpdate(DocumentEvent de) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(Integer.parseInt(addBalanceField.getText()) + customer.getMoney()));
                    } catch (NumberFormatException ex) {
                        String adding = addBalanceField.getText();
                        addBalanceField.setText(adding.substring(0, adding.length() - 1));
                        newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(customer.getMoney()));
                    }
                });
            }
            @Override
            public void removeUpdate(DocumentEvent de) {
                if (!newBalanceLabel.getText().equals("")) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(Integer.parseInt(addBalanceField.getText()) + customer.getMoney()));
                        } catch (NumberFormatException ex) {
                            newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(customer.getMoney()));
                        }
                    });
                }
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                // Implementation not needed
            }
        });

        // Add action listeners to change fields if amount added is changed
        plusButton.addActionListener(e -> {
            try {
                addBalanceField.setText(String.valueOf(Integer.parseInt(addBalanceField.getText()) + 100));
                newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(Integer.parseInt(addBalanceField.getText()) + customer.getMoney()));
            } catch (NumberFormatException ex) {
                addBalanceField.setText("100");
                newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(100 + customer.getMoney()));
            }
        });
        minusButton.addActionListener(e -> {
            int currentValue = Integer.parseInt(addBalanceField.getText());
            if (currentValue >= 100) {
                addBalanceField.setText(String.valueOf(Integer.parseInt(addBalanceField.getText()) - 100));
                newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(Integer.parseInt(addBalanceField.getText()) + customer.getMoney()));
            } else {
                addBalanceField.setText("0");
                newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(customer.getMoney()));
            }
        });
        hundredButton.addActionListener(e -> setMoney(100));
        twoHundredButton.addActionListener(e -> setMoney(200));
        fiveHundredButton.addActionListener(e -> setMoney(500));
        thousandButton.addActionListener(e -> setMoney(1000));
        addButton.addActionListener(e -> {
            if (!addBalanceField.getText().equals("0")) {
                int newAmount;
                try {
                    newAmount = Integer.parseInt(addBalanceField.getText()) + customer.getMoney();
                } catch (NumberFormatException ex) {
                    newAmount = 0;
                }
                String uid = customer.getUsername();
                MongoDatabase userDatabase = Auth.getUsersDatabase();
                MongoCollection<Document> collection = userDatabase.getCollection("Users");
                customer.setMoney(newAmount);
                collection.updateOne(eq("username", uid), combine(set("userInfo.wallet", newAmount)));

                balanceLabel.setText(newBalanceLabel.getText());
                new Thread(() -> {
                    addButton.setForeground(Color.GREEN);
                    addButton.setFont(new Font(CommonConstants.FONT, Font.PLAIN, 25));
                    addButton.setText("✓");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    addButton.setForeground(Color.WHITE);
                    addButton.setFont(new Font(CommonConstants.FONT, Font.PLAIN, 17));
                    addButton.setText("Add money");
                }).start();
                addBalanceField.setText("0");
            }
        });
    }

    // Util methods
    private void setMoney(int m) {
        addButton.setText("Add Money");
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font(CommonConstants.FONT, Font.PLAIN, 17));
        addBalanceField.setText(String.valueOf(m));
        newBalanceLabel.setText("₹ " + new DecimalFormat("#.00").format(Integer.parseInt(addBalanceField.getText()) + customer.getMoney()));
    }
}
