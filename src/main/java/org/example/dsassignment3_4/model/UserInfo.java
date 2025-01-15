package org.example.dsassignment3_4.model;

public class UserInfo extends User{

    private int age;
    private String location;
    private String hobbies;
    private String education;

    public UserInfo(String username, int age, String location, String hobbies, String education) {
        super(username);
        this.age = age;
        this.location = location;
        this.hobbies = hobbies;
        this.education = education;
    }

    public UserInfo(int age, String location, String hobbies, String education) {
        this.age = age;
        this.location = location;
        this.hobbies = hobbies;
        this.education = education;
    }

    public int getAge() {
        return age;
    }

    public String getLocation() {
        return location;
    }

    public String getHobbies() {
        return hobbies;
    }

    public String getEducation() {
        return education;
    }
}
