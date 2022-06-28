package pkg.metadata;

public class Config {
    private String privateKeyFile;
    private String sourceGetFilePath;
    private String sourcePutFilePath;
    private String destinationGetFilePath;
    private String destinationPutFilePath;
    private String sourceFile;
    private String destinationFile;
    private String user;
    private String host;
    private int port;
    private String vmName;
    private String destinationDatabasePath;
    private String vmResources;
    private String tempVmResources;

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public void setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    public String getSourceGetFilePath() {
        return sourceGetFilePath;
    }

    public void setSourceGetFilePath(String sourceGetFilePath) {
        this.sourceGetFilePath = sourceGetFilePath;
    }

    public String getSourcePutFilePath() {
        return sourcePutFilePath;
    }

    public void setSourcePutFilePath(String sourcePutFilePath) {
        this.sourcePutFilePath = sourcePutFilePath;
    }

    public String getDestinationGetFilePath() {
        return destinationGetFilePath;
    }

    public void setDestinationGetFilePath(String destinationGetFilePath) {
        this.destinationGetFilePath = destinationGetFilePath;
    }

    public String getDestinationPutFilePath() {
        return destinationPutFilePath;
    }

    public void setDestinationPutFilePath(String destinationPutFilePath) {
        this.destinationPutFilePath = destinationPutFilePath;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getDestinationDatabasePath() {
        return destinationDatabasePath;
    }

    public void setDestinationDatabasePath(String destinationDatabasePath) {
        this.destinationDatabasePath = destinationDatabasePath;
    }

    public String getVmResources() {
        return vmResources;
    }

    public void setVmResources(String vmResources) {
        this.vmResources = vmResources;
    }

    public String getTempVmResources() {
        return tempVmResources;
    }

    public void setTempVmResources(String tempVmResources) {
        this.tempVmResources = tempVmResources;
    }
}
