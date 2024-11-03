package Command;

import Model.Dao.StudentDao;
import Model.Person.Student;

public class UpdateStudentCommand implements Command {
    private final StudentDao studentDao;
    private final Student oldStudent;
    private final Student newStudent;

    public UpdateStudentCommand(StudentDao studentDao, Student oldStudent, Student newStudent) {
        this.studentDao = studentDao;
        this.oldStudent = oldStudent;
        this.newStudent = newStudent;
    }

    @Override
    public void execute() {
        // Perform the update in the DAO
        studentDao.update(oldStudent, newStudent);
    }

    @Override
    public void undo()  {
        // Revert the update by restoring the old student data
        studentDao.update(newStudent, oldStudent);
    }

    @Override
    public String getDescription() {
        return "Update Student: " + oldStudent.getPersonID();
    }
}
