package Command;

import Model.Dao.StudentDao;
import Model.Dao.TeacherDao;

import java.util.List;

public class VersionControlCommand implements Command {
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final List<Command> commandsToExecute;

    public VersionControlCommand(StudentDao studentDao, TeacherDao teacherDao, List<Command> commandsToExecute) {
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.commandsToExecute = commandsToExecute;
    }

    @Override
    public void execute() throws Exception {
        // Clear current state of both DAOs
        studentDao.clearAll();
        teacherDao.clearAll();

        // Re-execute commands up to the selected point
        for (Command command : commandsToExecute) {
            command.execute();
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Undo not supported for VersionControlCommand.");
    }

    @Override
    public String getDescription() {
        if (commandsToExecute.isEmpty()) {
            return "Reverted to initial state.";
        }
        return "Reverted to version: " + commandsToExecute.get(commandsToExecute.size() - 1).getDescription();
    }
}
