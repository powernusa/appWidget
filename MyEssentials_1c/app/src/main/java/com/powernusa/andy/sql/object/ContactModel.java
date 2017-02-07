package com.powernusa.andy.sql.object;

/**
 * Created by Andy on 1/21/2017.
 */

public class ContactModel {
    private int id;
    private String name, contactNo, email;

    public ContactModel() {

    }

    public ContactModel(String name,String contactNo,String email){
        this.name = name;
        this.contactNo = contactNo;
        this.email = email;
    }

    private byte[] byteArray;

    public byte[] getPhoto() {
        return byteArray;
    }

    public void setPhoto(byte[] array) {
        byteArray = array;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
