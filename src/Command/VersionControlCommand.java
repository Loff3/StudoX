    package Command;

    import Model.Dao.StudentDao;
    import java.util.List;

    public class VersionControlCommand implements Command {
        private final StudentDao studentDao;
        private final List<Command> commandsToExecute;

        public VersionControlCommand(StudentDao studentDao, List<Command> commandsToExecute) {
            this.studentDao = studentDao;
            this.commandsToExecute = commandsToExecute;
        }

        @Override
        public void execute() throws Exception{
            // Clear current state
            studentDao.clearAll();

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
            return "Reverted to version: " + commandsToExecute.get(commandsToExecute.size() - 1).getDescription();
        }
    }
