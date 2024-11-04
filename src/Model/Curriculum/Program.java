package Model.Curriculum;

import Model.Person.Student;
import Model.Person.Teacher;
import java.util.HashSet;
import java.util.Set;

public class Program {
    private final String programID; // Unique identifier
    private  String programName;
    private Teacher headTeacher;
    private Set<Student> students;
    private Set<Course> courses;

    // Constructor
    public Program(String programID, String programName) {
        this.programID = programID;
        this.programName = programName;
        this.students = new HashSet<>();
        this.courses = new HashSet<>();
    }

    // Copy Constructor
    public Program(Program other) {
        this.programID = other.programID;
        this.programName = other.programName;
        this.headTeacher = other.headTeacher;
        this.students = new HashSet<>(other.students);
        this.courses = new HashSet<>(other.courses);
    }

    // Getters and Setters
    public String getProgramID() {
        return programID;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Teacher getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(Teacher headTeacher) throws Exception {
        if (this.headTeacher != null && !this.headTeacher.equals(headTeacher)) {
            throw new Exception("This program already has a head teacher assigned.");
        }
        this.headTeacher = headTeacher;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    // Methods to manage students
    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    // Methods to manage courses
    public void addCourse(Course course) throws Exception {
        if (courses.contains(course)) {
            throw new Exception("This course is already added to the program.");
        }
        courses.add(course);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }

    // Override toString for display purposes
    @Override
    public String toString() {
        return programName;
    }

    // Equals and hashCode based on programID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Program other)) return false;
        return programID != null && programID.equals(other.getProgramID());
    }

    @Override
    public int hashCode() {
        return programID != null ? programID.hashCode() : 0;
    }

    public String getName() {return programName;
    }
}
