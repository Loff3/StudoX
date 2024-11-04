package Model.Service;

import Model.Dao.TeacherDao;
import Model.Person.Teacher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TeacherService {

    private static final String EMAIL_REGEX = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_REGEX = "^\\+?\\d{10,15}$";
    private static final String PERSONAL_NUMBER_REGEX = "^\\d{10,12}$";

    // Map to keep track of counters for each program initial
    private final Map<Character, Integer> programCounters = new HashMap<>();

    private final TeacherDao teacherDao;

    public TeacherService(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public Teacher createTeacher(String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses, String teacherId) throws Exception {
        // Validate inputs
        validatePersonalNumber(personalNumber);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validateProgram(program);
        validateCourses(courses);

        // Use the provided teacherId if not null
        if (teacherId == null) {
            // Generate unique identifier
            teacherId = generateUniqueIdentifier(program);
        } else {
            // Ensure the teacherId is unique
            if (teacherDao.teacherIdExists(teacherId)) {
                throw new Exception("Teacher ID already exists.");
            }
        }

        // Create the Teacher object
        Teacher teacher = new Teacher(teacherId, name, personalNumber, email, phoneNumber, program, courses);

        return teacher;
    }

    public Teacher updateTeacher(Teacher oldTeacher, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses, String newPersonID) throws Exception {
        // Validate inputs
        validateTeacherUpdate(oldTeacher, personalNumber, email, phoneNumber);
        validateProgram(program);
        validateCourses(courses);

        // If newPersonID is null, retain the old one or generate a new one if the program changes
        if (newPersonID == null) {
            newPersonID = oldTeacher.getPersonID();

            // Check if the program has changed
            if (!oldTeacher.getProgram().equalsIgnoreCase(program)) {
                // Generate new personID based on the new program
                newPersonID = generateUniqueIdentifier(program);
            }
        } else {
            // Ensure the newPersonID is unique
            if (!newPersonID.equals(oldTeacher.getPersonID()) && teacherDao.teacherIdExists(newPersonID)) {
                throw new Exception("New Person ID already exists.");
            }
        }

        // Create new Teacher object with the new or same personID
        Teacher newTeacher = new Teacher(newPersonID, name, personalNumber, email, phoneNumber, program, courses);

        return newTeacher;
    }

    private void validateTeacherUpdate(Teacher oldTeacher, String newPersonalNumber, String newEmail, String newPhoneNumber) throws Exception {
        if (!oldTeacher.getPersonalNumber().equals(newPersonalNumber)) {
            validatePersonalNumber(newPersonalNumber);
        }
        if (!oldTeacher.getEmail().equalsIgnoreCase(newEmail)) {
            validateEmail(newEmail);
        }
        if (!oldTeacher.getPhoneNumber().equals(newPhoneNumber)) {
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

    private void validateCourses(List<String> courses) throws Exception {
        if (courses == null) {
            throw new Exception("Courses cannot be null.");
        }
        for (String course : courses) {
            if (course == null || course.trim().isEmpty()) {
                throw new Exception("Course names cannot be null or empty.");
            }
        }
    }

    private void validateEmail(String email) throws Exception {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new Exception("Invalid email format.");
        }
        if (teacherDao.emailExists(email.toLowerCase())) {
            throw new Exception("Email already exists.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) throws Exception {
        if (!Pattern.matches(PHONE_REGEX, phoneNumber)) {
            throw new Exception("Invalid phone number format.");
        }
        if (teacherDao.phoneNumberExists(phoneNumber)) {
            throw new Exception("Phone number already exists.");
        }
    }

    private void validatePersonalNumber(String personalNumber) throws Exception {
        if (!Pattern.matches(PERSONAL_NUMBER_REGEX, personalNumber)) {
            throw new Exception("Invalid personal number format.");
        }
        if (teacherDao.personalNumberExists(personalNumber)) {
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
