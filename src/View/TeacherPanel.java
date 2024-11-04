package View;

import Controller.ControllerInterface;
import Model.Person.Teacher;
import ObserverPattern.Observer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TeacherPanel extends JPanel implements Observer {

    private ControllerInterface controller;

    // Components
    private JTable teacherTable;
    private DefaultTableModel teacherTableModel;
    private TableRowSorter<DefaultTableModel> tableRowSorter;

    // Form components
    // For Add Teacher Form
    private JPanel addFormPanel;
    private JTextField addNameField;
    private JTextField addPersonalNumberField;
    private JTextField addEmailField;
    private JTextField addPhoneNumberField;
    private JTextField addProgramField;
    private JTextField addCoursesField;

    // For Update Teacher Form
    private JPanel updateFormPanel;
    private JTextField updateNameField;
    private JTextField updatePersonalNumberField;
    private JTextField updateEmailField;
    private JTextField updatePhoneNumberField;
    private JTextField updateProgramField;
    private JTextField updateCoursesField;

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

    // Selected teacher for update
    private Teacher selectedTeacher;

    // Panel to hold forms
    private JPanel formContainer;

    public TeacherPanel(ControllerInterface controller) {
        this.controller = controller;
        controller.addObserver(this);

        initComponents();
        loadTeacherData();
        updateUndoRedoButtons();
        addInitialTeachers();
    }

    private void addInitialTeachers() {
        try {
            // Add initial teachers within try-catch to handle exceptions
            controller.addTeacher("Dr. Emily Clark", "2233445566", "emily.clark@university.edu", "+2233445566", "Physics", Arrays.asList("Quantum Mechanics", "Thermodynamics"));
            controller.addTeacher("Dr. Michael Brown", "3344556677", "michael.brown@university.edu", "+3344556677", "Mathematics", Arrays.asList("Calculus", "Algebra"));
            controller.addTeacher("Dr. Sarah Davis", "4455667788", "sarah.davis@university.edu", "+4455667788", "Computer Science", Arrays.asList("Data Structures", "Algorithms"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding initial teachers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Teacher Table
        String[] teacherColumns = {"Person ID", "Name", "Personal Number", "Email", "Phone Number", "Program", "Courses"};
        teacherTableModel = new DefaultTableModel(teacherColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(teacherTableModel);

        // Initialize TableRowSorter
        tableRowSorter = new TableRowSorter<>(teacherTableModel);
        teacherTable.setRowSorter(tableRowSorter);

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(teacherTable);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Teacher Form");
        updateButton = new JButton("Update Teacher Form");
        deleteButton = new JButton("Delete Teacher");
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
        deleteButton.addActionListener(e -> deleteTeacher());
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

        // Add selection listener to teacherTable
        teacherTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow >= 0) {
                    updateButton.setEnabled(true);
                    int modelRow = teacherTable.convertRowIndexToModel(selectedRow);
                    String teacherId = (String) teacherTableModel.getValueAt(modelRow, 0);
                    try {
                        selectedTeacher = controller.getTeacherById(teacherId);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error retrieving teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        selectedTeacher = null;
                        updateButton.setEnabled(false);
                    }
                } else {
                    updateButton.setEnabled(false);
                    selectedTeacher = null;
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
        JPanel addFormFieldsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        addNameField = new JTextField();
        addPersonalNumberField = new JTextField();
        addEmailField = new JTextField();
        addPhoneNumberField = new JTextField();
        addProgramField = new JTextField();
        addCoursesField = new JTextField();

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
        addFormFieldsPanel.add(new JLabel("Courses (comma-separated):"));
        addFormFieldsPanel.add(addCoursesField);

        // Submit Button
        JButton addFormSubmitButton = new JButton("ADD");
        addFormSubmitButton.addActionListener(e -> addTeacher());

        // Assemble Form Panel
        JPanel addFormTopPanel = new JPanel(new BorderLayout());
        addFormTopPanel.add(new JLabel("Add Teacher"), BorderLayout.WEST);
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
        JPanel updateFormFieldsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        updateNameField = new JTextField();
        updatePersonalNumberField = new JTextField();
        updateEmailField = new JTextField();
        updatePhoneNumberField = new JTextField();
        updateProgramField = new JTextField();
        updateCoursesField = new JTextField();

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
        updateFormFieldsPanel.add(new JLabel("Courses (comma-separated):"));
        updateFormFieldsPanel.add(updateCoursesField);

        // Submit Button
        JButton updateFormSubmitButton = new JButton("UPDATE");
        updateFormSubmitButton.addActionListener(e -> updateTeacher());

        // Assemble Form Panel
        JPanel updateFormTopPanel = new JPanel(new BorderLayout());
        updateFormTopPanel.add(new JLabel("Update Teacher"), BorderLayout.WEST);
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
        if (selectedTeacher == null) {
            JOptionPane.showMessageDialog(this, "Please select a teacher to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        if (updateFormVisible) {
            cardLayout.show(formContainer, "EMPTY");
            updateFormVisible = false;
        } else {
            // Populate the update form fields with selected teacher data
            updateNameField.setText(selectedTeacher.getName());
            updatePersonalNumberField.setText(selectedTeacher.getPersonalNumber());
            updateEmailField.setText(selectedTeacher.getEmail());
            updatePhoneNumberField.setText(selectedTeacher.getPhoneNumber());
            updateProgramField.setText(selectedTeacher.getProgram());
            updateCoursesField.setText(String.join(", ", selectedTeacher.getCourses()));

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

    private void addTeacher() {
        // Collect data from form fields
        String name = addNameField.getText().trim();
        String personalNumber = addPersonalNumberField.getText().trim();
        String email = addEmailField.getText().trim();
        String phoneNumber = addPhoneNumberField.getText().trim();
        String program = addProgramField.getText().trim();
        String coursesText = addCoursesField.getText().trim();

        // Simple validation (optional)
        if (name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || program.isEmpty() || coursesText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Split courses
        List<String> courses = Arrays.asList(coursesText.split("\\s*,\\s*"));

        try {
            // Pass data to the controller
            controller.addTeacher(name, personalNumber, email, phoneNumber, program, courses);
            // Clear text fields
            addNameField.setText("");
            addPersonalNumberField.setText("");
            addEmailField.setText("");
            addPhoneNumberField.setText("");
            addProgramField.setText("");
            addCoursesField.setText("");

            JOptionPane.showMessageDialog(this, "Teacher added successfully!");
            hideForms(); // Hide the form
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeacher() {
        // Collect data from form fields
        String name = updateNameField.getText().trim();
        String personalNumber = updatePersonalNumberField.getText().trim();
        String email = updateEmailField.getText().trim();
        String phoneNumber = updatePhoneNumberField.getText().trim();
        String program = updateProgramField.getText().trim();
        String coursesText = updateCoursesField.getText().trim();

        // Simple validation (optional)
        if (name.isEmpty() || personalNumber.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || program.isEmpty() || coursesText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Split courses
        List<String> courses = Arrays.asList(coursesText.split("\\s*,\\s*"));

        try {
            // Pass data and selectedTeacher to the controller
            controller.updateTeacher(selectedTeacher, name, personalNumber, email, phoneNumber, program, courses);

            // Clear the form fields
            updateNameField.setText("");
            updatePersonalNumberField.setText("");
            updateEmailField.setText("");
            updatePhoneNumberField.setText("");
            updateProgramField.setText("");
            updateCoursesField.setText("");

            JOptionPane.showMessageDialog(this, "Teacher updated successfully!");
            hideForms(); // Hide the form
            selectedTeacher = null;
            updateButton.setEnabled(false);
            teacherTable.clearSelection();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = teacherTable.convertRowIndexToModel(selectedRow);
            String teacherId = (String) teacherTableModel.getValueAt(modelRow, 0);
            try {
                Teacher teacher = controller.getTeacherById(teacherId);
                controller.deleteTeacher(teacher);
                JOptionPane.showMessageDialog(this, "Teacher deleted successfully!");
                selectedTeacher = null;
                updateButton.setEnabled(false);
                loadTeacherData(); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting teacher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a teacher to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadTeacherData() {
        teacherTableModel.setRowCount(0);
        List<Teacher> teachers = controller.getAllTeachers();
        for (Teacher teacher : teachers) {
            teacherTableModel.addRow(new Object[]{
                    teacher.getPersonID(),
                    teacher.getName(),
                    teacher.getPersonalNumber(),
                    teacher.getEmail(),
                    teacher.getPhoneNumber(),
                    teacher.getProgram(),
                    String.join(", ", teacher.getCourses())
            });
        }
    }

    private void updateUndoRedoButtons() {
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
    }

    @Override
    public void update(String message) {
        loadTeacherData();
        updateUndoRedoButtons();
    }
}
