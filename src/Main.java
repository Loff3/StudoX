import Commando.CommandInvoker;
import Controller.Controller;
import Model.Dao.StudentDao;
import View.View;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
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
