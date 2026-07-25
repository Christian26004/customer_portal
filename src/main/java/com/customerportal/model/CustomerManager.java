package com.customerportal.model;

import java.util.ArrayList;
import java.util.List;

public final class CustomerManager {

    private static final List<Customer> customers = new ArrayList<>();
    private static int nextCustomerId = 1;

    private CustomerManager() {
    }

    public static boolean registerCustomer(
            String name,
            String email,
            String password,
            Address address
    ) {
        String cleanedEmail = email.trim();

        if (emailExists(cleanedEmail)) {
            return false;
        }

        Customer customer = new Customer(
                nextCustomerId,
                name.trim(),
                cleanedEmail,
                password
        );

        customer.setAddress(address);
        
        customers.add(customer);
        nextCustomerId++;

        return true;
    }

   public static Customer loginCustomer(String email, String password) {

    System.out.println("Trying to login:");
    System.out.println("Email = " + email);
    System.out.println("Password = " + password);

    for (Customer customer : customers) {

        System.out.println("Stored email = " + customer.getEmail());
        System.out.println("Stored password = " + customer.getPassword());

        if (customer.getEmail().equalsIgnoreCase(email)
                && customer.getPassword().equals(password)) {

            System.out.println("LOGIN SUCCESS");
            return customer;
        }
    }

    System.out.println("LOGIN FAILED");
    return null;
}

    public static boolean emailExists(String email) {
        String cleanedEmail = email.trim();

        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(cleanedEmail)) {
                return true;
            }
        }

        return false;
    }

    public static List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
}