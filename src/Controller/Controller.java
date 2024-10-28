package Controller;

import Commando.*;
import Model.Dao.StudentDao;
import Model.Person.Student;

import java.util.List;

public class Controller implements ControllerInterface {
    private final CommandInvoker commandInvoker;
    private final StudentDao studentDao;

    public Controller(CommandInvoker commandInvoker, StudentDao studentDao) {
        this.commandInvoker = commandInvoker;
        this.studentDao = studentDao;
    }

    @Override
    public void addStudent(Student student) {
        Command addCommand = new AddStudentCommand(studentDao, student);
        commandInvoker.executeCommand(addCommand);
    }

    @Override
    public void deleteStudent(String studentId) {
        Command deleteCommand = new DeleteStudentCommand(studentDao, studentId);
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.getAll();
    }
    @Override
    public List<Student> searchStudents(String query) {
        SearchCommand searchCommand = new SearchCommand(studentDao, query);
        commandInvoker.executeCommand(searchCommand);
        return searchCommand.getSearchResults();
    }

}
