package Command;

import Model.Curriculum.Course;
import Model.Dao.CourseDao;
import Model.Service.CourseService;

public class AddCourseCommand implements Command {
    private final CourseDao courseDao;
    private final CourseService courseService;
    private final String courseCode;
    private final String courseName;
    private Course course;

    public AddCourseCommand(CourseDao courseDao, CourseService courseService, String courseCode, String courseName) {
        this.courseDao = courseDao;
        this.courseService = courseService;
        this.courseCode = courseCode;
        this.courseName = courseName;
    }

    @Override
    public void execute() throws Exception {
        course = courseService.createCourse(courseCode, courseName);
        if (course == null) {
            throw new Exception("Failed to create course.");
        }
        courseDao.save(course);
    }

    @Override
    public void undo() throws Exception {
        if (course != null) {
            courseDao.delete(course);
        } else {
            throw new Exception("No course to undo.");
        }
    }

    @Override
    public String getDescription() {
        return "Add Course: " + (course != null ? course.getCourseCode() : "N/A");
    }
}
