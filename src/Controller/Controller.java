package Controller;

import Command.*;
import Model.Dao.HistoryDao;
import Model.Dao.StudentDao;
import Model.Person.Student;
import Model.Service.StudentService;
import ObserverPattern.Observer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller implements ControllerInterface {
    private final CommandInvoker commandInvoker;
    private final StudentDao studentDao;
    private final HistoryDao historyDao;
    private final StudentService studentService;

    public Controller(CommandInvoker commandInvoker, StudentService studentService) {
        this.commandInvoker = commandInvoker;
        this.studentDao = commandInvoker.getStudentDao();
        this.historyDao = commandInvoker.getHistoryDao();
        this.studentService = studentService;
    }

    @Override
    public void revertToVersion(int historyIndex) throws Exception {
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
    }

    @Override
    public void addStudent(String name, String personalNumber, String email, String phoneNumber, String program) throws Exception {
        Command addCommand = new AddStudentCommand(studentDao, studentService, name, personalNumber, email, phoneNumber, program);
        commandInvoker.executeCommand(addCommand);
    }

    @Override
    public void updateStudent(Student oldStudent, String name, String personalNumber, String email, String phoneNumber, String program) throws Exception {
        Command updateCommand = new UpdateStudentCommand(studentDao, studentService, oldStudent, name, personalNumber, email, phoneNumber, program);
        commandInvoker.executeCommand(updateCommand);
    }

    @Override
    public void deleteStudent(Student student) throws Exception {
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
    public void undo() throws Exception {
        commandInvoker.undo();
    }

    @Override
    public void redo() throws Exception {
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
}
