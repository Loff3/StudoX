package Command;

import Model.Dao.TeacherDao;
import Model.Person.Teacher;
import Model.Service.TeacherService;

import java.util.List;

public class UpdateTeacherCommand implements Command {
    private final TeacherDao teacherDao;
    private final TeacherService teacherService;
    private final String oldTeacherId;
    private Teacher oldTeacher;
    private Teacher newTeacher;
    private Teacher backupOldTeacher;

    private final String name;
    private final String personalNumber;
    private final String email;
    private final String phoneNumber;
    private final String program;
    private final List<String> courses;
    private String newPersonID; // Store newPersonID

    public UpdateTeacherCommand(TeacherDao teacherDao, TeacherService teacherService, Teacher oldTeacher, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) {
        this.teacherDao = teacherDao;
        this.teacherService = teacherService;
        this.oldTeacherId = oldTeacher.getPersonID();
        this.oldTeacher = oldTeacher;
        this.name = name;
        this.personalNumber = personalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.courses = courses;
    }

    @Override
    public void execute() throws Exception {
        // Retrieve the oldTeacher from the DAO
        oldTeacher = teacherDao.get(oldTeacherId).orElse(null);
        if (oldTeacher == null) {
            throw new Exception("Teacher not found during update.");
        }
        // Create a backup of oldTeacher
        backupOldTeacher = new Teacher(oldTeacher);

        // Use TeacherService to validate and create newTeacher
        newTeacher = teacherService.updateTeacher(oldTeacher, name, personalNumber, email, phoneNumber, program, courses, newPersonID);

        if (newTeacher == null) {
            throw new Exception("Failed to update teacher.");
        }

        // Update in DAO
        teacherDao.update(oldTeacher, newTeacher);
    }

    @Override
    public void undo() throws Exception {
        if (backupOldTeacher != null && newTeacher != null) {
            teacherDao.update(newTeacher, backupOldTeacher);
        } else {
            throw new Exception("No backup available to undo update.");
        }
    }

    @Override
    public String getDescription() {
        return "Update Teacher: " + oldTeacherId + " to " + (newTeacher != null ? newTeacher.getPersonID() : "N/A");
    }
}
