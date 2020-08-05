package com.CabBooking.Controller;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.mapgraphutilities.DriverReshuffler;
import com.CabBooking.Model.mapgraphutilities.Driver;
import com.CabBooking.Model.mapgraphutilities.EdgeWeightedGraph;
import com.CabBooking.Model.mapgraphutilities.ShortestPath;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.TextField;
import com.CabBooking.View.*;

import com.google.gson.Gson;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Stack;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Implementation of UI when user wants to book a cab
 */
public class CabBookingPanel extends BackgroundPanel {
    private final UserPanel userPanel;
    private final EdgeWeightedGraph roadMap;
    private final TextField fromTextField;
    private final TextField toTextField;
    private final TextLabel errorLabel;
    private final TextLabel priceLabel;
    private final TextLabel timeLabel;
    private final TextLabel nameLabel;
    private final TextLabel ratingLabel;
    private final TextLabel contactLabel;
    private final TextLabel vehicleLabel;
    private final BookButton bookButton;
    private int time;
    private int price;
    private double[] distance;
    private Stack<Integer> path;
    private boolean isValidDestination;
    private Driver currentDriver;
    private int driverLocation;

    /**
     * Initialize the display
     * @param graph The map
     * @param up The parent {@code UserPanel}
     */
    CabBookingPanel(EdgeWeightedGraph graph, UserPanel up) {
        userPanel = up;
        isValidDestination = false;
        roadMap = graph;
        bookButton = new BookButton("BOOK");
        TextLabel pickupLabel = new TextLabel(200, 200, 200, 50, Color.WHITE, "Pickup Point", 22);
        TextLabel destinationLabel = new TextLabel(600, 200, 200, 50, Color.WHITE, "Destination", 22);
        fromTextField = new TextField(200, 250, 200, 50, Color.BLACK, Color.WHITE, Color.WHITE);
        toTextField = new TextField(600, 250, 200, 50, Color.BLACK, Color.WHITE, Color.WHITE);
        TextLabel arrowLabel = new TextLabel(450, 250, 100, 50, Color.WHITE, "→", 90);
        nameLabel = new TextLabel(200, 450, 400, 50, Color.WHITE, "", 25);
        ratingLabel = new TextLabel(600, 450, 200, 50, Color.WHITE, "", 25);
        contactLabel = new TextLabel(200, 500, 400, 50, Color.WHITE, "", 25);
        vehicleLabel = new TextLabel(500, 500, 300, 50, Color.WHITE, "", 25);
        errorLabel = new TextLabel(200, 500, 600, 50, Color.RED, "", 20);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel = new TextLabel(200, 350, 200, 50, Color.WHITE, "", 25);
        timeLabel = new TextLabel(600, 350, 200, 50, Color.WHITE, "", 25);
        priceLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ratingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        vehicleLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        add(bookButton);
        add(pickupLabel);
        add(destinationLabel);
        add(fromTextField);
        add(toTextField);
        add(arrowLabel);
        add(nameLabel);
        add(ratingLabel);
        add(contactLabel);
        add(vehicleLabel);
        add(errorLabel);
        add(priceLabel);
        add(timeLabel);
        setLayout(new BorderLayout());

        nameLabel.setVisible(false);
        contactLabel.setVisible(false);
        ratingLabel.setVisible(false);
        vehicleLabel.setVisible(false);

        addKeyBinding();
        addDocumentListeners();
        setVisible(true);
    }

    /**
     * @param from source location
     * @return {@code Driver} with best rating
     */
    private Driver findDriver(String from) {
        Document bestDriver = new Document();
        double max = 1;
        for (Document driver : Auth.getDriverAtLocation(Integer.parseInt(from))) {
            if (driver.getDouble("rating") > max) {
                max = driver.getDouble("rating");
                bestDriver = driver;
            }
        }
        Gson gson = new Gson();
        Driver d = gson.fromJson(bestDriver.toJson(), Driver.class);
        driverLocation  = d.getLocation();
        return d;
    }

    private void addDocumentListeners() {
        setDocListener(fromTextField, toTextField);
        setDocListener(toTextField, fromTextField);
    }

    /**
     * Add {@code DocumentListener} to both fields
     */
    private void setDocListener(TextField ftf, TextField ttf) {
        CabBookingPanel temporaryCabBookingPanel = this; // For usage within anonymous class
        ftf.getDocument().addDocumentListener(new DocumentListener() {
            // Update cost if source is changed
            @Override
            public void insertUpdate(DocumentEvent de) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        errorLabel.setText("");
                        bookButton.setText("BOOK");
                        nameLabel.setVisible(false);
                        contactLabel.setVisible(false);
                        ratingLabel.setVisible(false);
                        vehicleLabel.setVisible(false);
                        if (currentDriver != null && !(currentDriver.getLocation() != -1 && getDriverDBLocation() == -1)) {
                            updateDriverLocation(driverLocation);
                        }
                        if (!ttf.getText().equals("") && sourceDestinationIsUnique()) {
                            setTimeAndDistance();
                            isValidDestination = true;
                            priceLabel.setText("₹ " + price);
                            timeLabel.setText(getTimeFromSeconds(time));
                        } else {
                            clearInfo();
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                        clearInfo();
                    }
                });
            }
            @Override
            public void removeUpdate(DocumentEvent de) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        errorLabel.setText("");
                        bookButton.setText("BOOK");
                        nameLabel.setVisible(false);
                        contactLabel.setVisible(false);
                        ratingLabel.setVisible(false);
                        vehicleLabel.setVisible(false);
                        if (currentDriver != null && !(currentDriver.getLocation() != -1 && temporaryCabBookingPanel.getDriverDBLocation() == -1)) {
                            updateDriverLocation(driverLocation);
                        }
                        if ((!(ttf.getText().equals("")) || (ftf.getText().equals(""))) && sourceDestinationIsUnique()) {
                            setTimeAndDistance();
                            isValidDestination = true;
                            priceLabel.setText("₹ " + price);
                            timeLabel.setText(getTimeFromSeconds(time));
                        } else {
                            clearInfo();
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                        clearInfo();
                    }
                });
            }
            @Override
            public void changedUpdate(DocumentEvent de) {
                // Implementation not needed
            }
        });
    }

    // Method to add enter button functionality
    private void addKeyBinding() {
        Action action = new AbstractAction("BOOK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookButton.performAction();
            }
        };
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        bookButton.setAction(action);
        bookButton.getActionMap().put("bookAction", action);
        bookButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "bookAction");
    }

    // Util methods
    private void setTimeAndDistance() {
        int from = Integer.parseInt(fromTextField.getText());
        int to = Integer.parseInt(toTextField.getText());

        // Calculate shortest path
        ShortestPath shortestPath = new ShortestPath(roadMap, from);
        path = shortestPath.getPathTo(to);
        distance = new double[path.size()];
        for (int i = 0; i < path.size(); i++) {
            distance[i] = shortestPath.getDistanceTo(path.get(i));
        }

        // Calculate distance and time
        double distanceToTravel = distance[distance.length - 1];
        time = (int) distanceToTravel / CommonConstants.DIST_PER_TIME;
        price = time * CommonConstants.PRICE_PER_SECOND;
    }

    private String getTimeFromSeconds(int t) {
        if (t <= 0) {
            return "00m 00s";
        } else {
            int minutes = t / 60;
            int seconds = t % 60;
            return String.format("%02d", minutes) + "m " + String.format("%02d", seconds) + "s";
        }
    }

    public void updateDriverLocation(int l) {
        Bson filter = and(eq("name", currentDriver.getName()), eq("mobile", currentDriver.getMobile()),
                eq("vehicleID", Integer.parseInt(currentDriver.getVehicleID())));
        Bson update = combine(set("location", l));
        Auth.updateDriverStats(filter, update);
        currentDriver.setLocation(l);
    }
    public int getDriverDBLocation() {
        Document doc = Auth.getCurrentDriver(currentDriver.getName(), currentDriver.getMobile(), Integer.parseInt(currentDriver.getVehicleID()));
        assert doc != null;
        return doc.getInteger("location");
    }
    private boolean sourceDestinationIsUnique() {
        return !fromTextField.getText().equals(toTextField.getText());
    }
    private void clearInfo() {
        isValidDestination = false;
        priceLabel.setText("");
        timeLabel.setText("");
    }
    public Driver getCurrentDriver() {
        return currentDriver;
    }
    public int getDriverLocation() {
        return driverLocation;
    }
    void releaseDriver() {
        currentDriver = null;
    }

    /**
     * Custom class for circular book button
     */
    class BookButton extends JButton {
        BookButton(String text) {
            super(text);
            setBounds(425, 50, 150, 150);
            setBorder(new RoundedBorder(150));
            setBackground(Color.BLACK);
            setForeground(Color.WHITE);
            setOpaque(true);
            setFont(new Font(CommonConstants.FONT, Font.BOLD, 20));
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

            super.paintComponent(g);
        }

        private void performAction() {
            if (isValidDestination && sourceDestinationIsUnique()) {
                if (price <= userPanel.getCustomer().getMoney()) {
                    errorLabel.setText("");
                    if (this.getText().equals("BOOK")) {
                        try {
                            // Get drivers from another location if no drivers are available (Note that the reshuffleThread takes care of the 'if' part)
                            Thread reshuffleThread = new Thread(new DriverReshuffler(Integer.parseInt(fromTextField.getText())));
                            reshuffleThread.start();
                            reshuffleThread.join();
                            currentDriver = findDriver(fromTextField.getText());
                            updateDriverLocation(-1);

                            // Display driver details
                            nameLabel.setText(currentDriver.getName());
                            ratingLabel.setText("Rating: " + new DecimalFormat("#.00").format(currentDriver.getRating()) + " ★");
                            contactLabel.setText("Contact: " + currentDriver.getMobile());
                            vehicleLabel.setText("Vehicle ID: " + currentDriver.getVehicleID());
                            this.setText("Start Ride");
                            nameLabel.setVisible(true);
                            contactLabel.setVisible(true);
                            ratingLabel.setVisible(true);
                            vehicleLabel.setVisible(true);
                            invalidate();
                            validate();
                        } catch (NullPointerException | InterruptedException ex) {
                            ex.printStackTrace();
                            errorLabel.setText("Unable to Find a Cab at the moment");
                        }
                    } else {
                        // Refresh display to correct view if customer has enough money
                        CabBookedPanel cabBookedPanel = new CabBookedPanel(distance, path, roadMap, userPanel);
                        userPanel.setCabBookedPanel(cabBookedPanel);
                        cabBookedPanel.resetProgress(time);
                        cabBookedPanel.setTime(time);
                        cabBookedPanel.setPrice(price);
                        userPanel.getMenuPanel().disableButtons();
                        // Animation stuff
                        new Thread(() -> {
                            for (int i = 0; i <= 500; i++) {
                                int finalI = i;
                                SwingUtilities.invokeLater(() -> {
                                    Thread animationThread = new Thread(cabBookedPanel::repaint);
                                    animationThread.start();
                                    cabBookedPanel.updateProgressBooking(finalI * Math.sin(finalI * Math.PI / 1000), 1);
                                });
                                try {
                                    Thread.sleep(0, 1);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            cabBookedPanel.resetProgress(time);
                            cabBookedPanel.setBooked();
                            Thread reshuffleThread = new Thread(new DriverReshuffler(Integer.parseInt(fromTextField.getText())));
                            reshuffleThread.start();
                            try {
                                Thread.sleep(1000); // Aesthetics
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            for (int i = 0; i < time; i++) {
                                int finalI = i;
                                SwingUtilities.invokeLater(() -> {
                                    Thread animationThread = new Thread(cabBookedPanel::repaint);
                                    animationThread.start();
                                    cabBookedPanel.updateProgressBooked(finalI, 500.0 / time);
                                });
                                try {
                                    Thread.sleep(1000, 0);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            cabBookedPanel.setReached();
                            cabBookedPanel.resetProgress(0);
                            for (int i = 0; i <= 500; i += 2) {
                                int finalI = i;
                                SwingUtilities.invokeLater(() -> {
                                    Thread animationThread = new Thread(cabBookedPanel::repaint);
                                    animationThread.start();
                                    cabBookedPanel.updateProgressBooking(finalI, 2);
                                });
                                try {
                                    Thread.sleep(0, 1);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else {
                    // Else display appropriate message
                    errorLabel.setText("You need ₹ " + (price - userPanel.getCustomer().getMoney()) + " more. Please add more to your wallet.");
                }
            } else {
                errorLabel.setText("Invalid source or destination.");
            }
        }
    }

    /**
     * Implementation for rounded JButton
     */
    static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int r) {
            radius = r;
        }
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
