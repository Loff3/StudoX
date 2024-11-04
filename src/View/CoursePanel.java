package View;

import Controller.ControllerInterface;
import Model.Curriculum.Program;
import Model.Person.Student;
import Model.Person.Teacher;
import Model.Curriculum.Course;
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

public class CoursePanel extends JPanel implements Observer {

    private ControllerInterface controller;

    // Components
    private JTable courseTable;
    private DefaultTableModel courseTableModel;
    private TableRowSorter<DefaultTableModel> tableRowSorter;

    // Form components
    // For Add Course Form
    private JPanel addFormPanel;
    private JTextField addCourseCodeField;
    private JTextField addCourseNameField;
    private JComboBox<Teacher> addTeacherComboBox;

    // For Update Course Form
    private JPanel updateFormPanel;
    private JTextField updateCourseCodeField;
    private JTextField updateCourseNameField;
    private JComboBox<Teacher> updateTeacherComboBox;

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

    // Selected course for update
    private Course selectedCourse;

    // Panel to hold forms
    private JPanel formContainer;

    public CoursePanel(ControllerInterface controller) {
        this.controller = controller;
        controller.addObserver(this);

        initComponents();
        loadCourseData();
        updateUndoRedoButtons();
        addInitialCourses();
    }

    private void addInitialCourses() {
        try {
            // Add initial courses within try-catch to handle exceptions
            controller.addCourse("CS101", "Introduction to Computer Science", null);
            controller.addCourse("MATH101", "Calculus I", null);
            controller.addCourse("PHYS101", "Physics I", null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding initial courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        // Course Table
        String[] courseColumns = {"Course Code", "Course Name", "Teacher"};
        courseTableModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setFillsViewportHeight(true);

        // Initialize TableRowSorter
        tableRowSorter = new TableRowSorter<>(courseTableModel);
        courseTable.setRowSorter(tableRowSorter);

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(courseTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addButton = new JButton("Add Course");
        updateButton = new JButton("Update Course");
        deleteButton = new JButton("Delete Course");
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
        deleteButton.addActionListener(e -> deleteCourse());
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

        // Add selection listener to courseTable
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow >= 0) {
                    updateButton.setEnabled(true);
                    int modelRow = courseTable.convertRowIndexToModel(selectedRow);
                    String courseCode = (String) courseTableModel.getValueAt(modelRow, 0);
                    try {
                        selectedCourse = controller.getCourseByCode(courseCode);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error retrieving course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        selectedCourse = null;
                        updateButton.setEnabled(false);
                    }
                } else {
                    updateButton.setEnabled(false);
                    selectedCourse = null;
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
        addFormPanel.setBorder(BorderFactory.createTitledBorder("Add Course"));

        // Form Fields
        JPanel formFieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        addCourseCodeField = new JTextField();
        addCourseNameField = new JTextField();
        addTeacherComboBox = new JComboBox<>();
        loadTeachersIntoComboBox(addTeacherComboBox);

        formFieldsPanel.add(new JLabel("Course Code:"));
        formFieldsPanel.add(addCourseCodeField);
        formFieldsPanel.add(new JLabel("Course Name:"));
        formFieldsPanel.add(addCourseNameField);
        formFieldsPanel.add(new JLabel("Teacher:"));
        formFieldsPanel.add(addTeacherComboBox);

        // Submit Button
        JButton submitButton = new JButton("Add");
        submitButton.addActionListener(e -> addCourse());

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
        updateFormPanel.setBorder(BorderFactory.createTitledBorder("Update Course"));

        // Form Fields
        JPanel formFieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        updateCourseCodeField = new JTextField();
        updateCourseCodeField.setEditable(false); // Course code is not editable
        updateCourseNameField = new JTextField();
        updateTeacherComboBox = new JComboBox<>();
        loadTeachersIntoComboBox(updateTeacherComboBox);

        formFieldsPanel.add(new JLabel("Course Code:"));
        formFieldsPanel.add(updateCourseCodeField);
        formFieldsPanel.add(new JLabel("Course Name:"));
        formFieldsPanel.add(updateCourseNameField);
        formFieldsPanel.add(new JLabel("Teacher:"));
        formFieldsPanel.add(updateTeacherComboBox);

        // Submit Button
        JButton submitButton = new JButton("Update");
        submitButton.addActionListener(e -> updateCourse());

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
        model.addElement(null); // Option for no teacher
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
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout cardLayout = (CardLayout) (formContainer.getLayout());
        if (updateFormVisible) {
            cardLayout.show(formContainer, "EMPTY");
            updateFormVisible = false;
        } else {
            // Populate the update form fields with selected course data
            updateCourseCodeField.setText(selectedCourse.getCourseCode());
            updateCourseNameField.setText(selectedCourse.getCourseName());
            updateTeacherComboBox.setSelectedItem(selectedCourse.getTeacher());

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
            // Filter based on Course Code, Course Name, or Teacher Name
            tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1, 2));
        }
    }

    private void addCourse() {
        String courseCode = addCourseCodeField.getText().trim();
        String courseName = addCourseNameField.getText().trim();
        Teacher teacher = (Teacher) addTeacherComboBox.getSelectedItem();

        if (courseCode.isEmpty() || courseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.addCourse(courseCode, courseName, teacher);
            addCourseCodeField.setText("");
            addCourseNameField.setText("");
            addTeacherComboBox.setSelectedIndex(0); // Reset to "None"
            JOptionPane.showMessageDialog(this, "Course added successfully!");
            hideForms();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourse() {
        String courseCode = updateCourseCodeField.getText().trim();
        String courseName = updateCourseNameField.getText().trim();
        Teacher teacher = (Teacher) updateTeacherComboBox.getSelectedItem();

        if (courseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Retrieve the Course object
            Course course = controller.getCourseByCode(courseCode);
            if (course == null) {
                throw new Exception("Course with code " + courseCode + " does not exist.");
            }

            // Since you don't want to manage students and programs, pass empty sets
            Set<Student> emptyStudents = new HashSet<>();
            Set<Program> emptyPrograms = new HashSet<>();

            controller.updateCourse(course, courseName, teacher, emptyStudents, emptyPrograms);
            JOptionPane.showMessageDialog(this, "Course updated successfully!");
            hideForms();
            selectedCourse = null;
            updateButton.setEnabled(false);
            courseTable.clearSelection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow >= 0) {
            // No need to retrieve courseCode again if selectedCourse is already set
            try {
                controller.deleteCourse(selectedCourse); // Pass the Course object instead of courseCode
                JOptionPane.showMessageDialog(this, "Course deleted successfully!");
                selectedCourse = null;
                updateButton.setEnabled(false);
                loadCourseData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void loadCourseData() {
        courseTableModel.setRowCount(0);
        List<Course> courses = controller.getAllCourses();
        for (Course course : courses) {
            String teacherName = course.getTeacher() != null ? course.getTeacher().getName() : "None";
            courseTableModel.addRow(new Object[]{
                    course.getCourseCode(),
                    course.getCourseName(),
                    teacherName
            });
        }
    }

    private void updateUndoRedoButtons() {
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
    }

    @Override
    public void update(String message) {
        loadCourseData();
        updateUndoRedoButtons();
    }
}
