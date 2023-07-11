package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionService {

    private QueryService queryService;

    private ArrayList<String> queries;
    private String COMMIT_IDENTIFIER = "COMMIT";
    private String ROLLBACK_IDENTIFIER = "ROLLBACK";
    private String BEGIN_IDENTIFIER = "BEGIN TRANSACTION";
    private String END_IDENTIFIER = "END TRANSACTION";

    public TransactionService(QueryService queryService) {
        this.queryService = queryService;
        this.queries = new ArrayList<>();
    }

    /**
     * Execute the transaction for a user based on the query
     * @param userName
     * @param query
     * @return
     */
    public boolean executeTransaction (String userName, String query) {
        if(!query.toUpperCase().contains("BEGIN TRANSACTION")) {
            return false;
        }

        for (String q : query.trim().split(";")) {
            queries.add(q);
        }

        boolean canCommit = false;
        for(int i=0; i< queries.size(); i++) {
            if(queries.get(i).trim().toUpperCase().equals(COMMIT_IDENTIFIER)) {
                canCommit = true;
            }
        }

        if(canCommit) {
            for(int i=0; i < queries.size(); i++) {

                if (queries.get(i).trim().toUpperCase().equals(BEGIN_IDENTIFIER) ||
                        queries.get(i).trim().toUpperCase().equals(END_IDENTIFIER) ||
                        queries.get(i).trim().toUpperCase().equals(COMMIT_IDENTIFIER) ||
                        queries.get(i).trim().toUpperCase().equals(ROLLBACK_IDENTIFIER)) {
                    continue;
                }
                queryService.execute(userName, queries.get(i).trim());
            }
            return true;
        }

        return false;
    }
}
