package issuetracker.sqlService;

public class SqlRetrievalFailureException extends RuntimeException {
    public SqlRetrievalFailureException(String Message) {
        super(Message);
    }

}
