package xyz.lebalex.daytask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rrule {
    private String freq;
    private int interval=1;
    private Calendar until=null;
    private String inString;

    //FREQ=MONTHLY;INTERVAL=1;WKST=MO;BYMONTHDAY=20
                                /*freq        = "SECONDLY" / "MINUTELY" / "HOURLY" / "DAILY"
                                        / "WEEKLY" / "MONTHLY" / "YEARLY"*/

    public Rrule(String inString) {
        this.inString = inString;
        Pattern p = Pattern.compile("FREQ=([A-Z]*)");
        Matcher m = p.matcher(inString);
        if (m.find())
            freq = m.group(1);
        p = Pattern.compile("UNTIL=([0-9]*)");
        m = p.matcher(inString);
        if (m.find()) {
            try {
                until = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                until.setTime(sdf.parse(m.group(1)));
            }catch(Exception e){
                until=null;
            }
        }
        p = Pattern.compile("INTERVAL=([0-9]*)");
        m = p.matcher(inString);
        if (m.find()) {
            try {
                interval = Integer.parseInt(m.group(1));
            }catch(Exception e){
                interval=1;
            }
        }
    }

    public String getFreq() {
        return freq;
    }

    public int getInterval() {
        return interval;
    }

    public Calendar getUntil() {
        return until;
    }

    public int getFreqInt() {
        switch (freq) {
            case "WEEKLY":
                return Calendar.WEEK_OF_MONTH;
            case "MONTHLY":
                return Calendar.MONTH;
            case "YEARLY":
                return Calendar.YEAR;
            default:return 0;
        }
    }
}
