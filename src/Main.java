import Command.CommandInvoker;
import Controller.Controller;
import Controller.ControllerInterface;
import Model.Dao.HistoryDao;
import Model.Dao.StudentDao;
import Model.Person.Student;
import Model.Service.StudentService;
import View.View;
import com.formdev.flatlaf.FlatDarculaLaf;

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

            // Instantiate StudentService with StudentDao
            StudentService studentService = new StudentService(studentDao);

            // Instantiate CommandInvoker with DAOs
            CommandInvoker commandInvoker = new CommandInvoker(historyDao, studentDao);

            // Instantiate Controller with CommandInvoker and StudentService
            ControllerInterface controller = new Controller(commandInvoker, studentService);

            // Instantiate View and set Controller
            View view = new View(controller);
        });
    }
}
