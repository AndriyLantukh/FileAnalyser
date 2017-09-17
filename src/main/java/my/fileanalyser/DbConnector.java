package my.fileanalyser;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbConnector {

    private DbConnector() {
    }

    public DbConnector(String dbUrl, String login, String password) {
        this.dbUrl = dbUrl;
        this.login = login;
        this.password = password;
    }

    private String dbUrl;
    private String login;
    private String password;

    Connection conn = null;

    public FileStat saveFileStat(FileStat fileStat) {
        Connection conn = getConnection();
        if (conn != null) {
            saveFileStat(conn, fileStat);
        }
        return fileStat;
    }

    private FileStat saveFileStat(Connection conn, FileStat fileStat) {
        if (conn == null || fileStat == null) {
            return fileStat;
        }
        try {
            Statement st = conn.createStatement();

            String query = "INSERT INTO FILESTAT (FILENAME, LONGEST, SHORTEST, AVLINELEN, AVWORDLEN)" +
                    "VALUES ('" + fileStat.getFileName() + "','"
                    + fileStat.getLongestWord() + "','"
                    + fileStat.getShortestWord() + "',"
                    + fileStat.getAverageLineLength() + ","
                    + fileStat.getAverageWordLength() + ") RETURNING id";

            ResultSet rs = st.executeQuery(query);

            Integer id = null;
            while (rs.next()) {
                id = rs.getInt(1);
            }
            fileStat.setId(id);

            rs.close();
            st.close();
        } catch (SQLException se) {
            System.err.println("Threw a SQLException creating fileStat.");
            System.err.println(se.getMessage());
        }
        return fileStat;
    }

    public void saveLinesStat(List linesStatList) {
        Connection conn = getConnection();
        if (conn != null) {
            saveLinesStat(conn, linesStatList);
        }
    }

    private void saveLinesStat(Connection conn, List<LineStat> lineStatList) {
        if (conn == null || lineStatList == null || lineStatList.size() == 0) {
            return;
        }

        try {
            Statement st = conn.createStatement();

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO LINESTAT (FILEID, LONGEST, SHORTEST, LINELEN, AVERAGELEN) VALUES");

            for (LineStat lineStat : lineStatList) {
                String oneRow =  "(" + lineStat.getFileId() + ", '"
                    + lineStat.getLongestWord() + "','"
                    + lineStat.getShortestWord() + "',"
                    + lineStat.getLineLength() + ","
                    + lineStat.getAverageWordLength() + "),";
                sb.append(oneRow);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" RETURNING id");

            ResultSet rs = st.executeQuery(sb.toString());

            int index = 0;
            while (rs.next()) {
                lineStatList.get(index).setId(rs.getInt(1));
               index++;
            }

            rs.close();
            st.close();
        } catch (SQLException se) {
            System.err.println("Threw a SQLException creating lineStat.");
            System.err.println(se.getMessage());
        }
    }

    public List<FileStat> getFileStatList() {
        Connection conn = getConnection();
        if (conn != null) {
            return getFileStatList(conn);
        }
        return null;
    }

    public List<FileStat> getFileStatList(Connection conn) {
        if (conn == null) {
            return null;
        }
        List<FileStat> fileStatList = new ArrayList();
        try {
            Statement st = conn.createStatement();

            String query = "SELECT * FROM FILESTAT;";

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                fileStatList.add(new FileStat(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6)));
            }

            rs.close();
            st.close();
        } catch (SQLException se) {
            System.err.println("Threw a SQLException creating fileStat.");
            System.err.println(se.getMessage());
        }
        return fileStatList;
    }

    public List<LineStat> getLineStatList(int fileId) {
        Connection conn = getConnection();
        if (conn != null) {
            return getLineStatList(conn, fileId);
        }
        return null;
    }

    public List<LineStat> getLineStatList(Connection conn, int fileId) {
        if (conn == null) {
            return null;
        }
        List<LineStat> lineStatList = new ArrayList();
        try {
            Statement st = conn.createStatement();

            String query = "SELECT * FROM LINESTAT " +
                           "WHERE FILEID ='" + fileId + "' " +
                           "ORDER BY ID asc;";

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                lineStatList.add(new LineStat(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6)));
            }

            rs.close();
            st.close();
        } catch (SQLException se) {
            System.err.println("Threw a SQLException creating fileStat.");
            System.err.println(se.getMessage());
        }
        return lineStatList;
    }

    private ResultSet doQuery(String query) {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }
        ResultSet rs = null;
        try {
            Statement st = conn.createStatement();
            rs = st.executeQuery(query);
            rs.close();
            st.close();
        } catch (SQLException se) {
            System.err.println("Threw a SQLException creating fileStat.");
            System.err.println(se.getMessage());
        }
        return rs;
   }

    private Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(dbUrl, login, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(2);
            }

        }
        if (conn == null) {
            System.err.println("Can't create connection to " + dbUrl + " " + login + " " + password);
        }
        return conn;
    }

    public void saveFileCommonStat(FileAnalyser fileAnalyser) {
       FileStat fs = saveFileStat(fileAnalyser.getFileStat());
       if (fs == null || fs.getId() == null) {
           return;
       }
       fileAnalyser.getLineStatList().forEach(lineStat -> lineStat.setFileId(fs.getId()));
       saveLinesStat(fileAnalyser.getLineStatList());

        System.out.println("Data saved " + fs.toString());
    }

}


