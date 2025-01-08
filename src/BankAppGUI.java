import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BankAppGUI extends JFrame {
    private static final String FILE_NAME = "clients.dat";
    private final List<Client> clients;
    private final DefaultListModel<String> clientListModel;

    public BankAppGUI () {
        clients = loadClients();
        clientListModel = new DefaultListModel<>();
        updateClientListModel();

        setTitle("Bank App");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel clientPanel = new JPanel(new BorderLayout());
        JList<String> clientList = new JList<>(clientListModel);
        JScrollPane scrollPane = new JScrollPane(clientList);
        clientPanel.add(scrollPane, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Client");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton applyInterestButton = new JButton("Apply Interest");
        JButton findButton = new JButton("Find Client");
        JButton deleteButton = new JButton("Delete Client");

        buttonPanel.add(addButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(applyInterestButton);
        buttonPanel.add(findButton);
        buttonPanel.add(deleteButton);

        addButton.addActionListener(_ -> addClient());
        depositButton.addActionListener(_ -> deposit());
        withdrawButton.addActionListener(_ -> withdraw());
        transferButton.addActionListener(_ -> transfer());
        applyInterestButton.addActionListener(_ -> applyInterest());
        findButton.addActionListener(_ -> findClient());
        deleteButton.addActionListener(_ -> deleteClient());



        add(clientPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);

    }

    private void deleteClient() {
        Client client = selectClient("Select client to delete: ");
        if (client == null) {
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are thou sure thou want to delete THE CLIENT: " + client.getFirstName() + " " + client.getLastName() + "?!",
                "Confirm deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            clients.remove(client);
            updateClientListModel();
            JOptionPane.showMessageDialog(this, "Client deleted!");
        }
    }

    private void findClient() {
        String idString = JOptionPane.showInputDialog(this, "Enter client id: ");
        if (idString == null || idString.isEmpty()) {
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid id!");
            return;
        }

        Client client = clients.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Client not found!");
        } else {
            JOptionPane.showMessageDialog(this, client.toString());
        }
    }

    private void applyInterest() {
        Client client = selectClient();
        if (client == null) {
            return;
        }

        double interest = client.getBalance() * client.getInterest() / 100;

        if (client instanceof VIPClient vipClient) {
            interest += client.getBalance() * vipClient.getExtraInterest() / 100;
        }

        client.setBalance(client.getBalance() + interest);
        updateClientListModel();
        JOptionPane.showMessageDialog(this, "Interest applied! New balance: " + client.getBalance() + ".");
    }

    private void transfer() {
        Client sender = selectClient("Select sender: ");
        if (sender == null) {
            return;
        }

        Client receiver = selectClient("Select receiver: ");
        if (receiver == null) {
            return;
        }

        if (sender == receiver) {
            JOptionPane.showMessageDialog(this, "Sender and receiver cannot be the same!");
            return;
        }

        String amountString = JOptionPane.showInputDialog(this, "Enter transfer amount: ");
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
            return;
        }

        if (amount > sender.getBalance()) {
            JOptionPane.showMessageDialog(this, "Insufficient balance!");
        } else {
            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);
            updateClientListModel();
            JOptionPane.showMessageDialog(this, "Transfer successful! Sender balance: " + sender.getBalance() + ". Receiver balance: " + receiver.getBalance() + ".");
        }
    }

    private void withdraw() {
        Client client = selectClient();
        if (client == null) {
            return;
        }

        String amountString = JOptionPane.showInputDialog(this, "Enter withdrawal amount: ");
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
            return;
        }

        if (amount > client.getBalance()) {
            JOptionPane.showMessageDialog(this, "Insufficient balance!");
        } else {
            client.setBalance(client.getBalance() - amount);
            updateClientListModel();
            JOptionPane.showMessageDialog(this, "Withdrawal successful! New balance: " + client.getBalance() + ".");
        }
    }

    private void deposit() {
        Client client = selectClient();

        if (client == null) {
            return;
        }

        String amountString = JOptionPane.showInputDialog(this, "Enter deposit amount: ");
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
            return;
        }

        client.setBalance(client.getBalance() + amount);
        updateClientListModel();
        JOptionPane.showMessageDialog(this, "Deposit successful! New balance: " + client.getBalance() + ".");
    }

    private Client selectClient() {
        return selectClient("SelectClient");
    }

    private Client selectClient(String message) {
        String[] clientNames = clients
                .stream()
                .map(c -> c.getId() + ": " + c.getFirstName() + " " + c.getLastName())
                .toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, message, "Select Client",
                JOptionPane.QUESTION_MESSAGE, null, clientNames, null);

        if (selected == null || !selected.contains(":")) {
            return null;
        }

        int id = Integer.parseInt(selected.split(":")[0].trim());
        return clients.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    private void addClient() {
        String[] options = {"Regular", "VIP"};
        int clientType = JOptionPane.showOptionDialog(this,
                "Select client type: ",
                "Client type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (clientType == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String firstName = JOptionPane.showInputDialog(this, "Enter first name");
        if (firstName == null || firstName.isEmpty()) {
            return;
        }

        String lastName = JOptionPane.showInputDialog(this, "Enter last name");
        if (lastName == null || lastName.isEmpty()) {
            return;
        }

        String balanceString = JOptionPane.showInputDialog(this, "Enter balance");
        double balance;
        try {
            balance = Double.parseDouble(balanceString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid balance!");
            return;
        }

        String interestString = JOptionPane.showInputDialog(this, "Enter interest");
        if (interestString == null || interestString.isEmpty()) {
            return;
        }
        double interest;
        try {
            interest = Double.parseDouble(interestString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid interest!");
            return;
        }

        if (clientType == 1) { //VIP
            String extraInterestString = JOptionPane.showInputDialog(this, "Enter extra interest: ");
            if (extraInterestString == null || extraInterestString.isEmpty()) {
                return;
            }
            double extraInterest;
            try {
                extraInterest = Double.parseDouble(extraInterestString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid extra interest!");
                return;
            }

            VIPClient vipClient = new VIPClient(firstName, lastName, balance, interest, extraInterest);
            clients.add(vipClient);
            JOptionPane.showMessageDialog(this, "VIP client added!");
        } else {
            Client client = new Client(firstName, lastName, balance, interest);
            clients.add(client);
            JOptionPane.showMessageDialog(this, "Regular client added!");
        }
        updateClientListModel();
    }

    private List<Client> loadClients() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Object o = ois.readObject();
            if (o instanceof List<?> list) {
                List<Client> validClients = new ArrayList<>();

                for (Object client : list) {
                    if (client instanceof Client c) {
                        validClients.add(c);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid client data found. Skipping...");
                    }
                }
                return validClients;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid data format. Starting with an empty list.");
                return new ArrayList<>();
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "No existing data found. Starting with an empty list.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void updateClientListModel() {
        clientListModel.clear();
        for (Client client : clients) {
            if (client instanceof VIPClient vipClient) {
                clientListModel.addElement(String.format("%d: %s %s (Balance: %.2f, Interest: %.2f, Extra interest: %.2f)",
                        client.getId(), client.getFirstName(), client.getLastName(), client.getBalance(),
                        client.getInterest(), vipClient.getExtraInterest()));
            } else {
                clientListModel.addElement(String.format("%d: %s %s (Balance: %.2f, Interest: %.2f)",
                        client.getId(), client.getFirstName(), client.getLastName(), client.getBalance(), client.getInterest()));
            }
        }
    }

    @Override
    public void dispose() {
        System.out.println("Disposing, saving clients");
        saveClients();
        JOptionPane.showMessageDialog(this, "Client data has been saved. Program terminated.");
        super.dispose();
    }

    private void saveClients() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(clients);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving clients: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankAppGUI::new);
    }
}
