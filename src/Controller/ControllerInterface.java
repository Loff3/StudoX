package Controller;

import Model.Person.Student;
import java.util.List;

public interface ControllerInterface {
        void addStudent(Student student);
        void deleteStudent(String studentId);
        List<Student> getAllStudents();

        void undo();
        void redo();
        boolean canUndo();
        boolean canRedo();
}
