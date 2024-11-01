package View;

import Controller.ControllerInterface;
import Model.Person.Student;
import ObserverPattern.Observer;
import Model.Service.StudentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel implements Observer {

    private ControllerInterface controller;
    private StudentService studentService;

    // Components
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private TableRowSorter<DefaultTableModel> tableRowSorter;

    // Form components
    private JTextField nameField;
    private JTextField personalNumberField;
    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTextField programField;

    // Buttons
    private JButton addButton;
    private JButton deleteButton;
    private JButton undoButton;
    private JButton redoButton;

    // Search components
    private JTextField searchField;

    public StudentPanel(ControllerInterface controller) {
        this.controller = controller;
        controller.addObserver(this);

        // Instantiate StudentService and register as observer via controller
        studentService = new StudentService(controller);

        initComponents();
        loadStudentData();
        updateUndoRedoButtons();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Student Table
        String[] studentColumns = {"Person ID", "Name", "Personal Number", "Email", "Phone Number", "Program"};
        studentTableModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);

        // Initialize TableRowSorter
        tableRowSorter = new TableRowSorter<>(studentTableModel);
        studentTable.setRowSorter(tableRowSorter);

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        nameField = new JTextField();
        personalNumberField = new JTextField();
        emailField = new JTextField();
        phoneNumberField = new JTextField();
        programField = new JTextField();

        // Add components to form panel
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Personal Number:"));
        formPanel.add(personalNumberField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneNumberField);
        formPanel.add(new JLabel("Program:"));
        formPanel.add(programField);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Student");
        deleteButton = new JButton("Delete Student");
        undoButton = new JButton("<- Undo");
        redoButton = new JButton("Redo ->");
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(undoButton);
        buttonsPanel.add(redoButton);

        // Add action listeners
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        undoButton.addActionListener(e -> {
            controller.undo();
            updateUndoRedoButtons();
        });
        redoButton.addActionListener(e -> {
            controller.redo();
            updateUndoRedoButtons();
        });

        // Combine form and buttons panel
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.add(formPanel, BorderLayout.CENTER);
        lowerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchField = new JTextField(20);
        searchPanel.add(searchField, BorderLayout.CENTER);

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

        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(lowerPanel, BorderLayout.SOUTH);
    }

    private void filterTable(String query) {
        if (query.trim().isEmpty()) {
            tableRowSorter.setRowFilter(null);
        } else {
            tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void addStudent() {
        // Get data from text fields
        String name = nameField.getText().trim();
        String personalNumber = personalNumberField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String program = programField.getText().trim();

        // Simple validation
        if (name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || program.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create student via StudentService
            Student student = studentService.createStudent(name, personalNumber, email, phoneNumber, program);

            // Add student via controller
            controller.addStudent(student);

            // Clear text fields
            nameField.setText("");
            personalNumberField.setText("");
            emailField.setText("");
            phoneNumberField.setText("");
            programField.setText("");

            JOptionPane.showMessageDialog(this, "Student added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = studentTable.convertRowIndexToModel(selectedRow);
            String studentId = (String) studentTableModel.getValueAt(modelRow, 0);
            try {
                Student student = controller.getStudentById(studentId);
                controller.deleteStudent(student);
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadStudentData() {
        studentTableModel.setRowCount(0);
        List<Student> students = controller.getAllStudents();
        for (Student student : students) {
            studentTableModel.addRow(new Object[]{
                    student.getPersonID(),
                    student.getName(),
                    student.getPersonalNumber(),
                    student.getEmail(),
                    student.getPhoneNumber(),
                    student.getProgram()
            });
        }
    }

    private void updateUndoRedoButtons() {
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
    }

    @Override
    public void update(String message) {
        loadStudentData();
        updateUndoRedoButtons();
    }
}
