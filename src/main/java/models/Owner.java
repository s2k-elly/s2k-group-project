package models;

/**
 * Owner/Admin class.
 * Pretty much the same as User but with OWNER role.
 * Gives access to all the inventory-related permissions.
 */

public class Owner extends User {
// [Katramados] ChatGPT assisted, just passes OWNER role, Finalized Nov. 30th
    public Owner(String username, String password) {
        super(username, password, Role.OWNER);
    }
// [Katramados] Owner string output (no description fields), Finalized Nov. 30th
    @Override
    public String toString() {
        return "Owner{id=" + getID() +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
