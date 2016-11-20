package cesi.com.tchatapp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Date helper.
 */
public class DateHelper {

    private static String format = "HH:mm:ss";
    private static SimpleDateFormat formatter = null;

    /**
     * create formatted date from timestamp.
     *
     * @param timestamp the timestamp
     * @return formatted date
     * @throws ParseException the parse exception
     */
    public static String getFormattedDate(long timestamp) throws ParseException {
        if(formatter == null){
            formatter = new SimpleDateFormat(format);
        }
        return formatter.format(new Date(timestamp));
    }
}
