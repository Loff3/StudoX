import javax.swing.SwingUtilities;

import Commando.CommandInvoker;
import Controller.Controller;
import Controller.ControllerInterface;
import Model.Dao.HistoryDao;
import Model.Dao.StudentDao;
import View.Login;
import View.View;
//import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {
	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {

			// try {
			// UIManager.setLookAndFeel( new FlatDarculaLaf() );
			// } catch( Exception ex ) {
			// System.err.println( "Failed to initialize LaF" );
			// }
			// Instantiate DAOs
            StudentDao studentDao = StudentDao.getInstance();
            HistoryDao historyDao = HistoryDao.getInstance();
            studentDao.loadFromFile("students.txt");
            // Instantiate CommandInvoker with DAOs
            CommandInvoker commandInvoker = new CommandInvoker(historyDao, studentDao);

            // Instantiate Controller with CommandInvoker
            ControllerInterface controller = new Controller(commandInvoker);

            // Show Login window first
            Login loginWindow = new Login(controller);
            loginWindow.setVisible(true);
            
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                studentDao.saveToFile("students.txt");
            }));
        });
    }

}
