package org.example.dsassignment3_4.model;

public class UserProfile {

    private String email;
    private String dob;
    private String gender;
    private String location;

    public UserProfile(String email, String dob,String gender, String location) {
        this.email = email;
        this.dob = dob;
        this.gender=gender;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getLocation() {
        return location;
    }

    public String getGender() {
        return gender;
    }
}
