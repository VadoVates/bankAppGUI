public class VIPClient extends Client {
    private double extraInterest;

    public VIPClient(String firstName, String lastName, double balance, double interest, double extraInterest) {
        super(firstName, lastName, balance, interest);
        this.extraInterest = extraInterest;
    }

    public double getExtraInterest() {
        return extraInterest;
    }

    public void setExtraInterest(double extraInterest) {
        this.extraInterest = extraInterest;
    }

    @Override
    public String toString() {
        return "VIPClient{" +
                "balance=" + getBalance() +
                ", id=" + getId() +
                ", firstName=" + getFirstName() +
                ", lastName=" + getLastName() +
                ", interest=" + getInterest() +
                ", extraInterest=" + extraInterest +
                '}';
    }
}
