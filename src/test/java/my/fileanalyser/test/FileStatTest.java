package my.fileanalyser.test;

import my.fileanalyser.FileAnalyser;
import my.fileanalyser.FileStat;
import my.fileanalyser.LineStat;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileStatTest {

    @Test
    public void fileStatisticTest() {
        String sourceLine1 = " a bb   ccc    dddd";
        String sourceLine2 = " cccc  bbb  ddddd aa ";
        String sourceLine3 = " bbbb aaa   ccccc dddddd";
        String sourceLine4 = " aaaa bbbbb   cccccc ddddddd";
        String sourceLine5 = "";

        int expectedAvLineLen = (sourceLine1.length() + sourceLine2.length() + sourceLine3.length() + sourceLine4.length())  / 4;

        FileAnalyser fa = new FileAnalyser(Paths.get("D:\\TEST\\111.txt"));
        fa.analyseLine(sourceLine1);
        fa.analyseLine(sourceLine2);
        fa.analyseLine(sourceLine3);
        fa.analyseLine(sourceLine4);
        fa.analyseLine(sourceLine5);
        fa.analyseFileStat();

        assertEquals(4, fa.getLineStatList().size());

        FileStat fs = fa.getFileStat();

        assertEquals("ddddddd", fs.getLongestWord());
        assertEquals("a", fs.getShortestWord());
        assertEquals(3, fs.getAverageWordLength());
        assertEquals(expectedAvLineLen, fs.getAverageLineLength());
    }
}


