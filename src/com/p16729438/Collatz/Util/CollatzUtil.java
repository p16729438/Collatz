package com.p16729438.Collatz.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CollatzUtil {
    public static String getTimeStamp() {
        return new SimpleDateFormat("[yyyy-MM-dd (EEE)  a hh:mm:ss]     ").format(new Date());
    }
}