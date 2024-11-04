package Command;

import Model.Curriculum.Course;
import Model.Dao.CourseDao;

public class DeleteCourseCommand implements Command {
    private final CourseDao courseDao;
    private final String courseCode;
    private Course course;

    public DeleteCourseCommand(CourseDao courseDao, Course course) {
        this.courseDao = courseDao;
        this.courseCode = course.getCourseCode();
    }

    @Override
    public void execute() throws Exception {
        course = courseDao.get(courseCode).orElse(null);
        if (course == null) {
            throw new Exception("Course not found during delete.");
        }
        courseDao.delete(course);
    }

    @Override
    public void undo() throws Exception {
        if (course != null) {
            courseDao.save(course);
        } else {
            throw new Exception("No course to undo delete.");
        }
    }

    @Override
    public String getDescription() {
        return "Delete Course: " + courseCode;
    }
}
