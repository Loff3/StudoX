package View;

import Controller.ControllerInterface;
import Model.Service.StudentService;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    private ControllerInterface controller;

    // Components
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
    private TeacherPanel teacherPanel;
    private ProgramPanel programPanel;
    private CoursePanel coursePanel;
    private HistoryPanel historyPanel;

    public View(ControllerInterface controller) {
        super("StudioX");
        this.controller = controller;

        // Initialize components
        initComponents();

        // Set up the frame
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        // Set layout JFrame
        setLayout(new BorderLayout());

        // Main Panel
        tabbedPane = new JTabbedPane();

        // Create panels
        studentPanel = new StudentPanel(controller);
        teacherPanel = new TeacherPanel(controller);
        programPanel = new ProgramPanel(controller);
        coursePanel = new CoursePanel(controller);
        historyPanel = new HistoryPanel(controller);


        // Add tabs
        tabbedPane.addTab("Student Database", studentPanel);
        tabbedPane.add("Teacher Database", teacherPanel);
        tabbedPane.add("Program Panel", programPanel);
        tabbedPane.add("Course Database", coursePanel);
        tabbedPane.addTab("Version History", historyPanel);

        // Add components to frame
        add(tabbedPane, BorderLayout.CENTER);
    }
}
