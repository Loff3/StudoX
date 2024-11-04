package Command;

import Model.Curriculum.Course;
import Model.Dao.CourseDao;
import Model.Service.CourseService;
import Model.Person.Student;
import Model.Person.Teacher;
import Model.Curriculum.Program;

import java.util.HashSet;
import java.util.Set;

public class UpdateCourseCommand implements Command {
    private final CourseDao courseDao;
    private final CourseService courseService;
    private final String courseCode;
    private Course oldCourse;
    private Course newCourse;
    private Course backupOldCourse;

    private final String courseName;
    private final Teacher teacher;
    private final Set<Student> students;
    private final Set<Program> programs;

    public UpdateCourseCommand(CourseDao courseDao, CourseService courseService, Course oldCourse,
                               String courseName, Teacher teacher, Set<Student> students, Set<Program> programs) {
        this.courseDao = courseDao;
        this.courseService = courseService;
        this.courseCode = oldCourse.getCourseCode();
        this.oldCourse = oldCourse;
        this.courseName = courseName;
        this.teacher = teacher;
        this.students = students != null ? new HashSet<>(students) : null;
        this.programs = programs != null ? new HashSet<>(programs) : null;
    }

    @Override
    public void execute() throws Exception {
        oldCourse = courseDao.get(courseCode).orElse(null);
        if (oldCourse == null) {
            throw new Exception("Course not found during update.");
        }
        backupOldCourse = new Course(oldCourse); // Deep copy

        newCourse = new Course(courseCode, courseName);

        // Set teacher
        if (teacher != null) {
            newCourse.setTeacher(teacher);
        }

        // Add students
        if (students != null) {
            for (Student student : students) {
                newCourse.addStudent(student);
            }
        }

        // Add programs
        if (programs != null) {
            for (Program program : programs) {
                newCourse.addProgram(program);
            }
        }

        courseDao.update(oldCourse, newCourse);
    }

    @Override
    public void undo() throws Exception {
        if (backupOldCourse != null && newCourse != null) {
            courseDao.update(newCourse, backupOldCourse);
        } else {
            throw new Exception("No backup available to undo update.");
        }
    }

    @Override
    public String getDescription() {
        return "Update Course: " + courseCode;
    }
}
