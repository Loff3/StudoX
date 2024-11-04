package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import Controller.ControllerInterface;
import Model.Person.Student;
import Model.Service.StudentService;
import ObserverPattern.Observer;

public class StudentPanel extends JPanel implements Observer {

    private ControllerInterface controller;
    private StudentService studentService;

    // Components
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private TableRowSorter<DefaultTableModel> tableRowSorter;

    // Form components
    private JTextField nameField, personalNumberField, emailField, phoneNumberField, programField;

    // Edit/Delete components
    private JTextField editNameField, editPersonalNumberField, editEmailField, editPhoneNumberField, editProgramField;
    private JPanel editPanel;
    private JButton updateButton, closeButton;

    // Buttons
    private JButton addButton, deleteButton, undoButton, redoButton;

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

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Student Table
        String[] studentColumns = {"Person ID", "Name", "Personal Number", "Email", "Phone Number", "Program"};
        studentTableModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
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

        // Edit/Delete Panel (initially hidden)
        initEditPanel();

        // Add components to main panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(lowerPanel, BorderLayout.SOUTH);
        add(editPanel, BorderLayout.EAST); // Edit panel on the right

        // Add listener to show edit panel on row selection
        studentTable.getSelectionModel().addListSelectionListener(e -> showEditPanel());
    }

    private void initEditPanel() {
        editPanel = new JPanel(new BorderLayout());
        editPanel.setPreferredSize(new Dimension(250, getHeight()));
        editPanel.setVisible(false); // Start hidden

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        editNameField = new JTextField();
        editPersonalNumberField = new JTextField();
        editEmailField = new JTextField();
        editPhoneNumberField = new JTextField();
        editProgramField = new JTextField();

        fieldsPanel.add(new JLabel("Edit Name:"));
        fieldsPanel.add(editNameField);
        fieldsPanel.add(new JLabel("Edit Personal Number:"));
        fieldsPanel.add(editPersonalNumberField);
        fieldsPanel.add(new JLabel("Edit Email:"));
        fieldsPanel.add(editEmailField);
        fieldsPanel.add(new JLabel("Edit Phone Number:"));
        fieldsPanel.add(editPhoneNumberField);
        fieldsPanel.add(new JLabel("Edit Program:"));
        fieldsPanel.add(editProgramField);

        updateButton = new JButton("Update");
        closeButton = new JButton("Close");
        
        // Action listeners for update and close
        updateButton.addActionListener(e -> updateStudent());
        closeButton.addActionListener(e -> closeEditPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(updateButton);
        buttonPanel.add(closeButton);

        editPanel.add(fieldsPanel, BorderLayout.CENTER);
        editPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showEditPanel() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            editPanel.setVisible(false);
        } else {
            editPanel.setVisible(true);
            loadSelectedStudentIntoEditFields(selectedRow);
        }
    }

    private void loadSelectedStudentIntoEditFields(int viewRow) {
        if (viewRow >= 0) {
            // Konvertera indexet från tabellens vy till modellens index
            int modelRow = studentTable.convertRowIndexToModel(viewRow);
            String studentId = (String) studentTableModel.getValueAt(modelRow, 0);
            
            Student student = controller.getStudentById(studentId);
            if (student != null) {
                editNameField.setText(student.getName());
                editPersonalNumberField.setText(student.getPersonalNumber());
                editEmailField.setText(student.getEmail());
                editPhoneNumberField.setText(student.getPhoneNumber());
                editProgramField.setText(student.getProgram());
            }
        }
    }

    private void closeEditPanel() {
        studentTable.clearSelection();
        editPanel.setVisible(false);
    }

    private void filterTable(String query) {
        tableRowSorter.setRowFilter(query.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + query));
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String personalNumber = personalNumberField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String program = programField.getText().trim();

        if (name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || program.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Student student = studentService.createStudent(name, personalNumber, email, phoneNumber, program);
            controller.addStudent(student);

            // Clear input fields
            nameField.setText("");
            personalNumberField.setText("");
            emailField.setText("");
            phoneNumberField.setText("");
            programField.setText("");

            loadStudentData();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
            try {
                Student student = controller.getStudentById(studentId);
                controller.deleteStudent(student);
                loadStudentData();
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);

            // Prepare updated student object
            Student updatedStudent = new Student(
                studentId, 
                editNameField.getText().trim(), 
                editPersonalNumberField.getText().trim(),
                editEmailField.getText().trim(), 
                editPhoneNumberField.getText().trim(), 
                editProgramField.getText().trim()
            );

            // Call the controller's editStudent method
            controller.editStudent(studentId, updatedStudent);

            // Reload table data to reflect the changes
            loadStudentData();

            // Optionally clear selection or keep editing
            studentTable.clearSelection();
            closeEditPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
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
