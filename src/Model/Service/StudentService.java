package Model.Service;

import Controller.ControllerInterface;
import Model.Person.Student;
import ObserverPattern.Observer;

import java.util.*;
import java.util.regex.Pattern;

public class StudentService implements Observer {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_REGEX = "^\\+?\\d{10,15}$";
    private static final String PERSONAL_NUMBER_REGEX = "^\\d{10,12}$";

    // Local caches for uniqueness checks
    private final Set<String> emails = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> phoneNumbers = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> personalNumbers = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> studentIds = Collections.synchronizedSet(new HashSet<>());

    // Map to keep track of counters for each program initial
    private final Map<Character, Integer> programCounters = Collections.synchronizedMap(new HashMap<>());

    public StudentService(ControllerInterface controller) {
        controller.addObserver(this);
    }

    public Student createStudent(String name, String personalNumber, String email, String phoneNumber, String program) throws Exception {
        // Validate inputs
        validatePersonalNumber(personalNumber);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);

        // Generate unique identifier
        String studentId = generateUniqueIdentifier(program);

        // Create the Student object
        Student student = new Student(studentId, name, personalNumber, email, phoneNumber, program);

        // Update local caches
        synchronized (this) {
            emails.add(email.toLowerCase());
            phoneNumbers.add(phoneNumber);
            personalNumbers.add(personalNumber);
            studentIds.add(studentId);
        }

        return student;
    }

    private void validateEmail(String email) throws Exception {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        synchronized (this) {
            if (emails.contains(email.toLowerCase())) {
                throw new IllegalArgumentException("Email already exists.");
            }
        }
    }

    private void validatePhoneNumber(String phoneNumber) throws Exception {
        if (!Pattern.matches(PHONE_REGEX, phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        synchronized (this) {
            if (phoneNumbers.contains(phoneNumber)) {
                throw new IllegalArgumentException("Phone number already exists.");
            }
        }
    }

    private void validatePersonalNumber(String personalNumber) throws Exception {
        if (!Pattern.matches(PERSONAL_NUMBER_REGEX, personalNumber)) {
            throw new IllegalArgumentException("Invalid personal number format.");
        }
        synchronized (this) {
            if (personalNumbers.contains(personalNumber)) {
                throw new IllegalArgumentException("Personal number already exists.");
            }
        }
    }

    private String generateUniqueIdentifier(String program) {
        char programInitial = Character.toLowerCase(program.charAt(0));

        int counter;
        synchronized (this) {
            counter = programCounters.getOrDefault(programInitial, 0) + 1;
            programCounters.put(programInitial, counter);
        }

        // Format ID with leading zeros if needed
        String uniqueId = String.format("%c%04d", programInitial, counter);
        return uniqueId;
    }

    @Override
    public void update(String message) {

        // Example message formats:
        // "Student added: [student details]"
        // "Student deleted: [student details]"

        // For simplicity, let's assume the message is in the format:
        // "Student added: [ID],[Name],[PersonalNumber],[Email],[PhoneNumber],[Program]"

        if (message.startsWith("Student added: ")) {
            String data = message.substring("Student added: ".length());
            String[] parts = data.split(",", -1); // -1 to include empty strings
            if (parts.length == 6) {
                String email = parts[3].trim().toLowerCase();
                String phoneNumber = parts[4].trim();
                String personalNumber = parts[2].trim();
                String studentId = parts[0].trim();

                synchronized (this) {
                    emails.add(email);
                    phoneNumbers.add(phoneNumber);
                    personalNumbers.add(personalNumber);
                    studentIds.add(studentId);
                }
            }
        } else if (message.startsWith("Student deleted: ")) {
            String data = message.substring("Student deleted: ".length());
            String[] parts = data.split(",", -1);
            if (parts.length == 6) {
                String email = parts[3].trim().toLowerCase();
                String phoneNumber = parts[4].trim();
                String personalNumber = parts[2].trim();
                String studentId = parts[0].trim();

                synchronized (this) {
                    emails.remove(email);
                    phoneNumbers.remove(phoneNumber);
                    personalNumbers.remove(personalNumber);
                    studentIds.remove(studentId);
                }
            }
        }
    }
}
