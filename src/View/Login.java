package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Controller.ControllerInterface;

public class Login extends JFrame {
    private JTextField usernameField;
    private JButton loginButton;
    private JLabel errorLabel; 
    private ControllerInterface controller;

    public Login(ControllerInterface controller) {
        this.controller = controller;

        setTitle("Login");
        setSize(250, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(100, 25));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(20));

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            if (controller != null && controller.attemptLogin(usernameField.getText().trim())) {
                // Login successful, open the main view
                openView(controller);  // Open main view
                dispose();  // Close the login window
            } else {
                // Show error message
                showLoginError();
            }
        });

        // Error message
        errorLabel = new JLabel("");
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(errorLabel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void showLoginError() {
        errorLabel.setText("*Invalid username. Please try again.");
        errorLabel.setVisible(true);
    }

    public void clearErrorMessage() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
    // Method to open the main View after successful login
    public void openView(ControllerInterface controller) {
        // Instantiate the main View and set Controller
        View mainView = new View(controller);
        mainView.setVisible(true);
    }
}