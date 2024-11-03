import Command.CommandInvoker;
import Controller.Controller;
import Controller.ControllerInterface;
import Model.Dao.HistoryDao;
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
            // Instantiate DAOs
            StudentDao studentDao = StudentDao.getInstance();
            HistoryDao historyDao = HistoryDao.getInstance();

            // Instantiate CommandInvoker with DAOs
            CommandInvoker commandInvoker = new CommandInvoker(historyDao, studentDao);

            // Instantiate Controller with CommandInvoker
            ControllerInterface controller = new Controller(commandInvoker);

            // Instantiate View and set Controller and StudentService
            View view = new View(controller);


        });
    }
}
