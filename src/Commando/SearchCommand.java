package Commando;


import Model.Dao.StudentDao;
import Model.Person.Student;

import java.util.List;

public class SearchCommand implements Command {
    private final StudentDao studentDao;
    private final String query;
    private List<Student> searchResults;

    public SearchCommand(StudentDao studentDao, String query) {
        this.studentDao = studentDao;
        this.query = query;
    }

    @Override
    public void execute() {
        searchResults = studentDao.search(query);
        // Du kan välja att notifiera observatörer här om det behövs
    }

    public List<Student> getSearchResults() {
        return searchResults;
    }
}

