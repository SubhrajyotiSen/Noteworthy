package com.example.babai.ranndom2;

import com.orm.SugarRecord;

import java.util.UUID;

public class Note extends SugarRecord {

    public Note(String title, String desc)
    {
        this.title=title;
        this.desc=desc;
        this.uid = UUID.randomUUID().toString();

    }

    public Note(){

    }



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
