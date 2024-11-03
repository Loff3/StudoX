package Command;

public class RedoCommand implements Command {
    private final Command originalCommand;

    public RedoCommand(Command originalCommand) {
        this.originalCommand = originalCommand;
    }

    @Override
    public void execute() {
        // No action needed; the redo is already performed in CommandInvoker.redo()
    }

    @Override
    public void undo() {
        // Undo the redo by undoing the original command
        originalCommand.undo();
    }

    @Override
    public String getDescription() {
        return "Redo: " + originalCommand.getDescription();
    }
}
