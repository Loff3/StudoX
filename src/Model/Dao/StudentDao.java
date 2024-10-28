package Model.Dao;


import Model.Person.Student;
import ObserverPattern.Observable;
import ObserverPattern.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class StudentDao implements Dao<Student>, Observable {
    private List<Student> students = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    // Trådsäker Singleton
    private static volatile StudentDao instance;

    private StudentDao() {
        // Initialisera med några exempelstudenter
        students.add(new Student("1", "John Doe", "1234567890", "john@example.com", "Program1"));
        students.add(new Student("2", "Jane Smith", "0987654321", "jane@example.com", "Program2"));
    }

    // Trådsäker Singleton
    public static StudentDao getInstance() {
        if (instance == null) {
            synchronized (StudentDao.class) {
                if (instance == null) {
                    instance = new StudentDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Student> get(String personID) {
        return students.stream()
                .filter(student -> student.getPersonID().equals(personID))
                .findFirst();
    }


    @Override
    public List<Student> getAll() {
        return students;
    }

    @Override
    public void save(Student student) {
        students.add(student);

        notifyObservers("Student added: " + student.getName());
    }

    @Override
    public void update(Student student, Student updatedStudent) {
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setPersonalNumber(updatedStudent.getPersonalNumber());
        student.setPersonID(updatedStudent.getPersonID());
        student.setProgram(updatedStudent.getProgram());

        notifyObservers("Student updated: " + student.getName());
    }

    public List<Student> search(String query) {
        String lowerCaseQuery = query.toLowerCase();
        return students.stream()
                .filter(student ->
                        student.getPersonID().toLowerCase().contains(lowerCaseQuery) ||
                                student.getName().toLowerCase().contains(lowerCaseQuery) ||
                                student.getPersonalNumber().toLowerCase().contains(lowerCaseQuery) ||
                                student.getEmail().toLowerCase().contains(lowerCaseQuery) ||
                                student.getProgram().toLowerCase().contains(lowerCaseQuery)
                )
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Student student) {
        students.remove(student);

        notifyObservers("Student deleted: " + student.getName());
    }
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
