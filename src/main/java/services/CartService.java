package services;

import models.*;
import java.util.List;

/**
 * Service responsible for operations on a customer's ShoppingCart.
 * Includes internal stub model classes and a driver main().
 */

public class CartService {

    public void addCart(Customer customer, Videogame vg) {
        customer.getCart().getItems().add(vg);
        System.out.println("Added '" + vg.getTitle() + "' to " + customer.getUsername() + "'s cart.");
    }

    public void removeCart(Customer customer, Videogame vg) {
        if (vg == null) {
            System.out.println("❌ Game does not exist in cart.");
            return;
        }
        boolean removed = customer.getCart().getItems().remove(vg);
    }

    public double total(Customer customer) {
        double sum = 0.0;
        for (Videogame vg : customer.getCart().getItems()) {
            sum += vg.getPrice();
        }
        return sum;
    }

    public void checkout(Customer customer) {
        List<Videogame> items = customer.getCart().getItems();
        if (items.isEmpty()) {
            System.out.println("❌ Cart is empty.");
            return;
        }

        System.out.println("=== Checkout for " + customer.getUsername() + " ===");
        for (Videogame vg : items) {
            System.out.println("- " + vg.getTitle() + " ($" + vg.getPrice() + ")");
        }
        total(customer);
        System.out.println("✔ Payment successful. Thank you!");

        items.clear(); // empty cart after checkout
    }
}
