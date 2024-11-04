package Command;

public class UndoCommand implements Command {
    private final Command originalCommand;

    public UndoCommand(Command originalCommand) {
        this.originalCommand = originalCommand;
    }

    @Override
    public void execute() {

    }

    @Override
    public void undo() throws Exception{
        // Redo the original command
        originalCommand.execute();

    }

    @Override
    public String getDescription() {
        return "Undo: " + originalCommand.getDescription();
    }
}
