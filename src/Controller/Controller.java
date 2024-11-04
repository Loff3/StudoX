package Controller;

import Command.*;
import Model.Curriculum.Course;
import Model.Curriculum.Program;
import Model.Dao.*;
import Model.Person.Student;
import Model.Person.Teacher;
import Model.Service.CourseService;
import Model.Service.ProgramService;
import Model.Service.StudentService;
import Model.Service.TeacherService;
import ObserverPattern.Observer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Controller implements ControllerInterface {
    // Dao's
    private final CommandInvoker commandInvoker;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final ProgramDao programDao;
    private final HistoryDao historyDao;
    private final CourseDao courseDao;

    // Services
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ProgramService programService;
    private final CourseService courseService;

    // Login
    private final Set<String> validUsernames;

    public Controller(CommandInvoker commandInvoker, StudentService studentService, TeacherService teacherService, ProgramService programService, CourseService courseService) {
        this.commandInvoker = commandInvoker;
        this.studentDao = commandInvoker.getStudentDao();
        this.teacherDao = commandInvoker.getTeacherDao();
        this.programDao = commandInvoker.getProgramDao();
        this.courseDao = commandInvoker.getCourseDao();
        this.historyDao = commandInvoker.getHistoryDao();

        this.studentService = studentService;
        this.teacherService = teacherService;
        this.programService = programService;
        this.courseService = courseService;

        this.validUsernames = loadValidUsernames("valid_usernames.txt");
    }


    private Set<String> loadValidUsernames(String filename) {
        Set<String> usernames = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                usernames.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error loading usernames: " + e.getMessage());
        }
        return usernames;
    }

    @Override
    public void revertToVersion(int historyIndex) throws Exception {
        // Get the list of commands up to the selected point
        List<HistoryDao.CommandRecord> history = historyDao.getCommandHistory();

        if (historyIndex < 0 || historyIndex >= history.size()) {
            throw new IllegalArgumentException("Invalid history index.");
        }

        // Collect commands to execute
        List<Command> commandsToExecute = new ArrayList<>();
        for (int i = 0; i <= historyIndex; i++) {
            commandsToExecute.add(history.get(i).getCommand());
        }

        // Create and execute VersionControlCommand with both DAOs
        VersionControlCommand versionControlCommand = new VersionControlCommand(studentDao, teacherDao, commandsToExecute);
        commandInvoker.executeVersionControlCommand(versionControlCommand);
    }

    // Teacher methods
    @Override
    public void addTeacher(String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) throws Exception {
        Command addCommand = new AddTeacherCommand(teacherDao, teacherService, name, personalNumber, email, phoneNumber, program, courses);
        commandInvoker.executeCommand(addCommand);
    }

    @Override
    public void updateTeacher(Teacher oldTeacher, String name, String personalNumber, String email, String phoneNumber, String program, List<String> courses) throws Exception {
        Command updateCommand = new UpdateTeacherCommand(teacherDao, teacherService, oldTeacher, name, personalNumber, email, phoneNumber, program, courses);
        commandInvoker.executeCommand(updateCommand);
    }

    @Override
    public void deleteTeacher(Teacher teacher) throws Exception {
        Command deleteCommand = new DeleteTeacherCommand(teacherDao, teacher);
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public Teacher getTeacherById(String teacherId) {
        Optional<Teacher> teacherOpt = teacherDao.get(teacherId);
        if (teacherOpt.isPresent()) {
            return teacherOpt.get();
        } else {
            throw new IllegalArgumentException("Teacher not found.");
        }
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherDao.getAll();
    }

    @Override
    public void addCourse(String courseCode, String courseName, Teacher teacher) throws Exception {
        Command addCommand = new AddCourseCommand(courseDao, courseService, courseCode, courseName);
        commandInvoker.executeCommand(addCommand);
    }

    @Override
    public void updateCourse(Course oldCourse, String courseName, Teacher teacher, Set<Student> students, Set<Program> programs) throws Exception {
        Command updateCommand = new UpdateCourseCommand(courseDao, courseService, oldCourse, courseName, teacher, students, programs);
        commandInvoker.executeCommand(updateCommand);
    }

    @Override
    public void deleteCourse(Course course) throws Exception {
        Command deleteCommand = new DeleteCourseCommand(courseDao, course);
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public Course getCourseByCode(String courseCode) {
        Optional<Course> courseOpt = courseDao.get(courseCode);
        if (courseOpt.isPresent()) {
            return courseOpt.get();
        } else {
            throw new IllegalArgumentException("Course not found.");
        }
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.getAll();
    }

    // Student methods
    @Override
    public void addStudent(String name, String personalNumber, String email, String phoneNumber, String program) throws Exception {
        Command addCommand = new AddStudentCommand(studentDao, studentService, name, personalNumber, email, phoneNumber, program);
        commandInvoker.executeCommand(addCommand);
    }

    @Override
    public void updateStudent(Student oldStudent, String name, String personalNumber, String email, String phoneNumber, String program) throws Exception {
        Command updateCommand = new UpdateStudentCommand(studentDao, studentService, oldStudent, name, personalNumber, email, phoneNumber, program);
        commandInvoker.executeCommand(updateCommand);
    }

    @Override
    public void deleteStudent(Student student) throws Exception {
        Command deleteCommand = new DeleteStudentCommand(studentDao, student);
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public Student getStudentById(String studentId) {
        Optional<Student> studentOpt = studentDao.get(studentId);
        if (studentOpt.isPresent()) {
            return studentOpt.get();
        } else {
            throw new IllegalArgumentException("Student not found.");
        }
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.getAll();
    }


    @Override
    public void updateProgram(Program oldProgram, Teacher headTeacher, Set<Student> students, Set<Course> courses) throws Exception {
        Command updateCommand = new UpdateProgramCommand(programDao, programService, oldProgram, headTeacher, students, courses);
        commandInvoker.executeCommand(updateCommand);
    }

    @Override
    public void deleteProgram(Program program) throws Exception {
        Command deleteCommand = new DeleteProgramCommand(programDao, program);
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public Program getProgramById(String programID) throws Exception {
        Program program = programDao.getProgramById(programID);
        if (program == null) {
            throw new Exception("Program with ID " + programID + " does not exist.");
        }
        return program;
    }


    @Override
    public List<Program> getAllPrograms() {
        return programDao.getAll();
    }


    // Undo/Redo methods
    @Override
    public void undo() throws Exception {
        commandInvoker.undo();
    }

    @Override
    public void redo() throws Exception {
        commandInvoker.redo();
    }

    @Override
    public boolean canUndo() {
        return commandInvoker.canUndo();
    }

    @Override
    public boolean canRedo() {
        return commandInvoker.canRedo();
    }

    // Observer methods
    @Override
    public void addObserver(Observer o) {
        studentDao.addObserver(o);
        teacherDao.addObserver(o);
        programDao.addObserver(o);
        historyDao.addObserver(o);
        courseDao.addObserver(o);
    }

    @Override
    public void removeObserver(Observer o) {
        studentDao.removeObserver(o);
        teacherDao.removeObserver(o);
        programDao.removeObserver(o);
        historyDao.removeObserver(o);
        courseDao.removeObserver(o);
    }

    @Override
    public List<HistoryDao.CommandRecord> getCommandHistory() {
        return historyDao.getCommandHistory();
    }


    @Override
    public void addProgram(String programID, String programName, Teacher headTeacher) throws Exception {
        // Check if the program with the given ID already exists
        if (programDao.getProgramById(programID) != null) {
            throw new Exception("Program with ID " + programID + " already exists.");
        }

        // Create a new Program instance
        Program program = new Program(programID, programName);

        // Assign the head teacher if provided
        if (headTeacher != null) {
            program.setHeadTeacher(headTeacher);
        }

        // Add the program to the DAO
        programDao.addProgram(program);


    }

    @Override
    public boolean attemptLogin(String username) {
        if (validUsernames.contains(username)) {
            System.out.println("Login successful for username: " + username);
            return true;
        } else {
            System.out.println("Invalid username: " + username);
            return false;
        }
    }


}
