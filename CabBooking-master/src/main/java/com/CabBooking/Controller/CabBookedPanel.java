package com.CabBooking.Controller;

import com.CabBooking.Utils.CommonConstants;
import com.CabBooking.Model.Customer;
import com.CabBooking.Model.IntermediatePoint;
import com.CabBooking.Model.mapgraphutilities.Driver;
import com.CabBooking.Model.mapgraphutilities.EdgeWeightedGraph;
import com.CabBooking.Utils.Auth;
import com.CabBooking.View.BackgroundPanel;
import com.CabBooking.View.Button;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Stack;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Implementation of UI for when user is currently on a ride
 */
public class CabBookedPanel extends BackgroundPanel {
    private final EdgeWeightedGraph graph;
    private final UserPanel userPanel;
    private final CabBookedAnimator bookingAnimator;
    private double progress;
    private double xIn;
    private double yProgressBar;
    private double xOut;
    private double xCurr;
    private int[] dist;
    private IntermediatePoint[] points;
    private int pos = 0;
    private boolean booked;
    private boolean reached;
    private String from;
    private String to;
    private int price;
    private double div;
    private int minutes;
    private int seconds;
    private Driver driver;
    private int rating;
    private Button[] starButtons;
    private boolean tripWithoutPay;
    private int finalLocation;

    CabBookedPanel(double[] distances, Stack<Integer> path, EdgeWeightedGraph g, UserPanel up) {
        progress = 0;
        xIn = 250;
        yProgressBar = 250;
        xOut = 750;
        xCurr = 0;
        rating = 0;
        booked = false;
        reached = false;
        graph = g;
        driver = up.getCabBookingPanel().getCurrentDriver();
        userPanel = up;
        tripWithoutPay = true;
        bookingAnimator = new CabBookedAnimator();
        starButtons = new Button[5];
        for (int i = 0; i < 5; i++) {
            starButtons[i] = new Button(300 + (i * 80), 448, 80, 50, "☆", Color.BLACK, Color.WHITE);
            starButtons[i].setFont(new Font(CommonConstants.FONT, Font.PLAIN, 20));
        }
        addStarActionListeners(starButtons);

        // Enable tooltip displaying on intermediate point hover
        ToolTipManager.sharedInstance().registerComponent(this);

        //Scale distances to length of line in UI
        dist = new int[distances.length];
        double multiplier = 500 / distances[distances.length - 1];
        for (int i = 0; i < distances.length; i++) {
            dist[i] = (int)(distances[i] * multiplier);
        }

        // Create all intermediate points
        points = new IntermediatePoint[distances.length];
        for (int i = 0; i < distances.length; i++) {
            points[i] = new IntermediatePoint(dist[i] + xIn - 3, yProgressBar - 3, 6, 6, Integer.toString(path.get(i)));
        }

        // Get source and destination
        from = String.valueOf(path.get(0));
        to = String.valueOf(path.lastElement());
        finalLocation = Integer.parseInt(to);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        bookingAnimator.animate(g);
    }

    /**
     * Util methods to update and track progress
     */
    void updateProgressBooking(double prog, double inc) {
        progress = prog;
        xCurr += inc;
    }

    void updateProgressBooked(int prog, double inc) {
        seconds--;
        if (seconds < 0) {
            minutes--;
        }
        progress = prog;
        xCurr += inc;
    }

    void resetProgress(int t) {
        progress = t;
        xCurr = 0;
    }

    /**
     * Methods to add {@code ActionListener} to {@code payButton} and stars
     */
    private void addPayAction(Button payButton) {
        payButton.addActionListener(e -> {
            int driverTrips = driver.getRatedTrips();
            double previousRating = driver.getRating();
            double newRating = previousRating;
            if (rating != 0) {
                newRating = (previousRating * driverTrips + rating) / (driverTrips + 1);
                driverTrips++;
            }
            driver.setLocation(Integer.parseInt(to));
            Bson filter = and(eq("name", driver.getName()), eq("mobile", driver.getMobile()),
                    eq("vehicleID", Integer.parseInt(driver.getVehicleID())));
            Bson update = combine(set("location", Integer.parseInt(to)), set("rating", newRating), set("ratedTrips", driverTrips));
            Auth.updateDriverStats(filter, update);
            userPanel.getCabBookingPanel().releaseDriver();

            Customer currentCustomer = userPanel.getCustomer();
            currentCustomer.setMoney(currentCustomer.getMoney() - price);
            Auth.updateCustomerWallet(currentCustomer);
            tripWithoutPay = false;
            userPanel.getMenuPanel().enableButtons();
            userPanel.setCabBookedPanel(null);
            userPanel.setWalletPanel();
            userPanel.setCabBookingPanel(graph);
        });
    }

    private void addStarActionListeners(Button[] buttons) {
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            buttons[i].addActionListener(e -> {
                for (int j = 0; j <= finalI; j++) {
                    buttons[j].setText("★");
                }
                for (int j = finalI + 1; j < 5; j++) {
                    buttons[j].setText("☆");
                }
                rating = finalI + 1;
            });
        }
    }

    // Util methods
    @Override
    public String getToolTipText(MouseEvent event) {
        for (IntermediatePoint point : points) {
            if (point.contains(event.getPoint())) {
                return point.toString();
            }
        }
        return null;
    }

    void setBooked() {
        booked = true;
    }

    void setTime(int t) {
        minutes = t / 60;
        seconds = t % 60;
        div = 360.0 / (t);
    }

    void setReached() {
        reached = true;
    }

    void setPrice(int p) {
        price = p;
    }

    public boolean getTripWithoutPayStatus() {
        return tripWithoutPay;
    }
    public int getPrice() {
        return price;
    }
    public int getFinalLocation() {
        return finalLocation;
    }

    /**
     * Class to animate the ride visuals
     */
    private class CabBookedAnimator {
        private void animate(Graphics g) {
            if (!booked) {
                Graphics2D g2 = (Graphics2D) g;
                drawLine(g2, xIn + xCurr);

                for (int i = 0; i < pos; i++) {
                    g2.draw(points[i]);
                    g2.fill(points[i]);
                }
                g2.draw(points[dist.length - 1]);
                g2.fill(points[dist.length - 1]);

                if (pos < dist.length && dist[pos] + xIn <= xCurr + xIn) {
                    pos++;
                }
                g2.setFont(new Font(CommonConstants.FONT, Font.PLAIN, 25));
                g2.drawString(from, 225, 300);
                g2.drawString(to, (int) xOut, 300);
                g2.drawString("Your driver: " + driver.getName(), 200, 350);
                String s = "Rating: " + new DecimalFormat("#.00").format(driver.getRating()) + " ★";
                FontMetrics fontMetrics = g2.getFontMetrics();
                g2.drawString(s, 800 - fontMetrics.stringWidth(s), 350);
                g2.drawString("Contact: " + driver.getMobile(), 200, 400);
                s = "Vehicle ID: " + driver.getVehicleID();
                g2.drawString(s, 800 - fontMetrics.stringWidth(s), 400);
                g2.drawString("Cost: ₹ " + price, 200, 450);

                g2.translate(500, 125);
                g2.rotate(Math.toRadians(270));
                Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);
                Ellipse2D circle = new Ellipse2D.Float(0, 0, 75, 75);
                Ellipse2D circle1 = new Ellipse2D.Float(0, 0, 100, 100);
                circle.setFrameFromCenter(new Point(0, 0), new Point2D.Double(progress * 0.15, progress * 0.15));
                circle1.setFrameFromCenter(new Point(0, 0), new Point(75, 75));
                arc.setFrameFromCenter(new Point(0, 0), new Point(85, 85));
                arc.setAngleStart(0);
                arc.setAngleExtent(-progress * 0.72);
                g2.setColor(Color.RED);
                g2.draw(arc);
                g2.fill(arc);
                g2.setColor(Color.BLACK);
                g2.draw(circle1);
                g2.fill(circle1);
                g2.setColor(Color.WHITE);
                g2.draw(circle);
                g2.fill(circle);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(new Font(CommonConstants.FONT, Font.PLAIN, 25));
                drawLine(g2, xOut);

                g2.drawString("Cost: ₹ " + price, 200, 450);
                g2.setColor(Color.DARK_GRAY);
                drawProgressAndInfo(g2);

                g2.translate(500, 125);
                g2.rotate(Math.toRadians(270));
                Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);
                Ellipse2D circle = new Ellipse2D.Float(0, 0, 75, 75);
                circle.setFrameFromCenter(new Point(0, 0), new Point2D.Double(75, 75));
                arc.setFrameFromCenter(new Point(0, 0), new Point(85, 85));
                arc.setAngleStart(0);
                arc.setAngleExtent(-360.0 + div + progress * div);
                g2.setColor(Color.RED);
                g2.draw(arc);
                g2.fill(arc);

                g2.setColor(Color.WHITE);
                g2.draw(circle);
                g2.fill(circle);

                g2.setColor(Color.BLACK);
                g2.rotate(Math.toRadians(-270));
                g2.drawString(String.format("%02d", minutes) + "m " + String.format("%02d", seconds) + "s", -53, 10);
            }
            if (reached) {
                Graphics2D g2 = (Graphics2D) g;
                g2.translate(-500, -125);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = Color.BLACK;
                Color color2 = new Color(139, 0, 139);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                g2.setColor(Color.DARK_GRAY);
                drawProgressAndInfo(g2);

                g2.translate(500, 125);
                g2.rotate(Math.toRadians(270));
                Ellipse2D circle = new Ellipse2D.Float(0, 0, 75, 75);
                Ellipse2D circle1 = new Ellipse2D.Float(0, 0, 100, 100);
                circle.setFrameFromCenter(new Point(0, 0), new Point2D.Double(progress * 0.15, progress * 0.15));
                circle1.setFrameFromCenter(new Point(0, 0), new Point(75, 75));
                g2.setColor(Color.WHITE);
                g2.draw(circle1);
                g2.fill(circle1);
                g2.setColor(Color.BLACK);
                g2.draw(circle);
                g2.fill(circle);

                g2.setColor(Color.WHITE);
                g2.rotate(Math.toRadians(-270));
                g2.drawString("Reached", -50, 10);

                g2.translate(-500, -125);
                Button payButton = new Button(300, 500, 400, 50, "PAY ₹ " + price, Color.BLACK, Color.WHITE);
                payButton.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
                addPayAction(payButton);
                add(payButton);
                for (int i = 0; i < 5; i++) {
                    add(starButtons[i]);
                }
            }
        }
        /**
         * Util methods for graphics rendering
         */
        private void drawLine(Graphics2D g2, double whiteLength) {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            Color color1 = Color.BLACK;
            Color color2 = new Color(139, 0, 139);
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setColor(Color.WHITE);
            Line2D progressLine = new Line2D.Double(xIn, yProgressBar, whiteLength, yProgressBar);
            g2.setStroke(new BasicStroke(1));
            g2.draw(progressLine);
            g2.fill(progressLine);
        }

        private void drawProgressAndInfo(Graphics2D g2) {
            Line2D progressLine = new Line2D.Double(xIn, yProgressBar, xIn + xCurr, yProgressBar);
            g2.draw(progressLine);
            g2.fill(progressLine);

            g2.setColor(Color.WHITE);
            for (int i = 0; i < pos; i++) {
                g2.draw(points[i]);
                g2.fill(points[i]);
            }
            g2.draw(points[dist.length - 1]);
            g2.fill(points[dist.length - 1]);

            if (pos < dist.length && dist[pos] + xIn <= xCurr) {
                pos++;
            }
            g2.drawString(from, 225, 300);
            g2.drawString(to, (int) xOut, 300);
            g2.drawString("Your driver: " + driver.getName(), 200, 350);
            String s = "Rating: " + new DecimalFormat("#.00").format(driver.getRating()) + " ★";
            FontMetrics fontMetrics = g2.getFontMetrics();
            g2.drawString(s, 800 - fontMetrics.stringWidth(s), 350);
            g2.drawString("Contact: " + driver.getMobile(), 200, 400);
            s = "Vehicle ID: " + driver.getVehicleID();
            g2.drawString(s, 800 - fontMetrics.stringWidth(s), 400);
        }
    }

}
