import java.util.List;

public class BankStatementProcessor {
    private final List<BankTransaction> bankTransaction;
    public BankStatementProcessor(final List<BankTransaction> bankTransactions)
    {
        this.bankTransaction = bankTransactions;

    }
    public double calculateTotalAmount(){
        return 0;
    }
}
