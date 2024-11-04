package Controller;

import Model.Curriculum.Program;
import Model.Curriculum.Course;
import Model.Person.Student;
import Model.Person.Teacher;
import ObserverPattern.Observer;
import Model.Dao.HistoryDao.CommandRecord;
import java.util.List;
import java.util.Set;

public interface ControllerInterface {
        void addStudent(String name, String personalNumber, String email, String phoneNumber, String program) throws Exception;
        void updateStudent(Student oldStudent, String name, String personalNumber, String email, String phoneNumber, String program) throws Exception;
        void deleteStudent(Student student) throws Exception;
        Student getStudentById(String studentId);
        List<Student> getAllStudents();

        void addTeacher(String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) throws Exception;
        void updateTeacher(Teacher oldTeacher, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) throws Exception;
        void deleteTeacher(Teacher teacher) throws Exception;
        Teacher getTeacherById(String teacherId);
        List<Teacher> getAllTeachers();


        void updateProgram(Program oldProgram, Teacher headTeacher, Set<Student> students, Set<Course> courses) throws Exception;
        void deleteProgram(Program program) throws Exception;
        Program getProgramById(String name) throws Exception;
        List<Program> getAllPrograms();

        void addCourse(String courseCode, String courseName, Teacher teacher) throws Exception;
        void updateCourse(Course oldCourse, String courseName, Teacher teacher, Set<Student> students, Set<Program> programs) throws Exception;
        void deleteCourse(Course course) throws Exception;
        Course getCourseByCode(String courseCode);
        List<Course> getAllCourses();

        void revertToVersion(int historyIndex) throws Exception;

        void undo() throws  Exception;
        void redo() throws Exception;
        boolean canUndo();
        boolean canRedo();

        void addObserver(Observer o);
        void removeObserver(Observer o);


        List<CommandRecord> getCommandHistory();

        void addProgram(String s, String s1, Teacher teacher) throws Exception;

        boolean attemptLogin(String trim);
}
