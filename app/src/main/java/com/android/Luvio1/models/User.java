package com.android.Luvio1.models;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class User  implements Serializable{
    @Exclude
    private String avatar;
    private String firstName;
    private String lastName;
    private String birthday;
    private String fsId;
    private String star;
    private String aboutMe;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String avatar, String firstName, String lastName, String birthday, String fsId, String star, String aboutMe) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.fsId = fsId;
        this.star = star;
        this.aboutMe = aboutMe;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getFsId() {
        return fsId;
    }

    public String getStar() {
        return star;
    }

    public String getAboutMe() {
        return aboutMe;
    }




}
