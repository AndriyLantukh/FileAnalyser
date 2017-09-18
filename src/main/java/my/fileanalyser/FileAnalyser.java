package my.fileanalyser;


import lombok.Getter;
import lombok.Setter;
import my.fileanalyser.model.FileStat;
import my.fileanalyser.model.LineStat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FileAnalyser {

    Path path;
    List<LineStat> lineStatList = new ArrayList();
    FileStat fileStat;

    public FileAnalyser(Path path) {
        this.path = path;
    }


    public void analyseFile() {
        List<String> stringList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile())));
            stringList = br.lines().collect(Collectors.toList());
         } catch (FileNotFoundException e) {
            System.err.println("Threw a FileNotFoundException creating analyseFile.");
            return;
        }

        stringList.forEach(line -> analyseLine(line));
        analyseFileStat();
    }

    public void analyseFile(List<String> strings) {
        strings.forEach(line -> analyseLine(line));
        analyseFileStat();
    }

    public void analyseLine(String line) {
        int lineLen = line.length();
        line = line.trim();
        if (line.isEmpty()) {
            System.out.println("Line contains only spaces or empty. Will be skipped.");
            return;
        }

        line = line.replaceAll(" +", " ");
        String[] words = line.split(" ");
        String longestFirst = Arrays.asList(words).stream()
                .sorted((e2, e1) -> e1.length() < e2.length() ? -1 : 1)
                .findFirst().get();
        String shortestFirst = Arrays.asList(words).stream()
                .sorted((e2, e1) -> e1.length() > e2.length() ? -1 : 1)
                .findFirst().get();

        int avWordLen = line.replace(" ", "").length() / words.length;

        lineStatList.add(new LineStat(null, null, longestFirst, shortestFirst, lineLen, avWordLen));

    }

    public void analyseFileStat() {
        if (lineStatList.isEmpty()) {
            return;
        }
        String longest = lineStatList.get(0).getLongestWord();
        String shortest = lineStatList.get(0).getShortestWord();
        int commonLineLen = 0;
        int commonWordLen = 0;
        for (LineStat lineStat : lineStatList) {
            if (lineStat.getLongestWord().length() > longest.length()) {
                longest = lineStat.getLongestWord();
            }
            if (lineStat.getShortestWord().length() < shortest.length()) {
                shortest = lineStat.getShortestWord();
            }
            commonLineLen = commonLineLen + lineStat.getLineLength();
            commonWordLen = commonWordLen + lineStat.getAverageWordLength();
        }

        int avLine = commonLineLen / lineStatList.size();
        int avWord = commonWordLen / lineStatList.size();

        fileStat = new FileStat(null, path.toFile().getName(), longest, shortest, avLine, avWord);
    }
}


