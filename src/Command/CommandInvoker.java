package Command;

import Model.Dao.*;

import java.util.Stack;

public class CommandInvoker {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    private final HistoryDao historyDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final ProgramDao programDao;
    private final CourseDao courseDao;

    public CommandInvoker(HistoryDao historyDao, StudentDao studentDao, TeacherDao teacherDao, ProgramDao programDao, CourseDao courseDao) {
        this.historyDao = historyDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.programDao = programDao;
        this.courseDao = courseDao;
    }
    public void executeCommand(Command command) throws Exception{
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack when a new command is executed
        historyDao.addCommand(command); // Add to history
    }

    public HistoryDao getHistoryDao() {
        return historyDao;
    }

    public StudentDao getStudentDao() {
        return studentDao;
    }

    public TeacherDao getTeacherDao() {return teacherDao; }

    public ProgramDao getProgramDao() {return programDao; }

    public CourseDao getCourseDao() {return courseDao; }

    public void undo() throws Exception{
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);

            // Add undo action to history (optional)
            historyDao.addCommand(new UndoCommand(command));
        }
    }

    public void redo() throws Exception{
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);

            // Add RedoCommand to history
            historyDao.addCommand(new RedoCommand(command));
        }
    }


    public void executeVersionControlCommand(VersionControlCommand versionControlCommand) throws Exception{
        versionControlCommand.execute();

        // Clear undo and redo stacks since the state has changed significantly
        undoStack.clear();
        redoStack.clear();

        // Add the VersionControlCommand to history
        historyDao.addCommand(versionControlCommand);
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
