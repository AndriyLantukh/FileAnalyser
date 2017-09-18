package my.fileanalyser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class FileStat {

    private FileStat() {
    }

    private Integer id;
    private String fileName;
    private String longestWord;
    private String shortestWord;
    private int averageLineLength;
    private int averageWordLength;
}


