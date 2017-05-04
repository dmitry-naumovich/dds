package com.naumovich.configuration;

/**
 * Created by dzmitry on 2.5.17.
 */
public class AodvConfigurationParameters {

    public static final int ACTIVE_ROUTE_TIMEOUT_MILLIS = 3000;
    public static final int ALLOWED_HELLO_LOSS = 2;
    public static final int HELLO_INTERVAL_MILLIS = 1;
    public static final int LOCAL_ADD_TTL = 2;
    public static final int NET_DIAMETER = 35;
    public static final float MAX_REPAIR_TTL = 0.3f * NET_DIAMETER;
    public static final int MY_ROUTE_TIMEOUT_MILLIS = 2 * ACTIVE_ROUTE_TIMEOUT_MILLIS;
    public static final int NODE_TRAVERSAL_TIME = 40;
    public static final int NEXT_HOP_WAIT = NODE_TRAVERSAL_TIME + 10;
    public static final int NET_TRAVERSAL_TIME = 3 * NODE_TRAVERSAL_TIME * NET_DIAMETER / 2;
    public static final int RREQ_RETRIES = 2;
    public static final int TTL_START = 1;
    public static final int TTL_INCREMENT = 2;
    public static final int TTL_THRESHOLD = 7;


}
