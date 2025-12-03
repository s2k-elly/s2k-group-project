package models;

/**
 * Customer class.
 * Extends User and adds a ShoppingCart.
 * This is the user type that can actually use the cart + checkout.
 */



public class Customer extends User {

    private final ShoppingCart cart;
// [Katramados] Calls super with CUSTOMER role, Finalized Nov. 30th
    public Customer(String username, String password) {
        super(username, password, Role.CUSTOMER);
        this.cart = new ShoppingCart(this);
    }
// [Katramados] Returns owned cart instance, Finalized Nov. 30th
    public ShoppingCart getCart() {
        return cart;
    }
// [Katramados] Customer-specific string output, Finalized Nov. 30th
    @Override
    public String toString() {
        return "Customer{id=" + getID() +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
