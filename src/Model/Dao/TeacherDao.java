package Model.Dao;

import Model.Person.Teacher;
import ObserverPattern.Observable;
import ObserverPattern.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDao implements Dao<Teacher>, Observable {
    private List<Teacher> teachers = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    // Singleton pattern
    private static volatile TeacherDao instance;

    private TeacherDao() { }

    public static TeacherDao getInstance() {
        if (instance == null) {
            synchronized (TeacherDao.class) {
                if (instance == null) {
                    instance = new TeacherDao();
                }
            }
        }
        return instance;
    }

    public boolean teacherIdExists(String teacherId) {
        return teachers.stream().anyMatch(t -> t.getPersonID().equals(teacherId));
    }

    @Override
    public Optional<Teacher> get(String personID) {
        return teachers.stream()
                .filter(teacher -> teacher.getPersonID().equals(personID))
                .findFirst();
    }

    @Override
    public List<Teacher> getAll() {
        return new ArrayList<>(teachers);
    }

    @Override
    public void save(Teacher teacher) {
        teachers.add(teacher);
        notifyObservers("Teacher added: " + teacher.getPersonID());
    }

    @Override
    public void update(Teacher oldTeacher, Teacher newTeacher) {
        int index = teachers.indexOf(oldTeacher);
        if (index >= 0) {
            teachers.set(index, newTeacher);
            notifyObservers("Teacher updated: " + newTeacher.getPersonID());
        } else {
            System.out.println("Teacher not found.");
        }
    }

    @Override
    public void delete(Teacher teacher) {
        if (teachers.remove(teacher)) {
            notifyObservers("Teacher deleted: " + teacher.getPersonID());
        } else {
            System.out.println("Teacher not found.");
        }
    }

    // Methods to check for existence
    public boolean emailExists(String email) {
        return teachers.stream()
                .anyMatch(teacher -> teacher.getEmail().equalsIgnoreCase(email));
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return teachers.stream()
                .anyMatch(teacher -> teacher.getPhoneNumber().equals(phoneNumber));
    }

    public boolean personalNumberExists(String personalNumber) {
        return teachers.stream()
                .anyMatch(teacher -> teacher.getPersonalNumber().equals(personalNumber));
    }

    public void clearAll() {
        synchronized (this) {
            teachers.clear();
            notifyObservers("All teachers cleared.");
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
