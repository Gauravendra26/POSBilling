package de.syss.MifareClassicTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeUtil {
    public static String getCurrentDateTime() {
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateTime = dateFormat.format(calendar.getTime());

        return dateTime;
    }

    public static String getCurrentYear() {
        // Get the current year
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String year = yearFormat.format(calendar.getTime());

        return year;
    }

}
