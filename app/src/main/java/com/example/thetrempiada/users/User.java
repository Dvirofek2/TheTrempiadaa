package com.example.thetrempiada.users;

import com.example.thetrempiada.UserType;

import java.io.Serializable;

public abstract class  User implements Serializable {

    protected String firstName;
    protected UserType type;
    protected String lastName;

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    protected String id;
    protected String email;
    protected long phone;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserType getType() {
        return type;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString(){
        String s = "";
        s+="First name: "+firstName+"\n Last name: "+lastName+"\n email: "+email+"\n id"+id;
        return s;
    }
}
