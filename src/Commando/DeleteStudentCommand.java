package Commando;

import Model.Dao.StudentDao;
import Model.Person.Student;

public class DeleteStudentCommand implements Command {
    private final StudentDao studentDao;
    private final Student student;

    public DeleteStudentCommand(StudentDao studentDao, Student student) {
        this.studentDao = studentDao;
        this.student = student;
    }

    @Override
    public void execute() {
        studentDao.delete(student);
    }

    @Override
    public void undo() {
        studentDao.save(student);
    }

    @Override
    public String getDescription() {
        return "Delete Student: " + student.getPersonID();
    }
}
