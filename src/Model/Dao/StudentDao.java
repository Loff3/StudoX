package Model.Dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import Model.Person.Student;
import ObserverPattern.Observable;
import ObserverPattern.Observer;

public class StudentDao implements Dao<Student>, Observable {
    private List<Student> students = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    // Thread-safe Singleton
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
        notifyObservers("Student added: " + student.getName());
    }

    @Override
    public void update(Student student, Student updatedStudent) {
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setPersonalNumber(updatedStudent.getPersonalNumber());
        student.setProgram(updatedStudent.getProgram());
        student.setPhoneNumber(updatedStudent.getPhoneNumber());

        notifyObservers("Student added: " + formatStudentData(student));
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
        if (students.remove(student)) {
            notifyObservers("Student deleted: " + formatStudentData(student));
        } else {
            System.out.println("Error Student Not Found");
        }
    }


    public void clearAll() {
        students.clear();
        notifyObservers("All students cleared.");
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

    private String formatStudentData(Student student) {
        // Format: ID,Name,PersonalNumber,Email,PhoneNumber,Program
        return String.join(",",
                student.getPersonID(),
                student.getName(),
                student.getPersonalNumber(),
                student.getEmail(),
                student.getPhoneNumber(),
                student.getProgram()
        );
    }
    
    public void editStudent(String personID, Student updatedStudent) {
        Optional<Student> studentOpt = get(personID);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(updatedStudent.getName());
            student.setEmail(updatedStudent.getEmail());
            student.setPersonalNumber(updatedStudent.getPersonalNumber());
            student.setProgram(updatedStudent.getProgram());
            student.setPhoneNumber(updatedStudent.getPhoneNumber());

            notifyObservers("Student edited: " + formatStudentData(student));
        }
    }
    
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) { // Anpassa efter antalet attribut
                    String name = parts[0].trim();
                    String personalNumber = parts[1].trim();
                    String email = parts[2].trim();
                    String phoneNumber = parts[3].trim();
                    String program = parts[4].trim();

                    // Skapa studentobjekt och lägg till i listan
                    Student student = new Student(UUID.randomUUID().toString(), name, personalNumber, email, phoneNumber, program);
                    students.add(student);
                }
            }
            notifyObservers("Data loaded from file.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Student student : students) {
                writer.write(student.getName() + ";" + 
                             student.getPersonalNumber() + ";" + 
                             student.getEmail() + ";" + 
                             student.getPhoneNumber() + ";" + 
                             student.getProgram() + "\n");
            }
            System.out.println("Data saved to file.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}


