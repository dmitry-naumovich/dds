package com.naumovich.configuration;

/**
 * List of static parameters defined in AODV algorithm notation
 *
 */
public class AodvConfiguration {

    private static final int ACTIVE_ROUTE_TIMEOUT_MILLIS = 3000;
    public static final int HL_INCREMENT = 2;
    public static final int HL_START = 1;
    public static final int HL_THRESHOLD = 7;
    public static final int MY_ROUTE_TIMEOUT_MILLIS = 2 * ACTIVE_ROUTE_TIMEOUT_MILLIS;
    public static final int NET_DIAMETER = 100; //35
    public static final int NODE_TRAVERSAL_TIME = 40;
    private static final int NET_TRAVERSAL_TIME = 3 * NODE_TRAVERSAL_TIME * NET_DIAMETER / 2;
    public static final int FLOOD_RECORD_TIME = 2 * NET_TRAVERSAL_TIME;
    public static final int REV_ROUTE_LIFE = NET_TRAVERSAL_TIME;
    public static final int RREQ_RETRIES = 2;


}
