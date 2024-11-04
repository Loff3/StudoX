package Model.Service;

import Model.Dao.StudentDao;
import Model.Person.Student;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StudentService {

    private static final String EMAIL_REGEX = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_REGEX = "^\\+?\\d{10,15}$";
    private static final String PERSONAL_NUMBER_REGEX = "^\\d{10,12}$";

    // Map to keep track of counters for each program initial
    private final Map<Character, Integer> programCounters = new HashMap<>();

    private final StudentDao studentDao;

    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public Student createStudent(String name, String personalNumber, String email, String phoneNumber, String program, String studentId) throws Exception {
        // Validate inputs
        validatePersonalNumber(personalNumber);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validateProgram(program);

        // Use the provided studentId if not null
        if (studentId == null) {
            // Generate unique identifier
            studentId = generateUniqueIdentifier(program);
        } else {
            // Ensure the studentId is unique
            if (studentDao.studentIdExists(studentId)) {
                System.out.println("Student ID already exists.");
                return null;
            }
        }

        // Create the Student object
        Student student = new Student(studentId, name, personalNumber, email, phoneNumber, program);

        return student;
    }

    public Student updateStudent(Student oldStudent, String name, String personalNumber, String email, String phoneNumber, String program, String newPersonID) throws Exception {
        // Validate inputs
        validateStudentUpdate(oldStudent, personalNumber, email, phoneNumber);
        validateProgram(program);

        // If newPersonID is null, generate a new one if necessary
        if (newPersonID == null) {
            newPersonID = oldStudent.getPersonID();

            // Check if the program has changed
            if (!oldStudent.getProgram().equalsIgnoreCase(program)) {
                // Generate new personID based on the new program
                newPersonID = generateUniqueIdentifier(program);
            }
        }

        // Create new Student object with the new or same personID
        Student newStudent = new Student(newPersonID, name, personalNumber, email, phoneNumber, program);

        return newStudent;
    }

    private void validateStudentUpdate(Student oldStudent, String newPersonalNumber, String newEmail, String newPhoneNumber) throws Exception {
        if (!oldStudent.getPersonalNumber().equals(newPersonalNumber)) {
            validatePersonalNumber(newPersonalNumber);
        }
        if (!oldStudent.getEmail().equalsIgnoreCase(newEmail)) {
            validateEmail(newEmail);
        }
        if (!oldStudent.getPhoneNumber().equals(newPhoneNumber)) {
            validatePhoneNumber(newPhoneNumber);
        }
    }

    private void validateProgram(String program) throws Exception {
        if (program == null || program.isEmpty()) {
            throw new Exception("Program cannot be null or empty.");
        }
        char firstChar = program.charAt(0);
        if (!Character.isLetter(firstChar)) {
            throw new Exception("Program must start with a letter.");
        }
    }

    private void validateEmail(String email) throws Exception {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new Exception("Invalid email format.");
        }
        if (studentDao.emailExists(email.toLowerCase())) {
            throw new Exception("Email already exists.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) throws Exception {
        if (!Pattern.matches(PHONE_REGEX, phoneNumber)) {
            throw new Exception("Invalid phone number format.");
        }
        if (studentDao.phoneNumberExists(phoneNumber)) {
            throw new Exception("Phone number already exists.");
        }
    }

    private void validatePersonalNumber(String personalNumber) throws Exception {
        if (!Pattern.matches(PERSONAL_NUMBER_REGEX, personalNumber)) {
            throw new Exception("Invalid personal number format.");
        }
        if (studentDao.personalNumberExists(personalNumber)) {
            throw new Exception("Personal number already exists.");
        }
    }


    private String generateUniqueIdentifier(String program) {
        char programInitial = Character.toLowerCase(program.charAt(0));

        int counter;
        synchronized (programCounters) {
            counter = programCounters.getOrDefault(programInitial, 0) + 1;
            programCounters.put(programInitial, counter);
        }

        return String.format("%c%04d", programInitial, counter);
    }
}
