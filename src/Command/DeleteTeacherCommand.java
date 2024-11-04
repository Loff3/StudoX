package Command;

import Model.Dao.TeacherDao;
import Model.Person.Teacher;

public class DeleteTeacherCommand implements Command {
    private final TeacherDao teacherDao;
    private final String teacherId;
    private Teacher teacher;

    public DeleteTeacherCommand(TeacherDao teacherDao, Teacher teacher) {
        this.teacherDao = teacherDao;
        this.teacherId = teacher.getPersonID();
    }

    @Override
    public void execute() throws Exception {
        // Retrieve the teacher from the DAO to ensure it exists
        teacher = teacherDao.get(teacherId).orElse(null);
        if (teacher == null) {
            throw new Exception("Teacher not found during delete.");
        }
        // Perform deletion
        teacherDao.delete(teacher);
    }

    @Override
    public void undo() throws Exception {
        if (teacher != null) {
            // Restore the deleted teacher
            teacherDao.save(teacher);
        } else {
            throw new Exception("No teacher to undo delete.");
        }
    }

    @Override
    public String getDescription() {
        return "Delete Teacher: " + teacherId;
    }
}
