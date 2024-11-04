package Command;

import Model.Dao.ProgramDao;
import Model.Curriculum.Program;
import Model.Service.ProgramService;
import Model.Person.Student;
import Model.Person.Teacher;
import Model.Curriculum.Course;

import java.util.HashSet;
import java.util.Set;

public class UpdateProgramCommand implements Command {
    private final ProgramDao programDao;
    private final ProgramService programService;
    private final String programName;
    private Program oldProgram;
    private Program newProgram;
    private Program backupOldProgram;
    private String programId;

    private final Teacher headTeacher;
    private final Set<Student> students;
    private final Set<Course> courses;

    public UpdateProgramCommand(ProgramDao programDao, ProgramService programService, Program oldProgram,
                                Teacher headTeacher, Set<Student> students, Set<Course> courses) {
        this.programDao = programDao;
        this.programService = programService;
        this.programId = oldProgram.getProgramID();
        this.programName = oldProgram.getName();
        this.oldProgram = oldProgram;
        this.headTeacher = headTeacher;
        this.students = students != null ? new HashSet<>(students) : null;
        this.courses = courses != null ? new HashSet<>(courses) : null;
    }
    @Override
    public void execute() throws Exception {
        // Corrected: Retrieve using programID
        oldProgram = programDao.get(programId).orElse(null);
        if (oldProgram == null) {
            throw new Exception("Program not found during update.");
        }
        backupOldProgram = new Program(oldProgram); // Assuming a proper copy constructor exists

        newProgram = new Program(programId, programName); // Use programID instead of undefined 'id'

        // Set head teacher
        if (headTeacher != null) {
            newProgram.setHeadTeacher(headTeacher);
        }

        // Add students
        if (students != null) {
            for (Student student : students) {
                newProgram.addStudent(student);
            }
        }

        // Add courses
        if (courses != null) {
            for (Course course : courses) {
                newProgram.addCourse(course);
            }
        }

        programDao.update(oldProgram, newProgram);
    }


    @Override
    public void undo() throws Exception {
        if (backupOldProgram != null && newProgram != null) {
            programDao.update(newProgram, backupOldProgram);
        } else {
            throw new Exception("No backup available to undo update.");
        }
    }

    @Override
    public String getDescription() {
        return "Update Program: " + programName;
    }
}
