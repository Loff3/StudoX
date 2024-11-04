package Controller;

import Model.Person.Student;
import ObserverPattern.Observer;
import Model.Dao.HistoryDao.CommandRecord;
import java.util.List;

public interface ControllerInterface {
        void addStudent(String name, String personalNumber, String email, String phoneNumber, String program) throws Exception;
        void updateStudent(Student oldStudent, String name, String personalNumber, String email, String phoneNumber, String program) throws Exception;
        void deleteStudent(Student student) throws Exception;
        Student getStudentById(String studentId);
        List<Student> getAllStudents();

        void revertToVersion(int historyIndex) throws Exception;

        void undo() throws  Exception;
        void redo() throws Exception;
        boolean canUndo();
        boolean canRedo();

        void addObserver(Observer o);
        void removeObserver(Observer o);


        List<CommandRecord> getCommandHistory();
}
