/*
 * To compile and run this program:
 * 
 * Compile:
 * javac ExpenseTracker.java
 * 
 * Run:
 * java ExpenseTracker
 */

 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import java.io.*;
 import java.time.LocalDate;
 import java.time.format.DateTimeFormatter;
 import java.time.format.DateTimeParseException;
 import java.util.ArrayList;
 
 class Expense implements Serializable {
     private static final long serialVersionUID = 1L;
     private String description;
     private double amount;
     private LocalDate date;
     private String category;
 
     public Expense(String description, double amount, LocalDate date, String category) {
         this.description = description;
         this.amount = amount;
         this.date = date;
         this.category = category;
     }
 
     public String getDescription() { return description; }
     public double getAmount() { return amount; }
     public LocalDate getDate() { return date; }
     public String getCategory() { return category; }
 
     @Override
     public String toString() {
         return String.format("Date: %s, Description: %s, Amount: %.2f, Category: %s",
                 date.format(DateTimeFormatter.ofPattern("dd/MM/yy")), description, amount, category);
     }
 }
 
 public class ExpenseTracker extends JFrame {
     private ArrayList<Expense> expenses;
     private JTextArea expenseDisplay;
     private JTextField descriptionField, amountField, dateField;
     private JComboBox<String> categoryComboBox;
 
     public ExpenseTracker() {
         expenses = new ArrayList<>();
         loadExpenses();
         setupUI();
     }
 
     private void setupUI() {
         setTitle("Expense Tracker");
         setSize(400, 400);
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         setLayout(new BorderLayout());
 
         // Input panel
         JPanel inputPanel = new JPanel();
         inputPanel.setLayout(new GridLayout(5, 2));
 
         inputPanel.add(new JLabel("Description:"));
         descriptionField = new JTextField();
         inputPanel.add(descriptionField);
 
         inputPanel.add(new JLabel("Amount:"));
         amountField = new JTextField();
         inputPanel.add(amountField);
 
         inputPanel.add(new JLabel("Date (dd/MM/yyyy):"));
         dateField = new JTextField();
         inputPanel.add(dateField);
 
         inputPanel.add(new JLabel("Category:"));
         String[] categories = { "Food", "Transport", "Utilities", "Entertainment", "Health" };
         categoryComboBox = new JComboBox<>(categories);
         inputPanel.add(categoryComboBox);
 
         JButton addButton = new JButton("Add Expense");
         addButton.addActionListener(e -> addExpense());
         inputPanel.add(addButton);
 
         add(inputPanel, BorderLayout.NORTH);
 
         // Display area
         expenseDisplay = new JTextArea();
         expenseDisplay.setEditable(false);
         add(new JScrollPane(expenseDisplay), BorderLayout.CENTER);
 
         JButton viewButton = new JButton("View Expenses");
         viewButton.addActionListener(e -> viewExpenses());
         add(viewButton, BorderLayout.SOUTH);
     }
 
     private void addExpense() {
         String description = descriptionField.getText();
         double amount;
         LocalDate date;
         String category = (String) categoryComboBox.getSelectedItem();
 
         try {
             amount = Double.parseDouble(amountField.getText());
             date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
 
             if (amount < 0) throw new NumberFormatException();
 
             expenses.add(new Expense(description, amount, date, category));
             saveExpenses();
             clearInputFields();
             JOptionPane.showMessageDialog(this, "Expense added successfully!");
         } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a non-negative number.");
         } catch (DateTimeParseException e) {
             JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/MM/yyyy.");
         }
     }
 
     private void clearInputFields() {
         descriptionField.setText("");
         amountField.setText("");
         dateField.setText("");
         categoryComboBox.setSelectedIndex(0);
     }
 
     private void viewExpenses() {
         StringBuilder displayText = new StringBuilder();
         for (Expense expense : expenses) {
             displayText.append(expense).append("\n");
         }
         expenseDisplay.setText(displayText.toString());
     }
 
     private void loadExpenses() {
         try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("expenses.dat"))) {
             expenses = (ArrayList<Expense>) ois.readObject();
             JOptionPane.showMessageDialog(this, "Expenses loaded successfully.");
         } catch (FileNotFoundException e) {
             // No previous expense data found
         } catch (IOException | ClassNotFoundException e) {
             JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
         }
     }
 
     private void saveExpenses() {
         try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("expenses.dat"))) {
             oos.writeObject(expenses);
             JOptionPane.showMessageDialog(this, "Expenses saved successfully.");
         } catch (IOException e) {
             JOptionPane.showMessageDialog(this, "Error saving expenses: " + e.getMessage());
         }
     }
 
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             ExpenseTracker tracker = new ExpenseTracker();
             tracker.setVisible(true);
         });
     }
 }
 