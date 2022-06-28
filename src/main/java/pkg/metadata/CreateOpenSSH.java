package pkg.metadata;

import com.jcraft.jsch.*;

import java.io.File;
import java.util.List;

public class CreateOpenSSH {
    private final String SEND = "send";
    private final String RECEIVE = "receive";
    private final String SIMPLE_FILE_TRANSFER_PROTOCOL="sftp";
    private Config config;
    public CreateOpenSSH(Config config) {
        this.config = config;
    }

    public void fileTransfer(String transferType, String fileName){
        Session session = null;
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(config.getPrivateKeyFile());

            session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel(SIMPLE_FILE_TRANSFER_PROTOCOL);
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();

            ChannelSftp c = (ChannelSftp) channel;

            if(transferType.equalsIgnoreCase(SEND)){
                c.put(config.getSourcePutFilePath()+fileName, config.getDestinationPutFilePath()+fileName);
            }else if(transferType.equalsIgnoreCase(RECEIVE)){
                c.get(config.getDestinationGetFilePath()+fileName,config.getSourceGetFilePath()+fileName);
            }

            c.exit();

        } catch (JSchException | SftpException e) {

            e.printStackTrace();

        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void directoryTransfer(String transferType, String database, List<String> tableName){
        Session session = null;
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(config.getPrivateKeyFile());

            session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel(SIMPLE_FILE_TRANSFER_PROTOCOL);
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();

            ChannelSftp c = (ChannelSftp) channel;

            if(transferType.equalsIgnoreCase(SEND)){

                for(String table:tableName) {
                    //String getTablePath = config.getSourceGetFilePath() + "databases\\" + database + "\\" + table + "\\" + table + ".txt";
                    String getTablePath = config.getSourceGetFilePath() + "databases/" + database + "/" + table + "/" + table + ".txt";
                    String putTablePath = config.getDestinationDatabasePath() + database + "/" + table + "/" + table + ".txt";
                    c.put(getTablePath, putTablePath);

                    //String getMetadataPath = config.getSourceGetFilePath() + "databases\\" + database + "\\" + table + "\\" + table + "_metadata.txt";
                    String getMetadataPath = config.getSourceGetFilePath() + "databases/" + database + "/" + table + "/" + table + "_metadata.txt";
                    String putgetMetadataPathPath = config.getDestinationDatabasePath() + database + "/" + table + "/" + table + "_metadata.txt";
                    c.put(getMetadataPath, putgetMetadataPathPath);
                }
            }else if(transferType.equalsIgnoreCase(RECEIVE)){
                for(String table:tableName){

                    directoryBuilder(config.getSourceGetFilePath(),database,table);

                    String getTablePath=config.getDestinationDatabasePath()+database+"/"+table+"/"+table+".txt";
                    //String putTablePath=config.getSourceGetFilePath()+"databases\\"+database+"\\"+table+"\\"+table+".txt";
                    String putTablePath=config.getSourceGetFilePath()+"databases/"+database+"/"+table+"/"+table+".txt";
                    //String putPath=config.getSourceGetFilePath()+"databases/"+database+"/"+table;

                    //String putPath=config.getSourceGetFilePath()+table+".txt";
                    c.get(getTablePath,putTablePath);
                    String getMetadataPath=config.getDestinationDatabasePath()+database+"/"+table+"/"+table+"_metadata.txt";
                    //String putgetMetadataPathPath=config.getSourceGetFilePath()+"databases\\"+database+"\\"+table+"\\"+table+"_metadata.txt";
                    String putgetMetadataPathPath=config.getSourceGetFilePath()+"databases/"+database+"/"+table+"/"+table+"_metadata.txt";
                    c.get(getMetadataPath,putgetMetadataPathPath);
                }
            }

            c.exit();

        } catch (JSchException | SftpException e) {

            e.printStackTrace();

        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void directorySend(String database, String tableName){
        Session session = null;
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(config.getPrivateKeyFile());

            session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel(SIMPLE_FILE_TRANSFER_PROTOCOL);
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();

            ChannelSftp c = (ChannelSftp) channel;

            String getTablePath = config.getSourceGetFilePath() + "databases/" + database + "/" + tableName + "/" + tableName + ".txt";
//            String getTablePath = config.getSourceGetFilePath() + "databases\\" + database + "\\" + tableName + "\\" + tableName + ".txt";
            String putTablePath = config.getDestinationDatabasePath() + database + "/" + tableName + "/" + tableName +".txt";
            c.put(getTablePath, putTablePath);

//            String getMetadataPath = config.getSourceGetFilePath() + "databases\\" + database + "\\" + tableName + "\\" + tableName + "_metadata.txt";
            String getMetadataPath = config.getSourceGetFilePath() + "databases/" + database + "/" + tableName + "/" + tableName + "_metadata.txt";
            String putgetMetadataPathPath = config.getDestinationDatabasePath() + database + "/" + tableName + "/" + tableName + "_metadata.txt";
            c.put(getMetadataPath, putgetMetadataPathPath);

            c.exit();

        } catch (JSchException | SftpException e) {

            e.printStackTrace();

        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void directoryBuilder(String path,String database, String table){
        String directoryBuilder = config.getSourceGetFilePath()+"databases";
        File file = new File(directoryBuilder);
        boolean bool = file.mkdir();
        directoryBuilder = config.getSourceGetFilePath()+"databases/"+database;
        //directoryBuilder = config.getSourceGetFilePath()+"databases\\"+database;
        file = new File(directoryBuilder);
        bool = file.mkdir();
        directoryBuilder = config.getSourceGetFilePath()+"databases/"+database+"/"+table;
        //directoryBuilder = config.getSourceGetFilePath()+"databases\\"+database+"\\"+table;
        file = new File(directoryBuilder);
        bool = file.mkdir();
    }
}
