package Controller;

import Model.Person.Student;
import ObserverPattern.Observer;
import Model.Dao.HistoryDao.CommandRecord;
import java.util.List;

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
}
