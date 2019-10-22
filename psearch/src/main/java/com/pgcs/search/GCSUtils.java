package com.pgcs.search;

import java.io.IOException;
import java.util.logging.Logger;

//*** PHS: Common tools and shared methods
public class GCSUtils {
    private static final Logger logger = Logger.getLogger(GCSUtils.class.getName());

    public static void log(String logstr) {
        logger.info("====== [PHS Logs] ======> " + logstr);
    } // end Log

} // end class
