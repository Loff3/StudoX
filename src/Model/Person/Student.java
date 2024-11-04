package Model.Person;

public class Student {
    private final String personID;
    private final String name;
    private final String personalNumber;
    private final String email;
    private final String phoneNumber;
    private final String program;

    public Student(String personID, String name, String personalNumber, String email, String phoneNumber, String program) {
        this.personID = personID;
        this.name = name;
        this.personalNumber = personalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
    }
    // Copy
    public Student(Student other) {
        this.personID = other.personID;
        this.name = other.name;
        this.personalNumber = other.personalNumber;
        this.email = other.email;
        this.phoneNumber = other.phoneNumber;
        this.program = other.program;
    }


    // Getters (no setters to maintain immutability)
    public String getPersonID() {
        return personID;
    }

    public String getName() {
        return name;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProgram() {
        return program;
    }

    public void setName(String name){

    }

    public void setEmail(String email) {

    }

    public void setPersonalNumber(String number) {

    }

    public void setProgram(String program) {

    }

    public void setPhoneNumber(String number) {

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Student)) return false;
        Student other = (Student) obj;
        return personID != null && personID.equals(other.getPersonID());
    }

    @Override
    public int hashCode() {
        return personID != null ? personID.hashCode() : 0;
    }



}
