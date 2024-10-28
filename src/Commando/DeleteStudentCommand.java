package Commando;

import Model.Dao.StudentDao;
import Model.Person.Student;

import java.util.Optional;

public class DeleteStudentCommand implements Command {
    private final StudentDao studentDao;
    private final String studentId;
    private Student removedStudent;

    public DeleteStudentCommand(StudentDao studentDao, String studentId) {
        this.studentDao = studentDao;
        this.studentId = studentId;
    }

    @Override
    public void execute() {
        Optional<Student> studentOpt = studentDao.get(studentId);
        if (studentOpt.isPresent()) {
            removedStudent = studentOpt.get();
            studentDao.delete(removedStudent);
        } else {
            throw new IllegalArgumentException("Student not found.");
        }
    }
}
