package my.fileanalyser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class LineStat {

    private LineStat() {
    }

    private Integer id;
    private Integer fileId;
    private String longestWord;
    private String shortestWord;
    private int lineLength;
    private int averageWordLength;
}


