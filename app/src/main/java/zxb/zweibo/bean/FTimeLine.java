package zxb.zweibo.bean;

import java.util.ArrayList;

/**
 * statuses/friends_timeline
 * 获取当前登录用户及其所关注用户的最新微博
 * Created by rex on 15-7-30.
 */
public class FTimeLine {

    /**
     *"statuses": [],
     "advertises": [ ],
     "ad": [ ],
     "hasvisible": false,
     "previous_cursor": 0,
     "next_cursor": 3870323466470248,
     "total_number": 150,
     "interval": 2000,
     "uve_blank": -1,
     "since_id": 3870325844186935,
     "max_id": 3870323466470248,
     "has_unread": 0
     */

    private StatusContent[] statuses;

    private String[] advertises;

    private Ad[] ad;

    private boolean hasvisible;

    /**
     * 暂时不支持
     */
    private int previous_cursor;
    /**
     * 暂时不支持
     */
    private double next_cursor;

    private int total_number;

    private int interval;

    private int uve_blank;

    private double since_id;

    private double max_id;

    private int has_unread;

    public StatusContent[] getStatuses() {
        return statuses;
    }

    public void setStatuses(StatusContent[] statuses) {
        this.statuses = statuses;
    }

    public String[] getAdvertises() {
        return advertises;
    }

    public void setAdvertises(String[] advertises) {
        this.advertises = advertises;
    }

    public Ad[] getAd() {
        return ad;
    }

    public void setAd(Ad[] ad) {
        this.ad = ad;
    }

    public boolean isHasvisible() {
        return hasvisible;
    }

    public void setHasvisible(boolean hasvisible) {
        this.hasvisible = hasvisible;
    }

    public int getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(int previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public double getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(double next_cursor) {
        this.next_cursor = next_cursor;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getUve_blank() {
        return uve_blank;
    }

    public void setUve_blank(int uve_blank) {
        this.uve_blank = uve_blank;
    }

    public double getSince_id() {
        return since_id;
    }

    public void setSince_id(double since_id) {
        this.since_id = since_id;
    }

    public double getMax_id() {
        return max_id;
    }

    public void setMax_id(double max_id) {
        this.max_id = max_id;
    }

    public int getHas_unread() {
        return has_unread;
    }

    public void setHas_unread(int has_unread) {
        this.has_unread = has_unread;
    }
}
