package Controller;

import Model.Person.Student;

import java.util.List;

public interface ControllerInterface {
        void addStudent(Student student);
        void deleteStudent(String studentId);
        List<Student> getAllStudents();
        List<Student> searchStudents(String query);
}
