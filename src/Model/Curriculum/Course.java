package Model.Curriculum;


import Model.Person.Student;
import Model.Person.Teacher;
import java.util.HashSet;
import java.util.Set;

public class Course {
    private final String courseCode;
    private final String courseName;
    private Teacher teacher;
    private Set<Student> students;
    private Set<Program> programs;

    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.students = new HashSet<>();
        this.programs = new HashSet<>();
    }

    // Copy Constructor
    public Course(Course other) {
        this.courseCode = other.courseCode;
        this.courseName = other.courseName;
        this.teacher = other.teacher;
        this.students = new HashSet<>(other.students);
        this.programs = new HashSet<>(other.programs);
    }

    // Getters
    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    // Methods to manage teacher
    public void setTeacher(Teacher teacher) throws Exception {
        if (this.teacher != null && !this.teacher.equals(teacher)) {
            throw new Exception("This course already has a teacher assigned.");
        }
        this.teacher = teacher;
    }

    // Methods to manage students
    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    // Methods to manage programs
    public void addProgram(Program program) {
        programs.add(program);
    }

    public void removeProgram(Program program) {
        programs.remove(program);
    }

    // Equals and hashCode based on course code
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Course other)) return false;
        return courseCode != null && courseCode.equals(other.getCourseCode());
    }

    @Override
    public int hashCode() {
        return courseCode != null ? courseCode.hashCode() : 0;
    }
}
