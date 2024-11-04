package Command;

import Model.Dao.TeacherDao;
import Model.Person.Teacher;
import Model.Service.TeacherService;

import java.util.List;

public class AddTeacherCommand implements Command {
    private final TeacherDao teacherDao;
    private final TeacherService teacherService;
    private final String name;
    private final String personalNumber;
    private final String email;
    private final String phoneNumber;
    private final String program;
    private final List<String> courses;
    private final String teacherId; // Optional: can be null
    private Teacher teacher;

    // Constructor without teacherId
    public AddTeacherCommand(TeacherDao teacherDao, TeacherService teacherService, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) {
        this(teacherDao, teacherService, name, personalNumber, email, phoneNumber, program, courses, null);
    }

    // Constructor with teacherId
    public AddTeacherCommand(TeacherDao teacherDao, TeacherService teacherService, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses, String teacherId) {
        this.teacherDao = teacherDao;
        this.teacherService = teacherService;
        this.name = name;
        this.personalNumber = personalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.courses = courses;
        this.teacherId = teacherId;
    }

    @Override
    public void execute() throws Exception {
        // Use TeacherService to create and validate the teacher
        teacher = teacherService.createTeacher(name, personalNumber, email, phoneNumber, program, courses, teacherId);
        if (teacher == null) {
            throw new Exception("Failed to create teacher.");
        }
        // Save the teacher in DAO
        teacherDao.save(teacher);
    }

    @Override
    public void undo() throws Exception {
        if (teacher != null) {
            teacherDao.delete(teacher);
        } else {
            throw new Exception("No teacher to undo.");
        }
    }

    @Override
    public String getDescription() {
        return "Add Teacher: " + (teacher != null ? teacher.getPersonID() : "N/A");
    }
}
