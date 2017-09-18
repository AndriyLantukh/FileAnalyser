package my.fileanalyser.test;

import my.fileanalyser.FileAnalyser;
import my.fileanalyser.model.LineStat;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LineStatTest {

    @Test
    public void lineStatisticTest() {
        String sourceLine = " a vvv   bbbbbb    1234567890";

        FileAnalyser fa = new FileAnalyser(Paths.get("D:\\TEST\\111.txt"));
        fa.analyseLine(sourceLine);

        LineStat ls = fa.getLineStatList().get(0);

        assertEquals("1234567890", ls.getLongestWord());
        assertEquals("a", ls.getShortestWord());
        assertEquals(5, ls.getAverageWordLength());
    }

    @Test
    public void skipLineTest() {
        String sourceLine = "                                 ";

        FileAnalyser fa = new FileAnalyser(Paths.get("D:\\TEST\\111.txt"));
        fa.analyseLine(sourceLine);

        assertTrue(fa.getLineStatList().isEmpty());
   }

    @Test
    public void oneCharInLineTest() {
        String sourceLine = " ,           ";

        FileAnalyser fa = new FileAnalyser(Paths.get("D:\\TEST\\111.txt"));
        fa.analyseLine(sourceLine);

        LineStat ls = fa.getLineStatList().get(0);

        assertEquals(",", ls.getLongestWord());
        assertEquals(",", ls.getShortestWord());
        assertEquals(1, ls.getAverageWordLength());
    }



}


