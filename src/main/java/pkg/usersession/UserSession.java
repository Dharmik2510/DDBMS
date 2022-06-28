package pkg.usersession;

public class UserSession {
    private String userName;
    private String selectedDatabase;
    private String virtualMachineName;
    public UserSession() {
    }

    public UserSession(String userName, String selectedDatabase,String virtualMachineName) {
        this.userName = userName;
        this.selectedDatabase = selectedDatabase;
        this.virtualMachineName = virtualMachineName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSelectedDatabase() {
        return selectedDatabase;
    }

    public void setSelectedDatabase(String selectedDatabase) {
        this.selectedDatabase = selectedDatabase;
    }

    public String getVirtualMachineName() {
        return virtualMachineName;
    }

    public void setVirtualMachineName(String virtualMachineName) {
        this.virtualMachineName = virtualMachineName;
    }
}
