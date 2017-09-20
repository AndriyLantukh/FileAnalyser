package my.fileanalyser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Андрей on 15.09.2017.
 */
public class AnalyseFileMain {


    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        if (args.length == 0) {
            System.out.println("Please specify file to analyse.");
            return;
        }

        String filePath = args[0];
//        String filePath = "D:\\TEST";


        ConcurrentLinkedQueue<Path> fileQueue = getFiles(filePath);
        if (fileQueue.isEmpty()) {
            return;
        }

        // DbConnector dbConnector = getDbConnector();
        DbConnector dbConnector = getDbConnectorWithPool();
        if (dbConnector == null) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);
        while (!fileQueue.isEmpty()) {
            Runnable worker = new FileWorkerThread(dbConnector, fileQueue.poll());
            executor.execute(worker);
        }
        executor.shutdown();

//        fileQueue.forEach(thePath -> {
//            FileAnalyser fileAnalyser = new FileAnalyser(thePath);
//            fileAnalyser.analyseFile();
//            dbConnector.saveData(fileAnalyser);
//        });

        long endTime = System.currentTimeMillis();
        System.out.println("Done in " + (endTime - startTime) + " msec");
    }


    private static ConcurrentLinkedQueue<Path> getFiles(String stringPath) {
        Path path = Paths.get(stringPath);
        //   List<Path> fileList = new ArrayList<>();
        ConcurrentLinkedQueue<Path> lq = new ConcurrentLinkedQueue();
        if (!path.toFile().exists()) {
            System.err.println("File is not exist! " + stringPath);
        }
        if (path.toFile().isDirectory()) {
            try {
                Files.walk(Paths.get(stringPath)).filter(Files::isRegularFile).forEach(thePath -> lq.add(thePath));
            } catch (IOException e) {
                System.err.println("Can't get files from directory. " + stringPath);
            }
        } else {
            lq.add(path);
        }
        return lq;
    }

    private static DbConnector getDbConnector() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        InputStream resourceStream = loader.getResourceAsStream("db.properties");
        try {
            props.load(resourceStream);
        } catch (IOException e) {
            System.err.println("Can't get db.properties.");
            return null;
        }

        String driverName = props.getProperty("jdbc.driver");
        String dbUrl = props.getProperty("jdbc.url");
        String login = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        System.out.println("Connect to DB " + driverName + " " + dbUrl + " " + login + " " + password);

        return new DbConnector(driverName, dbUrl, login, password);
    }

    private static DbConnector getDbConnectorWithPool() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        InputStream resourceStream = loader.getResourceAsStream("db.properties");
        try {
            props.load(resourceStream);
        } catch (IOException e) {
            System.err.println("Can't get db.properties.");
            return null;
        }

        String serverName = props.getProperty("jdbc.server.name");
        String dbname = props.getProperty("jdbc.db.name");
        String login = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        String dataSourceName = props.getProperty("jdbc.data.source.name");
        int maxConnections = Integer.parseInt(props.getProperty("jdbc.max.connections"));

        System.out.println("Connect to DB " + serverName + " " + dbname + " " + login + " " + password + " " + dataSourceName + " " + maxConnections);

        return new DbConnector(serverName, dbname, login, password, dataSourceName, maxConnections);
    }
}
