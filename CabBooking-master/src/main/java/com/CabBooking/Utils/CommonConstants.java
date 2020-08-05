package com.CabBooking.Utils;

import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * Class to declare all constants
 */

public class CommonConstants {
    public static final int LAUNCH_WINDOW_LEFT = 700;
    public static final int LAUNCH_WINDOW_CREDS_LENGTH = 400;
    public static final int LAUNCH_WINDOW_WIDTH = 50;
    public static final int LAUNCH_WINDOW_Y_REF = 150;
    public static final int LAUNCH_MSG_FONT_SIZE = 20;
    public static final String IP = "localhost";
    public static final String PORT = "27017";
    public static final String DATABASE = "CabBookingDB";
    public static final int DIST_PER_TIME = 5;
    public static final int PRICE_PER_SECOND = 25;
    public static final int NUMBER_DESTINATIONS = 100;
    public static final String FONT = "Lucida Grande";
    public static final int FONT_SIZE = 13;
    public static final int WINDOW_HEIGHT = SystemUtils.IS_OS_UNIX ? 620 : 635;
}
