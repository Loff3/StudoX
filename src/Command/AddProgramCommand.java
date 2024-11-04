package Command;

import Model.Dao.ProgramDao;
import Model.Curriculum.Program;
import Model.Service.ProgramService;

public class AddProgramCommand implements Command {
    private final ProgramDao programDao;
    private final ProgramService programService;
    private final String id;
    private final String name;
    private Program program;

    public AddProgramCommand(ProgramDao programDao, ProgramService programService, String id, String name) {
        this.programDao = programDao;
        this.id = id;
        this.programService = programService;
        this.name = name;
    }

    @Override
    public void execute() throws Exception {
        program = programService.createProgram(id, name);
        if (program == null) {
            throw new Exception("Failed to create program.");
        }
        programDao.save(program);
    }

    @Override
    public void undo() throws Exception {
        if (program != null) {
            programDao.delete(program);
        } else {
            throw new Exception("No program to undo.");
        }
    }

    @Override
    public String getDescription() {
        return "Add Program: " + (program != null ? program.getName() : "N/A");
    }
}
