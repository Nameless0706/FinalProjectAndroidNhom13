package com.nhom13.phonemart.model.request;

public class CreateUserRequest {
    private String firstName;

    private String lastName;
    private String email;
    private String password;


    public CreateUserRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
