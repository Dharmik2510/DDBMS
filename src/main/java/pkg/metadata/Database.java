package pkg.metadata;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private String vmName;
    private List<String> tables = new ArrayList<>();

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}
