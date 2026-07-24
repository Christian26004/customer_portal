package com.customerportal.model;

public class Customer {

    private int customerId;
    private String name;
    private  String email;
    private String password; 

    public Customer(int customerId, String name, String email, String password) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Customer(String name, String email, String password) {
        this(0, name, email, password);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    
}
