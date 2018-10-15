package xyz.lebalex.daytask;

import java.util.Calendar;

public class ListTasks {
    private int id;
    private String date;
    private String title;
    private Calendar calen;

    public ListTasks(int id, String date, String title, Calendar calen) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.calen = calen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getCalen() {
        return calen;
    }

    public void setCalen(Calendar calen) {
        this.calen = calen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
