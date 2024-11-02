package Commando;

import Model.Dao.StudentDao;
import Model.Person.Student;

public class EditStudentCommand implements Command {
    private final StudentDao studentDao;
    private final String studentId;
    private final Student originalStudent;
    private final Student updatedStudent;

    public EditStudentCommand(StudentDao studentDao, String studentId, Student updatedStudent) {
        this.studentDao = studentDao;
        this.studentId = studentId;
        this.originalStudent = studentDao.get(studentId).orElse(null); // Load the original student
        this.updatedStudent = updatedStudent;
    }

    @Override
    public void execute() {
        if (originalStudent != null) {
            studentDao.editStudent(studentId, updatedStudent);
        } else {
            System.err.println("Error: Original student not found.");
        }
    }

    // Description for undo/redo history
    public String getDescription() {
        return (originalStudent != null) 
            ? "Edited Student: " + originalStudent.getName() + " to " + updatedStudent.getName()
            : "Error: Original student not found.";
    }

	@Override
	public void undo() {
		studentDao.delete(originalStudent);
		
	}
}
