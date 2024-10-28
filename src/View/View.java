package View;

import Controller.ControllerInterface;
import Model.Person.Student;
import ObserverPattern.Observer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class View extends JFrame implements Observer {

    private ControllerInterface controller;

    // Table components
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableRowSorter;

    // Form components
    private JTextField idField;
    private JTextField nameField;
    private JTextField personalNumberField;
    private JTextField emailField;
    private JTextField programField;
    private JButton addButton;
    private JButton deleteButton;

    // Search components
    private JTextField searchField;
    // Removed searchButton and resetButton since filtering is dynamic

    public View() {
        super("StudioX");

        // Initialize components
        initComponents();

        // Set up the frame
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setController(ControllerInterface controller) {
        this.controller = controller;
        // Load initial data
        loadStudentData();
    }

    private void initComponents() {
        // Set layout
        setLayout(new BorderLayout());

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new Object[]{"Person ID", "Name", "Personal Number", "Email", "Program"}, 0) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);

        // Initialize TableRowSorter
        tableRowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(tableRowSorter);

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        // Initialize form components
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        idField = new JTextField();
        nameField = new JTextField();
        personalNumberField = new JTextField();
        emailField = new JTextField();
        programField = new JTextField();

        addButton = new JButton("Add Student");
        deleteButton = new JButton("Delete Selected Student");

        // Search components
        searchField = new JTextField(20);

        // Add components to form panel
        formPanel.add(new JLabel("Person ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Personal Number:"));
        formPanel.add(personalNumberField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Program:"));
        formPanel.add(programField);
        formPanel.add(addButton);
        formPanel.add(deleteButton);

        // Add action listeners
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        // Removed search buttons

        // Add DocumentListener to searchField
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not needed for plain text components
            }
        });

        // Add components to frame
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }

    private void filterTable(String query) {
        if (query.trim().length() == 0) {
            tableRowSorter.setRowFilter(null);
        } else {
            // Case-insensitive regex filter
            tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void addStudent() {
        // Get data from text fields
        String personID = idField.getText().trim();
        String name = nameField.getText().trim();
        String personalNumber = personalNumberField.getText().trim();
        String email = emailField.getText().trim();
        String program = programField.getText().trim();

        // Simple validation
        if (personID.isEmpty() || name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || program.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new student object
        Student student = new Student(personID, name, personalNumber, email, program);

        try {
            // Add student via controller
            controller.addStudent(student);

            // Clear text fields
            idField.setText("");
            nameField.setText("");
            personalNumberField.setText("");
            emailField.setText("");
            programField.setText("");

            JOptionPane.showMessageDialog(this, "Student added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert the selected row index in the view to the model index
            int modelRow = studentTable.convertRowIndexToModel(selectedRow);
            String studentId = (String) tableModel.getValueAt(modelRow, 0);
            try {
                controller.deleteStudent(studentId);
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadStudentData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get all students from the controller
        List<Student> students = controller.getAllStudents();

        // Add students to the table model
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getPersonID(),
                    student.getName(),
                    student.getPersonalNumber(),
                    student.getEmail(),
                    student.getProgram()
            });
        }
    }

    @Override
    public void update(String message) {
        // Reload student data when notified
        loadStudentData();

        // Optionally display the message
        System.out.println("Update received: " + message);
    }
}
