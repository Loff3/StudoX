package View;

import Controller.ControllerInterface;
import Model.Curriculum.Course;
import Model.Curriculum.Program;
import Model.Person.Student;
import Model.Person.Teacher;
import ObserverPattern.Observer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class ProgramPanel extends JPanel implements Observer {

    private ControllerInterface controller;

    // Components
    private JTable programTable;
    private DefaultTableModel programTableModel;
    private TableRowSorter<DefaultTableModel> tableRowSorter;

    // Form components
    // For Add Program Form
    private JPanel addFormPanel;
    private JTextField addProgramIDField;
    private JTextField addProgramNameField;
    private JComboBox<Teacher> addHeadTeacherComboBox;

    // For Update Program Form
    private JPanel updateFormPanel;
    private JTextField updateProgramIDField;
    private JTextField updateProgramNameField;
    private JComboBox<Teacher> updateHeadTeacherComboBox;

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

    // Selected program for update
    private Program selectedProgram;

    // Panel to hold forms
    private JPanel formContainer;

    public ProgramPanel(ControllerInterface controller) {
        this.controller = controller;
        controller.addObserver(this);

        initComponents();
        loadProgramData();
        updateUndoRedoButtons();
        addInitialPrograms();
    }

    private void addInitialPrograms() {
        try {
            // Add initial programs within try-catch to handle exceptions
            controller.addProgram("CS-BS", "Bachelor of Science in Computer Science", null);
            controller.addProgram("MATH-BS", "Bachelor of Science in Mathematics", null);
            controller.addProgram("PHYS-BS", "Bachelor of Science in Physics", null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding initial programs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
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

        // Program Table
        String[] programColumns = {"Program ID", "Program Name", "Head Teacher"};
        programTableModel = new DefaultTableModel(programColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        programTable = new JTable(programTableModel);
        programTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        programTable.setFillsViewportHeight(true);

        // Initialize TableRowSorter
        tableRowSorter = new TableRowSorter<>(programTableModel);
        programTable.setRowSorter(tableRowSorter);

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(programTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addButton = new JButton("Add Program");
        updateButton = new JButton("Update Program");
        deleteButton = new JButton("Delete Program");
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
        deleteButton.addActionListener(e -> deleteProgram());
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

        // Add selection listener to programTable
        programTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = programTable.getSelectedRow();
                if (selectedRow >= 0) {
                    updateButton.setEnabled(true);
                    int modelRow = programTable.convertRowIndexToModel(selectedRow);
                    String programID = (String) programTableModel.getValueAt(modelRow, 0);
                    try {
                        selectedProgram = controller.getProgramById(programID);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error retrieving program: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        selectedProgram = null;
                        updateButton.setEnabled(false);
                    }
                } else {
                    updateButton.setEnabled(false);
                    selectedProgram = null;
                }
            }
        });

        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(lowerPanel, BorderLayout.SOUTH);
    }

    private void initAddFormPanel() {
        addFormPanel = new JPanel(new BorderLayout(10, 10));
        addFormPanel.setBorder(BorderFactory.createTitledBorder("Add Program"));

        // Form Fields
        JPanel formFieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        addProgramIDField = new JTextField();
        addProgramNameField = new JTextField();
        addHeadTeacherComboBox = new JComboBox<>();
        loadTeachersIntoComboBox(addHeadTeacherComboBox);

        formFieldsPanel.add(new JLabel("Program ID:"));
        formFieldsPanel.add(addProgramIDField);
        formFieldsPanel.add(new JLabel("Program Name:"));
        formFieldsPanel.add(addProgramNameField);
        formFieldsPanel.add(new JLabel("Head Teacher:"));
        formFieldsPanel.add(addHeadTeacherComboBox);

        // Submit Button
        JButton submitButton = new JButton("Add");
        submitButton.addActionListener(e -> addProgram());

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            hideForms();
            addFormVisible = false;
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton);

        // Assemble Add Form Panel
        addFormPanel.add(formFieldsPanel, BorderLayout.CENTER);
        addFormPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initUpdateFormPanel() {
        updateFormPanel = new JPanel(new BorderLayout(10, 10));
        updateFormPanel.setBorder(BorderFactory.createTitledBorder("Update Program"));

        // Form Fields
        JPanel formFieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        updateProgramIDField = new JTextField();
        updateProgramIDField.setEditable(false); // Program ID is not editable
        updateProgramNameField = new JTextField();
        updateHeadTeacherComboBox = new JComboBox<>();
        loadTeachersIntoComboBox(updateHeadTeacherComboBox);

        formFieldsPanel.add(new JLabel("Program ID:"));
        formFieldsPanel.add(updateProgramIDField);
        formFieldsPanel.add(new JLabel("Program Name:"));
        formFieldsPanel.add(updateProgramNameField);
        formFieldsPanel.add(new JLabel("Head Teacher:"));
        formFieldsPanel.add(updateHeadTeacherComboBox);

        // Submit Button
        JButton submitButton = new JButton("Update");
        submitButton.addActionListener(e -> updateProgram());

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            hideForms();
            updateFormVisible = false;
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton);

        // Assemble Update Form Panel
        updateFormPanel.add(formFieldsPanel, BorderLayout.CENTER);
        updateFormPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadTeachersIntoComboBox(JComboBox<Teacher> comboBox) {
        List<Teacher> teachers = controller.getAllTeachers();
        DefaultComboBoxModel<Teacher> model = new DefaultComboBoxModel<>();
        model.addElement(null); // Option for no head teacher
        for (Teacher teacher : teachers) {
            model.addElement(teacher);
        }
        comboBox.setModel(model);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Teacher teacher) {
                    value = teacher.getName();
                } else if (value == null) {
                    value = "None";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
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
        if (selectedProgram == null) {
            JOptionPane.showMessageDialog(this, "Please select a program to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        if (updateFormVisible) {
            cardLayout.show(formContainer, "EMPTY");
            updateFormVisible = false;
        } else {
            // Populate the update form fields with selected program data
            updateProgramIDField.setText(selectedProgram.getProgramID());
            updateProgramNameField.setText(selectedProgram.getProgramName());
            updateHeadTeacherComboBox.setSelectedItem(selectedProgram.getHeadTeacher());

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
            // Filter based on Program ID and Program Name
            tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1));
        }
    }

    private void addProgram() {
        String programID = addProgramIDField.getText().trim();
        String programName = addProgramNameField.getText().trim();
        Teacher headTeacher = (Teacher) addHeadTeacherComboBox.getSelectedItem();

        // Simple validation
        if (programID.isEmpty() || programName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.addProgram(programID, programName, headTeacher);
            // Clear form fields
            addProgramIDField.setText("");
            addProgramNameField.setText("");
            addHeadTeacherComboBox.setSelectedIndex(0); // Reset to "None"

            JOptionPane.showMessageDialog(this, "Program added successfully!");
            hideForms(); // Hide the form
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding program: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateProgram() {
        String programID = updateProgramIDField.getText().trim();
        String programName = updateProgramNameField.getText().trim();
        Teacher headTeacher = (Teacher) updateHeadTeacherComboBox.getSelectedItem();

        // Simple validation
        if (programName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Program name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Update the selected program's name
            selectedProgram.setProgramName(programName);

            // Create dummy variables for students and courses
            // If you want to preserve existing students and courses, pass them
            Set<Student> updatedStudents = new HashSet<>(selectedProgram.getStudents());
            Set<Course> updatedCourses = new HashSet<>(selectedProgram.getCourses());

            // If you want to pass empty sets (not recommended as it would remove all students and courses)
            // Set<Student> updatedStudents = new HashSet<>();
            // Set<Course> updatedCourses = new HashSet<>();

            // Call the controller's updateProgram with the required parameters
            controller.updateProgram(selectedProgram, headTeacher, updatedStudents, updatedCourses);

            JOptionPane.showMessageDialog(this, "Program updated successfully!");
            hideForms(); // Hide the form
            selectedProgram = null;
            updateButton.setEnabled(false);
            programTable.clearSelection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating program: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProgram() {
        int selectedRow = programTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = programTable.convertRowIndexToModel(selectedRow);
            String programID = (String) programTableModel.getValueAt(modelRow, 0);
            try {
                Program program = controller.getProgramById(programID);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected program?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                controller.deleteProgram(program);
                JOptionPane.showMessageDialog(this, "Program deleted successfully!");
                selectedProgram = null;
                updateButton.setEnabled(false);
                loadProgramData(); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting program: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a program to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void loadProgramData() {
        programTableModel.setRowCount(0);
        List<Program> programs = controller.getAllPrograms();
        for (Program program : programs) {
            String headTeacherName = program.getHeadTeacher() != null ? program.getHeadTeacher().getName() : "None";
            programTableModel.addRow(new Object[]{
                    program.getProgramID(),    // Program ID
                    program.getProgramName(),  // Program Name
                    headTeacherName           // Head Teacher
            });
        }
    }


    private void updateUndoRedoButtons() {
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
    }

    @Override
    public void update(String message) {
        loadProgramData();
        updateUndoRedoButtons();
    }
}
