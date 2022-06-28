package pkg.transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TransactionManager {
    private boolean transaction = false;
    private List<String> transactionQueries = new ArrayList<>();
    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    public List<String> getTransactionQueries() {
        return transactionQueries;
    }

    public void setTransactionQueries(List<String> transactionQueries) {
        this.transactionQueries = transactionQueries;
    }

}
