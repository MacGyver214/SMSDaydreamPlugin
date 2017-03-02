package com.example.lfswe.smsfetch;

/**
 * An object to represent a message object to be displayed in a ListView
 * Created to supplement the MessageAdapter in order to streamline placement in XML layout
 * Created by MacGyver on 2/27/2017.
 */

public class Message {

    //Instance Data
    private String name;
    private String smsText;

    public Message(String name, String smsText) {
        this.name = name;
        this.smsText = smsText;
    }

    /**
     * A getter and setter for both pieces of instance data
     * @return
     */
    public String getSmsText() {
        return smsText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }
}
