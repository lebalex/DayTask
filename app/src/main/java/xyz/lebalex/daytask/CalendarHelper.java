package xyz.lebalex.daytask;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarHelper {
    private static final String[] FIELDS = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
    };
    private static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
    private List<ListTasks> eventList = new ArrayList<ListTasks>();
    private Calendar bd;
    private Calendar now;

    public List<ListTasks> getListCalen(Context ctx) {
        //Log.d("CalendarHelper","getListCalen");
        ContentResolver contentResolver;
        contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, "_ID");
        HashSet<String> calendarIds = new HashSet<String>();

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    String name = cursor.getString(1);
                    String displayName = cursor.getString(2);
                    if (name!=null && name.contains("gmail.com"))
                        calendarIds.add(id);
                }


                //////////////
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long d1 = calendar.getTimeInMillis();

                calendar.add(Calendar.DATE, 1);
                long d2 = calendar.getTimeInMillis();

                calendar.add(Calendar.DATE, -1);


                int count = 0;
                for (String id : calendarIds) {

                    Cursor eventCursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
                            (new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventTimezone", "eventLocation", "allDay", "rrule", "_id", "rdate", "original_id","eventStatus"}),
                            "deleted=0 and ((dtstart >=" + d1 + " and dtstart<" + d2 + ") or rrule is not null)  and calendar_id=" + id, null, "dtstart ASC");
//"deleted=0 and (dtstart >=" + d1 + " and dtstart<" + d2 + ") and calendar_id=" + id
                    if (eventCursor.getCount() > 0) {
                        Rrule mRrule;
                        if (eventCursor.moveToFirst()) {
                            do {

                                String title = eventCursor.getString(1);
                                //final String description = eventCursor.getString(2);
                                //long d3 = eventCursor.getLong(3);
                                final Date begin = new Date(eventCursor.getLong(3));
                                //final Date end = new Date(eventCursor.getLong(4));
                                final int all_day = eventCursor.getInt(7);
                                final String rrule = eventCursor.getString(8);
                                String _id = eventCursor.getString(9);
                                final String rdate = eventCursor.getString(10);

                                final String original_id = eventCursor.getString(11);
                                if (original_id != null)
                                    _id = original_id;
                                final String eventStatus = eventCursor.getString(12);

                                if (eventStatus != null){


                                    Calendar bd = Calendar.getInstance();
                                bd.setTime(begin);

                                if (rrule != null) {
                                    mRrule = new Rrule(rrule);

                                    bd = ToNowDate(bd, mRrule.getFreqInt(), mRrule.getInterval());

                                    if (mRrule.getUntil() != null) {
                                        try {
                                            if (bd.after(mRrule.getUntil()))
                                                bd.set(Calendar.YEAR, 1901);
                                        } catch (Exception e) {
                                        }

                                    }


                                }
                                if (bd.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                                        && bd.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                                        && bd.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && title != null) {
                                    //сегодня
                                    if (eventStatus.equalsIgnoreCase("2")) {
                                        if (fintFromList(Integer.parseInt(_id), title, bd, true))
                                            count--;
                                    } else {
                                        if (addToList(Integer.parseInt(_id), all_day, title, bd))
                                            count++;
                                    }

                                }
                            }
                            }
                            while (eventCursor.moveToNext());
                        }

                    }
                }

            }
        } catch (AssertionError ex) {
            Log.d("day task", ex.getMessage(),ex);
        }
        return eventList;
    }

    private Calendar ToNowDate(Calendar bd, int freq, int interval) {
        Calendar now = Calendar.getInstance();
        while (compareDate(bd, now)<0) {
            bd.add(freq, interval);
        }
        return bd;
    }
    private int compareDate(Calendar bd, Calendar now)
    {
        if(bd.get(Calendar.YEAR)!=now.get(Calendar.YEAR))
        {
            return bd.get(Calendar.YEAR)-now.get(Calendar.YEAR);
        }else         if(bd.get(Calendar.MONTH)!=now.get(Calendar.MONTH))
        {
            return bd.get(Calendar.MONTH)-now.get(Calendar.MONTH);
        }else         if(bd.get(Calendar.DAY_OF_MONTH)!=now.get(Calendar.DAY_OF_MONTH))
        {
            return bd.get(Calendar.DAY_OF_MONTH)-now.get(Calendar.DAY_OF_MONTH);
        }else return 0;


    }

    private boolean fintFromList(int _id, String title, Calendar bd, boolean delete) {
        boolean result = false;
        Iterator<ListTasks> l = eventList.iterator();
        while(l.hasNext())
        {
            ListTasks item = l.next();
            if (item.getTitle().equalsIgnoreCase(title) && item.getCalen().equals(bd) && item.getId() == _id) {
                if(delete) l.remove();
                result = true;
            }
        }
        /*for (ListTasks l : eventList
                ) {
            if (l.getTitle().equalsIgnoreCase(title) && l.getCalen().equals(bd) && l.getId() == _id) {
                if(delete) eventList.remove(l);
                result = true;
            }
        }*/
            return result;
    }


    private boolean addToList(int _id, int all_day, String title, Calendar bd){
        boolean result = false;
        if (all_day == 1) {
            if (!fintFromList(_id, title, bd, false)) {
                eventList.add(new ListTasks(_id, "День", title, bd));
                result = true;
            }
        } else {
            String h = "" + bd.get((Calendar.HOUR_OF_DAY));
            if (bd.get((Calendar.HOUR_OF_DAY)) < 10)
                h = "0" + bd.get((Calendar.HOUR_OF_DAY));
            String m = "" + bd.get((Calendar.MINUTE));
            if (bd.get((Calendar.MINUTE)) < 10)
                m = "0" + bd.get((Calendar.MINUTE));
            if (!fintFromList(_id, title, bd, false)) {
                eventList.add(new ListTasks(_id, h + ":" + m, title, bd));
                result = true;
            }
        }
        return result;
    }
}
