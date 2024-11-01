package View;

import Controller.ControllerInterface;
import Model.Person.Student;
import Model.Service.StudentService;
import ObserverPattern.Observer;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    private ControllerInterface controller;
    private StudentService studentService;

    // Components
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
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
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Set layout JFrame
        setLayout(new BorderLayout());

        // Main Panel
        tabbedPane = new JTabbedPane();

        // Create panels
        studentPanel = new StudentPanel(controller);
        historyPanel = new HistoryPanel(controller);

        // Add tabs
        tabbedPane.addTab("Student Database", studentPanel);
        tabbedPane.addTab("Version History", historyPanel);

        // Add components to frame
        add(tabbedPane, BorderLayout.CENTER);
    }
}
