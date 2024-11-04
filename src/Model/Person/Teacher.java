package Model.Person;

import java.util.List;

public class Teacher {
    private final String personID;
    private final String name;
    private final String personalNumber;
    private final String email;
    private final String phoneNumber;
    private final String program; // Head teacher of a program
    private final List<String> courses; // List of courses the teacher teaches

    public Teacher(String personID, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) {
        this.personID = personID;
        this.name = name;
        this.personalNumber = personalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.courses = courses;
    }

    // Copy Constructor
    public Teacher(Teacher other) {
        this.personID = other.personID;
        this.name = other.name;
        this.personalNumber = other.personalNumber;
        this.email = other.email;
        this.phoneNumber = other.phoneNumber;
        this.program = other.program;
        this.courses = other.courses;
    }

    // Getters (no setters to maintain immutability)
    public String getPersonID() { return personID; }
    public String getName() { return name; }
    public String getPersonalNumber() { return personalNumber; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProgram() { return program; }
    public List<String> getCourses() { return courses; }

    // Empty setters to maintain immutability (as per existing Student class)
    public void setName(String name) { }
    public void setEmail(String email) { }
    public void setPersonalNumber(String number) { }
    public void setProgram(String program) { }
    public void setPhoneNumber(String number) { }
    public void setCourses(List<String> courses) { }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Teacher other)) return false;
        return personID != null && personID.equals(other.getPersonID());
    }

    @Override
    public int hashCode() {
        return personID != null ? personID.hashCode() : 0;
    }
}
