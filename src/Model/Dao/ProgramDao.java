package Model.Dao;

import Model.Curriculum.Program;
import ObserverPattern.Observable;
import ObserverPattern.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramDao implements Dao<Program>, Observable {
    private final List<Program> programs = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();

    // Singleton pattern
    private static volatile ProgramDao instance;

    private ProgramDao() { }

    public static ProgramDao getInstance() {
        if (instance == null) {
            synchronized (ProgramDao.class) {
                if (instance == null) {
                    instance = new ProgramDao();
                }
            }
        }
        return instance;
    }

    // Method to get program by ID
    public Program getProgramById(String programID) {
        Optional<Program> programOpt = programs.stream()
                .filter(program -> program.getProgramID().equals(programID))
                .findFirst();
        return programOpt.orElse(null);
    }


    // Method to add a program
    public void addProgram(Program program) {
        programs.add(program);
        notifyObservers("Program added: " + program.getProgramID());
    }

    @Override
    public Optional<Program> get(String programID) {
        return programs.stream()
                .filter(program -> program.getProgramID().equals(programID))
                .findFirst();
    }

    @Override
    public List<Program> getAll() {
        return new ArrayList<>(programs);
    }

    @Override
    public void save(Program program) {
        programs.add(program);
        notifyObservers("Program added: " + program.getProgramID());
    }

    @Override
    public void update(Program oldProgram, Program newProgram) {
        int index = programs.indexOf(oldProgram);
        if (index >= 0) {
            programs.set(index, newProgram);
            notifyObservers("Program updated: " + newProgram.getProgramID());
        } else {
            System.out.println("Program not found.");
        }
    }

    @Override
    public void delete(Program program) {
        if (programs.remove(program)) {
            notifyObservers("Program deleted: " + program.getProgramID());
        } else {
            System.out.println("Program not found.");
        }
    }

    public void clearAll() {
        synchronized (this) {
            programs.clear();
            notifyObservers("All programs cleared.");
        }
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
}
