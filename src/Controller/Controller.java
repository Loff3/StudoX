package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import Commando.AddStudentCommand;
import Commando.Command;
import Commando.CommandInvoker;
import Commando.DeleteStudentCommand;
import Commando.EditStudentCommand;
import Commando.VersionControlCommand;
import Model.Dao.HistoryDao;
import Model.Dao.StudentDao;
import Model.Person.Student;
import ObserverPattern.Observer;

public class Controller implements ControllerInterface {
    private final CommandInvoker commandInvoker;
    private final StudentDao studentDao;
    private final HistoryDao historyDao;
    private final Set<String> validUsernames;

    public Controller(CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
        this.studentDao = commandInvoker.getStudentDao();
        this.historyDao = commandInvoker.getHistoryDao();
        this.validUsernames = loadValidUsernames("valid_usernames.txt"); // tar filen för med valid users
    }

    private Set<String> loadValidUsernames(String filename) {
        Set<String> usernames = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                usernames.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error loading usernames: " + e.getMessage());
        }
        return usernames;
    }

    @Override
    public boolean attemptLogin(String username) {
        if (validUsernames.contains(username)) {
            System.out.println("Login successful for username: " + username);
            return true;
        } else {
            System.out.println("Invalid username: " + username);
            return false;
        }
    }

    @Override
    public void revertToVersion(int historyIndex) {
        // Get the list of commands up to the selected point
        List<HistoryDao.CommandRecord> history = historyDao.getCommandHistory();

        if (historyIndex < 0 || historyIndex >= history.size()) {
            throw new IllegalArgumentException("Invalid history index.");
        }

        // Collect commands to execute
        List<Command> commandsToExecute = new ArrayList<>();
        for (int i = 0; i <= historyIndex; i++) {
            commandsToExecute.add(history.get(i).getCommand());
        }

        // Create and execute VersionControlCommand
        VersionControlCommand versionControlCommand = new VersionControlCommand(studentDao, commandsToExecute);
        commandInvoker.executeVersionControlCommand(versionControlCommand);
        
        studentDao.saveToFile("students.txt");
    }

    @Override
    public void addStudent(Student student) {
        Command addCommand = new AddStudentCommand(studentDao, student);
        commandInvoker.executeCommand(addCommand);
    }

    @Override
    public void deleteStudent(Student student) {
        Command deleteCommand = new DeleteStudentCommand(studentDao, student);
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public Student getStudentById(String studentId) {
        Optional<Student> studentOpt = studentDao.get(studentId);
        if (studentOpt.isPresent()) {
            return studentOpt.get();
        } else {
            throw new IllegalArgumentException("Student not found.");
        }
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.getAll();
    }

    @Override
    public void undo() {
        commandInvoker.undo();
    }

    @Override
    public void redo() {
        commandInvoker.redo();
    }

    @Override
    public boolean canUndo() {
        return commandInvoker.canUndo();
    }

    @Override
    public boolean canRedo() {
        return commandInvoker.canRedo();
    }

    @Override
    public void addObserver(Observer o) {
        studentDao.addObserver(o);
        historyDao.addObserver(o);
    }

    @Override
    public void removeObserver(Observer o) {
        studentDao.removeObserver(o);
        historyDao.removeObserver(o);
    }

    @Override
    public List<HistoryDao.CommandRecord> getCommandHistory() {
        return historyDao.getCommandHistory();
    }

    @Override
    public void editStudent(String personID, Student updatedStudent) {
        Command editCommand = new EditStudentCommand(studentDao, personID, updatedStudent);
        commandInvoker.executeCommand(editCommand);
    }

}
