package Command;

import Model.Dao.StudentDao;
import Model.Person.Student;
import Model.Service.StudentService;

public class UpdateStudentCommand implements Command {
    private final StudentDao studentDao;
    private final StudentService studentService;
    private final String oldStudentId;
    private Student oldStudent;
    private Student newStudent;
    private Student backupOldStudent;

    private final String name;
    private final String personalNumber;
    private final String email;
    private final String phoneNumber;
    private final String program;
    private String newPersonID; // Store newPersonID

    public UpdateStudentCommand(StudentDao studentDao, StudentService studentService, Student oldStudent, String name, String personalNumber, String email, String phoneNumber, String program) {
        this.studentDao = studentDao;
        this.studentService = studentService;
        this.oldStudentId = oldStudent.getPersonID();
        this.oldStudent = oldStudent;
        this.name = name;
        this.personalNumber = personalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
    }

    @Override
    public void execute() throws Exception{
        // Retrieve the oldStudent from the DAO
        oldStudent = studentDao.get(oldStudentId).orElse(null);
        if (oldStudent == null) {
            throw new Exception("Student not found during update.");
        }
        // Create a backup of oldStudent
        backupOldStudent = new Student(oldStudent);

        // Use StudentService to validate and create newStudent (might have a new personID)
        newStudent = studentService.updateStudent(oldStudent, name, personalNumber, email, phoneNumber, program, newPersonID);

        if (newStudent == null) {
            throw new Exception("Student not found during update.");

        }

        // Store newPersonID
        if (newPersonID == null) {
            newPersonID = newStudent.getPersonID();
        }

        // Update in DAO
        studentDao.update(oldStudent, newStudent);
    }

    @Override
    public void undo() {
        // Revert to backup
        studentDao.update(newStudent, backupOldStudent);

    }

    @Override
    public String getDescription() {
        return "Update Student: " + oldStudentId + " to " + newPersonID;
    }
}

