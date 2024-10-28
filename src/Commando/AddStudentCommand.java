package Commando;

import Model.Dao.StudentDao;
import Model.Person.Student;

public class AddStudentCommand implements Command {
    private final StudentDao studentDao;
    private final Student student;

    public AddStudentCommand(StudentDao studentDao, Student student) {
        this.studentDao = studentDao;
        this.student = student;
    }

    @Override
    public void execute() {
        studentDao.save(student);
    }
}
