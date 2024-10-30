import Commando.CommandInvoker;
import Controller.Controller;
import Model.Dao.StudentDao;
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
            // Create DAO
            StudentDao studentDao = StudentDao.getInstance();

            // Create CommandInvoker
            CommandInvoker commandInvoker = new CommandInvoker();

            // Create Controller
            Controller controller = new Controller(commandInvoker, studentDao);

            // Create View and set Controller
            View view = new View();
            view.setController(controller);

            // Register View as observer to StudentDao
            studentDao.addObserver(view);
        });
    }
}
