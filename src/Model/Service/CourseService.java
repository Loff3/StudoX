package Model.Service;

import Model.Curriculum.Course;
import Model.Dao.CourseDao;
import Model.Person.Student;
import Model.Person.Teacher;
import Model.Curriculum.Program;

public class CourseService {

    private final CourseDao courseDao;

    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public Course createCourse(String courseCode, String courseName) throws Exception {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new Exception("Course code cannot be empty.");
        }
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new Exception("Course name cannot be empty.");
        }
        if (courseDao.get(courseCode).isPresent()) {
            throw new Exception("Course already exists.");
        }
        return new Course(courseCode, courseName);
    }

    public void setTeacher(Course course, Teacher teacher) throws Exception {
        course.setTeacher(teacher);
    }

    public void addStudentToCourse(Course course, Student student) {
        course.addStudent(student);
    }

    public void addProgramToCourse(Course course, Program program) {
        course.addProgram(program);
    }

    // Additional validation methods can be added here
}
