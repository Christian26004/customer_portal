package com.customerportal.model;

public class Address {

    private int addressId;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address(int addressId, String street, String city, String state, String zipCode) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public int getAddressId() {
        return addressId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getstate() {
        return state;
    }

    public String getzipCode() {
        return zipCode;
    }

    public void setStreet(String street) {
            this.street = street;
    }

        public void setCity(String city) {
            this.city = city;
    }

        public void setState(String state) {
            this.state = state;
    }

        public void setzipCode(String zipCode) {
            this.zipCode = zipCode;
    }

}
