package com.revconnect.model;

public class PersonalUser extends User {

    public PersonalUser() {
        super();
        setUserType(UserType.PERSONAL);
    }

    public PersonalUser(String username, String email, String passwordHash, String fullName) {
        super(username, email, passwordHash, UserType.PERSONAL, fullName);
    }
}