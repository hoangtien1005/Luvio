package com.android.Luvio.models;

public class User {
    String FirstName;
    String LastName;
    String Birthday;
    String Id;
    String Star;
    String AboutMe;
    public User(String firstName, String lastName, String birthday, String id, String star, String aboutMe) {
        FirstName = firstName;
        LastName = lastName;
        Birthday = birthday;
        Id = id;
        Star = star;
        AboutMe = aboutMe;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getBirthday() {
        return Birthday;
    }

    public String getId() {
        return Id;
    }

    public String getStar() {
        return Star;
    }

    public String getAboutMe() {
        return AboutMe;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setStar(String star) {
        Star = star;
    }

    public void setAboutMe(String aboutMe) {
        AboutMe = aboutMe;
    }
}
