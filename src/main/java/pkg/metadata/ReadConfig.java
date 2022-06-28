package pkg.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadConfig {

    public Config read(){
        Config config = new Config();
        try (InputStream input = ReadConfig.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return config;
            }

            prop.load(input);
            config.setHost(prop.getProperty("vm.host"));
            config.setPort(Integer.parseInt(prop.getProperty("vm.port")));
            config.setUser(prop.getProperty("vm.user"));
            config.setPrivateKeyFile(prop.getProperty("vm.private.key"));
            config.setDestinationGetFilePath(prop.getProperty("vm.destination.get.filepath"));
            config.setDestinationPutFilePath(prop.getProperty("vm.destination.put.filepath"));
            config.setSourceGetFilePath(prop.getProperty("vm.source.get.filepath"));
            config.setSourcePutFilePath(prop.getProperty("vm.source.put.filepath"));
            config.setVmName(prop.getProperty("vm.name"));
            config.setDestinationDatabasePath(prop.getProperty("vm.destination.databases"));
            config.setVmResources(prop.getProperty("vm.resources"));
            config.setTempVmResources(prop.getProperty("vm.resources.temp"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }
}
