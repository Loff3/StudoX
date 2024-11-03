package Model.Dao;

import ObserverPattern.Observable;
import ObserverPattern.Observer;
import Command.Command;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryDao implements Observable {
    private List<CommandRecord> commandHistory = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    // Singleton Pattern
    private static volatile HistoryDao instance;

    private HistoryDao() {}

    public static HistoryDao getInstance() {
        if (instance == null) {
            synchronized (HistoryDao.class) {
                if (instance == null) {
                    instance = new HistoryDao();
                }
            }
        }
        return instance;
    }

    public void addCommand(Command command) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        CommandRecord record = new CommandRecord(command.getDescription(), timestamp, command);
        commandHistory.add(record);
        notifyObservers("History Updated");
    }

    public List<CommandRecord> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }

    public void clearHistory() {
        commandHistory.clear();
        notifyObservers("History Cleared");
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }

    // Inner class to store command records
    public static class CommandRecord {
        private String description;
        private String timestamp;
        private Command command;

        public CommandRecord(String description, String timestamp, Command command) {
            this.description = description;
            this.timestamp = timestamp;
            this.command = command;
        }

        public String getDescription() {
            return description;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public Command getCommand() {
            return command;
        }
    }
}
