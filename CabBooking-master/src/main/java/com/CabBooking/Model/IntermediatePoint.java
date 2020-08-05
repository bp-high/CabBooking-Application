package com.CabBooking.Model;

import java.awt.geom.Ellipse2D;

public class IntermediatePoint extends Ellipse2D.Double {
    private String text;
    public IntermediatePoint(double x, double y, double w, double h, String t) {
        super(x, y, w, h);
        text = t;
    }

    @Override
    public String toString() {
        return text;
    }
}
