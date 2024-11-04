package Command;

import Model.Dao.ProgramDao;
import Model.Curriculum.Program;

public class DeleteProgramCommand implements Command {
    private final ProgramDao programDao;
    private final String programName;
    private Program program;

    public DeleteProgramCommand(ProgramDao programDao, Program program) {
        this.programDao = programDao;
        this.programName = program.getName();
    }

    @Override
    public void execute() throws Exception {
        program = programDao.get(programName).orElse(null);
        if (program == null) {
            throw new Exception("Program not found during delete.");
        }
        programDao.delete(program);
    }

    @Override
    public void undo() throws Exception {
        if (program != null) {
            programDao.save(program);
        } else {
            throw new Exception("No program to undo delete.");
        }
    }

    @Override
    public String getDescription() {
        return "Delete Program: " + programName;
    }
}
