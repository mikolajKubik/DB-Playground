package edu.kdmk;

public class ConnectionStringProvider {
    private static final String MACOS_CONNECTION_STRING = "mongodb://root:root@111.222.32.4:27017,111.222.32.3:27018,111.222.32.2:27019/?replicaSet=rs0&authSource=admin";
    private static final String WINDOWS_CONNECTION_STRING = "mongodb://root:root@mongo1:27017,mongo2:27018,mongo3:27019/?replicaSet=rs0&authSource=admin";
    private static final String LINUX_CONNECTION_STRING = "mongodb://root:root@mongo1:27017,mongo2:27018,mongo3:27019/?replicaSet=rs0&authSource=admin";

    public static String getConnectionString() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            return MACOS_CONNECTION_STRING;
        } else if (osName.contains("win")) {
            return WINDOWS_CONNECTION_STRING;
        } else if (osName.contains("nix") || osName.contains("nux")) {
            return LINUX_CONNECTION_STRING;
        } else {
            throw new IllegalStateException("Unsupported OS: " + osName);
        }
    }
}
