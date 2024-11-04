package Model.Service;

import Model.Dao.ProgramDao;
import Model.Curriculum.Program;
import Model.Person.Student;
import Model.Person.Teacher;
import Model.Curriculum.Course;

public class ProgramService {

    private final ProgramDao programDao;

    public ProgramService(ProgramDao programDao) {
        this.programDao = programDao;
    }

    public Program createProgram(String programID, String programName) throws Exception {
        // Validate programID
        if (programID == null || programID.trim().isEmpty()) {
            throw new Exception("Program ID cannot be empty.");
        }

        // Validate programName
        if (programName == null || programName.trim().isEmpty()) {
            throw new Exception("Program name cannot be empty.");
        }

        // Check if the program with the given ID already exists
        if (programDao.get(programID).isPresent()) {
            throw new Exception("Program with ID " + programID + " already exists.");
        }

        // Create and return the new Program
        return new Program(programID, programName);
    }

    public void setHeadTeacher(Program program, Teacher teacher) throws Exception {
        program.setHeadTeacher(teacher);
    }

    public void addStudentToProgram(Program program, Student student) {
        program.addStudent(student);
    }

    public void addCourseToProgram(Program program, Course course) throws Exception {
        program.addCourse(course);
    }

    // Additional validation methods can be added here
}
