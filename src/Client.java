import java.io.Serializable;

public class Client implements Serializable {
    private static int nextId = 1;
    private final int id;
    private String firstName;
    private String lastName;
    private double balance;
    private double interest;

    public Client(String firstName, String lastName, double balance, double interest) {
        this.balance = balance;
        this.firstName = firstName;
        this.id = nextId++;
        this.interest = interest;
        this.lastName = lastName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Client{" +
                "balance=" + balance +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", interest=" + interest +
                '}';
    }
}
