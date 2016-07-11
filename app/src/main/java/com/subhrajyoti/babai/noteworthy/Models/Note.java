package com.subhrajyoti.babai.noteworthy.Models;

import java.util.UUID;

public class Note {

    private int id;
    private String date;
    private String title;
    private String desc;
    private String uid;

    public Note(String title, String desc)
    {
        this.title=title;
        this.desc=desc;
        this.uid = UUID.randomUUID().toString();

    }

    public Note(int id,String title, String desc, String date)
    {
        this.id = id;
        this.title=title;
        this.desc=desc;
        this.uid = UUID.randomUUID().toString();
        this.date = date;

    }

    public Note(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUid() {
        return uid;
    }



}
