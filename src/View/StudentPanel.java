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
    // For Add Student Form
    private JPanel addFormPanel;
    private JTextField addNameField;
    private JTextField addPersonalNumberField;
    private JTextField addEmailField;
    private JTextField addPhoneNumberField;
    private JTextField addProgramField;

    // For Update Student Form
    private JPanel updateFormPanel;
    private JTextField updateNameField;
    private JTextField updatePersonalNumberField;
    private JTextField updateEmailField;
    private JTextField updatePhoneNumberField;
    private JTextField updateProgramField;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton undoButton;
    private JButton redoButton;

    // Search components
    private JTextField searchField;

    // Form visibility flags
    private boolean addFormVisible = false;
    private boolean updateFormVisible = false;

    // Selected student for update
    private Student selectedStudent;

    // Panel to hold forms
    private JPanel formContainer;

    public StudentPanel(ControllerInterface controller) {
        this.controller = controller;
        controller.addObserver(this);

        initComponents();
        loadStudentData();
        updateUndoRedoButtons();
        addInitialStudents();
    }

    private void addInitialStudents() {
        try {
            // Add initial students within try-catch to handle exceptions
            controller.addStudent("Alice Smith", "1234567890", "alice@example.com", "+1234567890", "Computer Science");
            controller.addStudent("Bob Johnson", "0987654321", "bob@example.com", "+0987654321", "Mathematics");
            controller.addStudent("Carol Williams", "1122334455", "carol@example.com", "+1122334455", "Physics");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding initial students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Student Form");
        updateButton = new JButton("Update Student Form");
        deleteButton = new JButton("Delete Student");
        undoButton = new JButton("<- Undo");
        redoButton = new JButton("Redo ->");
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        updateButton.setEnabled(false); // Initially disabled

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(undoButton);
        buttonsPanel.add(redoButton);

        // Add action listeners
        addButton.addActionListener(e -> toggleAddForm());
        updateButton.addActionListener(e -> toggleUpdateForm());
        deleteButton.addActionListener(e -> deleteStudent());
        undoButton.addActionListener(e -> {
            try {
                controller.undo();
                updateUndoRedoButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during undo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        redoButton.addActionListener(e -> {
            try {
                controller.redo();
                updateUndoRedoButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during redo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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

        // Form Container with CardLayout to hold forms
        formContainer = new JPanel(new CardLayout());
        initAddFormPanel();
        initUpdateFormPanel();

        // Empty panel when no form is displayed
        JPanel emptyPanel = new JPanel();
        formContainer.add(emptyPanel, "EMPTY");
        formContainer.add(addFormPanel, "ADD_FORM");
        formContainer.add(updateFormPanel, "UPDATE_FORM");

        // Initially show empty panel
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        cardLayout.show(formContainer, "EMPTY");

        // Lower Panel
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.add(formContainer, BorderLayout.CENTER);
        lowerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add selection listener to studentTable
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    updateButton.setEnabled(true);
                    int modelRow = studentTable.convertRowIndexToModel(selectedRow);
                    String studentId = (String) studentTableModel.getValueAt(modelRow, 0);
                    try {
                        selectedStudent = controller.getStudentById(studentId);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error retrieving student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        selectedStudent = null;
                        updateButton.setEnabled(false);
                    }
                } else {
                    updateButton.setEnabled(false);
                    selectedStudent = null;
                }
            }
        });

        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(lowerPanel, BorderLayout.SOUTH);
    }

    private void initAddFormPanel() {
        addFormPanel = new JPanel(new BorderLayout());

        // Close Button
        JButton addFormCloseButton = new JButton("X");
        addFormCloseButton.addActionListener(e -> {
            hideForms();
            addFormVisible = false;
        });

        // Form Fields
        JPanel addFormFieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        addNameField = new JTextField();
        addPersonalNumberField = new JTextField();
        addEmailField = new JTextField();
        addPhoneNumberField = new JTextField();
        addProgramField = new JTextField();

        addFormFieldsPanel.add(new JLabel("Name:"));
        addFormFieldsPanel.add(addNameField);
        addFormFieldsPanel.add(new JLabel("Personal Number:"));
        addFormFieldsPanel.add(addPersonalNumberField);
        addFormFieldsPanel.add(new JLabel("Email:"));
        addFormFieldsPanel.add(addEmailField);
        addFormFieldsPanel.add(new JLabel("Phone Number:"));
        addFormFieldsPanel.add(addPhoneNumberField);
        addFormFieldsPanel.add(new JLabel("Program:"));
        addFormFieldsPanel.add(addProgramField);

        // Submit Button
        JButton addFormSubmitButton = new JButton("ADD");
        addFormSubmitButton.addActionListener(e -> addStudent());

        // Assemble Form Panel
        JPanel addFormTopPanel = new JPanel(new BorderLayout());
        addFormTopPanel.add(new JLabel("Add Student"), BorderLayout.WEST);
        addFormTopPanel.add(addFormCloseButton, BorderLayout.EAST);

        addFormPanel.add(addFormTopPanel, BorderLayout.NORTH);
        addFormPanel.add(addFormFieldsPanel, BorderLayout.CENTER);
        addFormPanel.add(addFormSubmitButton, BorderLayout.SOUTH);
    }

    private void initUpdateFormPanel() {
        updateFormPanel = new JPanel(new BorderLayout());

        // Close Button
        JButton updateFormCloseButton = new JButton("X");
        updateFormCloseButton.addActionListener(e -> {
            hideForms();
            updateFormVisible = false;
        });

        // Form Fields
        JPanel updateFormFieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        updateNameField = new JTextField();
        updatePersonalNumberField = new JTextField();
        updateEmailField = new JTextField();
        updatePhoneNumberField = new JTextField();
        updateProgramField = new JTextField();

        updateFormFieldsPanel.add(new JLabel("Name:"));
        updateFormFieldsPanel.add(updateNameField);
        updateFormFieldsPanel.add(new JLabel("Personal Number:"));
        updateFormFieldsPanel.add(updatePersonalNumberField);
        updateFormFieldsPanel.add(new JLabel("Email:"));
        updateFormFieldsPanel.add(updateEmailField);
        updateFormFieldsPanel.add(new JLabel("Phone Number:"));
        updateFormFieldsPanel.add(updatePhoneNumberField);
        updateFormFieldsPanel.add(new JLabel("Program:"));
        updateFormFieldsPanel.add(updateProgramField);

        // Submit Button
        JButton updateFormSubmitButton = new JButton("UPDATE");
        updateFormSubmitButton.addActionListener(e -> updateStudent());

        // Assemble Form Panel
        JPanel updateFormTopPanel = new JPanel(new BorderLayout());
        updateFormTopPanel.add(new JLabel("Update Student"), BorderLayout.WEST);
        updateFormTopPanel.add(updateFormCloseButton, BorderLayout.EAST);

        updateFormPanel.add(updateFormTopPanel, BorderLayout.NORTH);
        updateFormPanel.add(updateFormFieldsPanel, BorderLayout.CENTER);
        updateFormPanel.add(updateFormSubmitButton, BorderLayout.SOUTH);
    }

    private void toggleAddForm() {
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        if (addFormVisible) {
            cardLayout.show(formContainer, "EMPTY");
            addFormVisible = false;
        } else {
            cardLayout.show(formContainer, "ADD_FORM");
            addFormVisible = true;
            updateFormVisible = false;
        }
    }

    private void toggleUpdateForm() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        if (updateFormVisible) {
            cardLayout.show(formContainer, "EMPTY");
            updateFormVisible = false;
        } else {
            // Populate the update form fields with selected student data
            updateNameField.setText(selectedStudent.getName());
            updatePersonalNumberField.setText(selectedStudent.getPersonalNumber());
            updateEmailField.setText(selectedStudent.getEmail());
            updatePhoneNumberField.setText(selectedStudent.getPhoneNumber());
            updateProgramField.setText(selectedStudent.getProgram());

            cardLayout.show(formContainer, "UPDATE_FORM");
            updateFormVisible = true;
            addFormVisible = false;
        }
    }

    private void hideForms() {
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        cardLayout.show(formContainer, "EMPTY");
        addFormVisible = false;
        updateFormVisible = false;
    }

    private void filterTable(String query) {
        if (query.trim().isEmpty()) {
            tableRowSorter.setRowFilter(null);
        } else {
            tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void addStudent() {
        // Collect data from form fields
        String name = addNameField.getText().trim();
        String personalNumber = addPersonalNumberField.getText().trim();
        String email = addEmailField.getText().trim();
        String phoneNumber = addPhoneNumberField.getText().trim();
        String program = addProgramField.getText().trim();

        // Simple validation (optional)
        if (name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || program.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Pass data to the controller
            controller.addStudent(name, personalNumber, email, phoneNumber, program);
            // Clear text fields
            addNameField.setText("");
            addPersonalNumberField.setText("");
            addEmailField.setText("");
            addPhoneNumberField.setText("");
            addProgramField.setText("");

            JOptionPane.showMessageDialog(this, "Student added successfully!");
            hideForms(); // Hide the form
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        // Collect data from form fields
        String name = updateNameField.getText().trim();
        String personalNumber = updatePersonalNumberField.getText().trim();
        String email = updateEmailField.getText().trim();
        String phoneNumber = updatePhoneNumberField.getText().trim();
        String program = updateProgramField.getText().trim();

        // Simple validation (optional)
        if (name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || program.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Pass data and selectedStudent to the controller
            controller.updateStudent(selectedStudent, name, personalNumber, email, phoneNumber, program);

            // Clear the form fields
            updateNameField.setText("");
            updatePersonalNumberField.setText("");
            updateEmailField.setText("");
            updatePhoneNumberField.setText("");
            updateProgramField.setText("");

            JOptionPane.showMessageDialog(this, "Student updated successfully!");
            hideForms(); // Hide the form
            selectedStudent = null;
            updateButton.setEnabled(false);
            studentTable.clearSelection();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                selectedStudent = null;
                updateButton.setEnabled(false);
                loadStudentData(); // Refresh table
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
