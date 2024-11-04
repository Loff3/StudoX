package Command;

import Model.Dao.StudentDao;
import Model.Person.Student;
import Model.Service.StudentService;

public class AddStudentCommand implements Command {
    private final StudentDao studentDao;
    private final StudentService studentService;
    private final String name;
    private final String personalNumber;
    private final String email;
    private final String phoneNumber;
    private final String program;
    private String studentId; // Store studentId
    private Student student;

    // Constructor without studentId
    public AddStudentCommand(StudentDao studentDao, StudentService studentService, String name, String personalNumber, String email, String phoneNumber, String program) {
        this(studentDao, studentService, name, personalNumber, email, phoneNumber, program, null);
    }

    // Constructor with studentId
    public AddStudentCommand(StudentDao studentDao, StudentService studentService, String name, String personalNumber, String email, String phoneNumber, String program, String studentId) {
        this.studentDao = studentDao;
        this.studentService = studentService;
        this.name = name;
        this.personalNumber = personalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.studentId = studentId;
    }

    @Override
    public void execute() throws Exception{
        // Use StudentService to create and validate the student
        student = studentService.createStudent(name, personalNumber, email, phoneNumber, program, studentId);
        if (student == null) {
            throw new Exception("Failed to create student.");
        }
        // Store the studentId for future use
        if (studentId == null) {
            studentId = student.getPersonID();
        }
        studentDao.save(student);
    }

    @Override
    public void undo() throws Exception{
        if (student != null) {
            studentDao.delete(student);
        } else {
            throw new Exception("No student to undo.");
        }
    }

    @Override
    public String getDescription() {
        return "Add Student: " + studentId;
    }
}
