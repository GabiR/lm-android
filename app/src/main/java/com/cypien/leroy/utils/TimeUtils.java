package com.cypien.leroy.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by GabiRotaru on 23/08/16.
 */
public class TimeUtils {
    public boolean isMorning() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        try {
            int currHour = Integer.parseInt(sdf.format(calendar.getTime()));
            if(currHour < 20 && currHour > 6)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }
}
