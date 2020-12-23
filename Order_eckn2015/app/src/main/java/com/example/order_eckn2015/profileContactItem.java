package com.example.order_eckn2015;

public class profileContactItem {
    private final String contactName;
    private final String contactNum;

    profileContactItem(String contactName, String contactNum) {
        this.contactName = contactName;
        this.contactNum = contactNum;
    }

    public String getName() {
        return contactName;
    }

    public String getContactNum() {
        return contactNum;
    }
}
