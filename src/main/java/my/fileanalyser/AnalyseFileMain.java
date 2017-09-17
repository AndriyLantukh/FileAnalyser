package my.fileanalyser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 15.09.2017.
 */
public class AnalyseFileMain {


    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please specify file to analyse.");
            return;
        }

        String filePath = args[0];
 //       String filePath = "D:\\TEST";

        Path path = Paths.get(filePath);
        List<Path> fileList = new ArrayList<>();
        if (!path.toFile().exists()) {
            System.out.println("File is not exist! " + filePath);
        }
        if (path.toFile().isDirectory()) {
            try {
               Files.walk(Paths.get(filePath)).filter(Files::isRegularFile).forEach(thePath -> fileList.add(thePath));
            } catch (IOException e) {
                System.out.println("Can't get files from directory. " + filePath);
            }
        } else {
            fileList.add(path);
        }

        String dbUrl = "jdbc:postgresql://localhost:5432/filesData";
        String login = "myuser";
        String password = "";

        DbConnector dbConnector = new DbConnector(dbUrl, login, password);

        fileList.forEach(thePath -> {
            FileAnalyser fileAnalyser = new FileAnalyser(thePath);
            fileAnalyser.analyseFile();
            dbConnector.saveFileCommonStat(fileAnalyser);
        });
    }
}
