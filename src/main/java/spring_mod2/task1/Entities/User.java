package spring_mod2.task1.Entities;

import java.util.UUID;

public abstract class User {
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isActive = true;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = generateUsername();
        this.password = generatePassword();
    }

    protected String generateUsername() {
        return firstName + "." + lastName;
    }

    protected String generatePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
