package Model.Dao;

import Model.Curriculum.Course;
import ObserverPattern.Observable;
import ObserverPattern.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDao implements Dao<Course>, Observable {
    private final List<Course> courses = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();

    // Singleton pattern
    private static volatile CourseDao instance;

    private CourseDao() { }

    public static CourseDao getInstance() {
        if (instance == null) {
            synchronized (CourseDao.class) {
                if (instance == null) {
                    instance = new CourseDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Course> get(String courseCode) {
        return courses.stream()
                .filter(course -> course.getCourseCode().equals(courseCode))
                .findFirst();
    }

    @Override
    public List<Course> getAll() {
        return new ArrayList<>(courses);
    }

    @Override
    public void save(Course course) {
        courses.add(course);
        notifyObservers("Course added: " + course.getCourseCode());
    }

    @Override
    public void update(Course oldCourse, Course newCourse) {
        int index = courses.indexOf(oldCourse);
        if (index >= 0) {
            courses.set(index, newCourse);
            notifyObservers("Course updated: " + newCourse.getCourseCode());
        } else {
            System.out.println("Course not found.");
        }
    }

    @Override
    public void delete(Course course) {
        if (courses.remove(course)) {
            notifyObservers("Course deleted: " + course.getCourseCode());
        } else {
            System.out.println("Course not found.");
        }
    }

    public void clearAll() {
        synchronized (this) {
            courses.clear();
            notifyObservers("All courses cleared.");
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
