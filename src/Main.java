import Command.CommandInvoker;
import Controller.Controller;
import Controller.ControllerInterface;
import Model.Dao.*;
import Model.Service.CourseService;
import Model.Service.ProgramService;
import Model.Service.StudentService;
import Model.Service.TeacherService;
import View.View;
import com.formdev.flatlaf.FlatDarculaLaf;
import View.Login;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel( new FlatDarculaLaf() );
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
            // Instantiate DAOs
            StudentDao studentDao = StudentDao.getInstance();
            HistoryDao historyDao = HistoryDao.getInstance();
            TeacherDao teacherDao = TeacherDao.getInstance();
            ProgramDao programDao = ProgramDao.getInstance();
            CourseDao courseDao = CourseDao.getInstance();

            // Instantiate Services with Dao's
            StudentService studentService = new StudentService(studentDao);
            TeacherService teacherService = new TeacherService(teacherDao);
            ProgramService programService = new ProgramService(programDao);
            CourseService courseService = new CourseService(courseDao);


            // Instantiate CommandInvoker with DAOs
            CommandInvoker commandInvoker = new CommandInvoker(historyDao, studentDao, teacherDao, programDao, courseDao);

            // Instantiate Controller with CommandInvoker and StudentService
            ControllerInterface controller = new Controller(commandInvoker, studentService, teacherService, programService, courseService);

            // Instantiate View and set Controller


            Login loginWindow = new Login(controller);
            loginWindow.setVisible(true);
           // View view = new View(controller);
        });
    }
}
