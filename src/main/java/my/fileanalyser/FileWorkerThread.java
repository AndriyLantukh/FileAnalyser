package my.fileanalyser;


import java.nio.file.Path;

public class FileWorkerThread implements Runnable{

    DbConnector connector;
    Path filePath;

    FileWorkerThread(DbConnector connector, Path filePath) {
        this.connector = connector;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        FileAnalyser fileAnalyser = new FileAnalyser(filePath);
        fileAnalyser.analyseFile();
        connector.saveData(fileAnalyser);
    }


}


