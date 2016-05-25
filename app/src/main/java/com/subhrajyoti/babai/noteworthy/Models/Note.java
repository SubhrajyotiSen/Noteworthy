package com.subhrajyoti.babai.noteworthy.Models;

import java.util.UUID;

public class Note {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;




    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    private String title;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getUid() {
        return uid;
    }

    private String uid;



}
