package Model.Dao;

import Model.Person.Student;
import ObserverPattern.Observable;
import ObserverPattern.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDao implements Dao<Student>, Observable {
    private List<Student> students = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    // Singleton pattern
    private static volatile StudentDao instance;

    private StudentDao() {
    }

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

    public boolean studentIdExists(String studentId) {
        return students.stream().anyMatch(s -> s.getPersonID().equals(studentId));
    }


    @Override
    public Optional<Student> get(String personID) {
        return students.stream()
                .filter(student -> student.getPersonID().equals(personID))
                .findFirst();
    }

    @Override
    public List<Student> getAll() {
        return new ArrayList<>(students);
    }

    @Override
    public void save(Student student) {
        students.add(student);
        // Notify observers if necessary
    }

    @Override
    public void update(Student oldStudent, Student newStudent) {
        int index = students.indexOf(oldStudent);
        if (index >= 0) {
            students.set(index, newStudent);
            // Notify observers if necessary
        } else {
            System.out.println("Student not found.");
        }
    }

    @Override
    public void delete(Student student) {
        if (students.remove(student)) {
            // Notify observers if necessary
        } else {
            System.out.println("Student not found.");
        }
    }

    // Methods to check for existence
    public boolean emailExists(String email) {
        return students.stream()
                .anyMatch(student -> student.getEmail().equalsIgnoreCase(email));
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return students.stream()
                .anyMatch(student -> student.getPhoneNumber().equals(phoneNumber));
    }

    public boolean personalNumberExists(String personalNumber) {
        return students.stream()
                .anyMatch(student -> student.getPersonalNumber().equals(personalNumber));
    }

    public void clearAll() {
        synchronized (this) {
            students.clear();
            notifyObservers("All students cleared.");
        }
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
        for (Observer o : observers) {
            o.update(message);
        }
    }
}
