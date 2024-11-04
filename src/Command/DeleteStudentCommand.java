package Command;

import Model.Dao.StudentDao;
import Model.Person.Student;

public class DeleteStudentCommand implements Command {
    private final StudentDao studentDao;
    private final String studentId;
    private Student student;

    public DeleteStudentCommand(StudentDao studentDao, Student student) {
        this.studentDao = studentDao;
        this.studentId = student.getPersonID();
    }

    @Override
    public void execute() throws Exception{
        // Retrieve the student from the DAO
        student = studentDao.get(studentId).orElse(null);
        if (student == null) {
            throw new Exception("Student not found during delete.");
        }
        studentDao.delete(student);
    }

    @Override
    public void undo() throws Exception{

        if (student != null) {
            studentDao.save(student);
        } else {
            throw new Exception("No student to undo.");
        }

    }

    @Override
    public String getDescription() {
        return "Delete Student: " + studentId;
    }
}
