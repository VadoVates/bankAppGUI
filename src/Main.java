public class Main {
    public static void main(String[] args) {
        Client client1 = new Client( "Jan", "Kowalski", 2137.01,10.0);
        VIPClient clientvip1 = new VIPClient("Mieszko", "Pierwszy", 2137.00, 10.0, 1.0);
        Client client2 = new Client( "Jan", "Kowalski", 2137.01,10.0);
        VIPClient clientvip2 = new VIPClient("Mieszko", "Pierwszy", 2137.00, 10.0, 1.0);

        System.out.println(client1);
        System.out.println(clientvip1);
        System.out.println(client2);
        System.out.println(clientvip2);
    }
}