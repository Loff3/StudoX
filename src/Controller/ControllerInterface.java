package Controller;

import java.util.List;

import Model.Dao.HistoryDao.CommandRecord;
import Model.Person.Student;
import ObserverPattern.Observer;

public interface ControllerInterface {
        void addStudent(Student student);
        void deleteStudent(Student student);

        Student getStudentById(String studentId);
        List<Student> getAllStudents();

        void revertToVersion(int historyIndex);

        void undo();
        void redo();
        boolean canUndo();
        boolean canRedo();

        void addObserver(Observer o);
        void removeObserver(Observer o);


        List<CommandRecord> getCommandHistory();
        
        boolean attemptLogin(String username);
        
        void editStudent(String personID, Student updatedStudent);
}
