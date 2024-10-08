package dic_exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class dic_exec {
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    private Vector<tuple>[] slist;
    private Vector<tuple> book;
    private byte[] mp3_file;
    private List<Integer> count;
    private int book_count;
    private boolean add_kotoba_success_flag = true;
    private boolean add_book_success_flag = true;
    public Vector<tuple> sagasu(String kotoba) {
        slist = new Vector[5];
        for (int i = 0; i < 5; i++) {
            slist[i] = new Vector<tuple>();
        }
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String[] columns = {"kotoba", "imi", "bikou", "kanji"};
            for (int i = 1; i < 5; i++) {
                String sql = String.format("SELECT kotoba, imi, bikou, kanji, hinsi FROM dic WHERE %s LIKE ?;", columns[i - 1]);
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + kotoba + "%");
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    tuple t = new tuple();
                    t.kotoba = rs.getString("kotoba");
                    t.imi = rs.getString("imi");
                    t.bikou = rs.getString("bikou");
                    t.kanji = rs.getString("kanji");
                    t.hinsi = rs.getInt("hinsi");
                    slist[i].add(t);
                }
            }
            HashMap<String, tuple> mergedSet = new HashMap<String, tuple>();
            for (int i = 1; i < 5; i++) {
                for(int n = 0; n < slist[i].size(); n++){
                    mergedSet.put(slist[i].get(n).kotoba, slist[i].get(n));
                }
            }
            slist[0].addAll(mergedSet.values());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return slist[0];
    }
    public byte[] mp3_load(String kotoba) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = "SELECT hatsuon FROM dic WHERE kotoba=?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, kotoba);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                InputStream tmp = rs.getBinaryStream("hatsuon");
                if (tmp != null) {
                    ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int i;
                    while ((i = tmp.read(buffer)) != -1) {
                        mp3.write(buffer, 0, i);
                    }
                    mp3_file = mp3.toByteArray();
                }
            }
        }catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mp3_file;
    }
    public List<Integer> count_kotoba(){
        count = new ArrayList<>();
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = String.format("SELECT count(kotoba) FROM dic;");
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count.add(rs.getInt(1));
            }
            for (int i = 0; i < 6; i++) {
                sql = String.format("SELECT count(kotoba) FROM dic WHERE hinsi=%d;", i);
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    count.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
    public boolean add_kotoba(tuple new_kotoba, byte[] mp3) {
        boolean f = false;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = "INSERT INTO dic VALUES (?, ?, ?, ?, ?, ?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, new_kotoba.kotoba);
            pstmt.setInt(2, new_kotoba.hinsi);
            pstmt.setString(3, new_kotoba.imi);
            pstmt.setString(5, new_kotoba.bikou);
            pstmt.setString(6, new_kotoba.kanji);
            if (mp3 == null) {
                pstmt.setBytes(4, null);
                f = true;
            } else {
                pstmt.setBytes(4, mp3);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            add_kotoba_success_flag = false;
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (f) {
                String path = null;
                if (System.getProperty("os.name").startsWith("Windows")) {
                    path = "/gen_tts.exe";
                } else if (System.getProperty("os.name").startsWith("Linux")) {
                    path = "/gen_tts";
                }
                try {
                    File bin = File.createTempFile("gen_tts", null);
                    bin.deleteOnExit();
                    try (InputStream i = dic_exec.class.getResourceAsStream(path);
                         FileOutputStream w = new FileOutputStream(bin.getAbsolutePath())) {
                        if (i == null) {
                            throw new IOException();
                        }
                        byte[] buffer = new byte[1024];
                        int t;
                        while ((t = i.read(buffer)) != -1) {
                            w.write(buffer, 0, t);
                        }
                    }
                    if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
                        bin.setExecutable(true);
                    }
                    ProcessBuilder pb = new ProcessBuilder(bin.getAbsolutePath(), load_lang(), new_kotoba.kotoba);
                    Process p = pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return add_kotoba_success_flag;
    }
    public void modify_kotoba(tuple t, byte[] mp3, boolean f){
        try{
             conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
             if (mp3 == null){
                 if (f) {
                     String sql = "UPDATE dic SET imi = ?, bikou = ?, kanji = ?, hatsuon = ? WHERE kotoba=?;";
                     pstmt = conn.prepareStatement(sql);
                     pstmt.setString(1, t.imi);
                     pstmt.setString(2, t.bikou);
                     pstmt.setString(3, t.kanji);
                     pstmt.setBytes(4, null);
                     pstmt.setString(5, t.kotoba);
                 } else {
                     String sql = "UPDATE dic SET imi = ?, bikou = ?, kanji = ? WHERE kotoba=?;";
                     pstmt = conn.prepareStatement(sql);
                     pstmt.setString(1, t.imi);
                     pstmt.setString(2, t.bikou);
                     pstmt.setString(3, t.kanji);
                     pstmt.setString(4, t.kotoba);
                 }
             } else {
                 String sql = "UPDATE dic SET imi = ?, bikou = ?, kanji = ?, hatsuon = ? WHERE kotoba=?;";
                 pstmt = conn.prepareStatement(sql);
                 pstmt = conn.prepareStatement(sql);
                 pstmt.setString(1, t.imi);
                 pstmt.setString(2, t.bikou);
                 pstmt.setString(3, t.kanji);
                 pstmt.setBytes(4, mp3);
                 pstmt.setString(5, t.kotoba);
             }
             pstmt.executeUpdate();
             pstmt.close();
             String sql_ = "VACUUM;";
             pstmt = conn.prepareStatement(sql_);
             pstmt.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void del_kotoba(String kotoba) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = "DELETE FROM dic WHERE kotoba=?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, kotoba);
            pstmt.executeUpdate();
            pstmt.close();
            String sql_ = "VACUUM;";
            pstmt = conn.prepareStatement(sql_);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public Vector<tuple> load_book(){
        book = new Vector<tuple>();
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = "SELECT kotoba, imi, bikou, kanji, hinsi FROM bookmark;";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tuple t = new tuple();
                t.kotoba = rs.getString("kotoba");
                t.imi = rs.getString("imi");
                t.bikou = rs.getString("bikou");
                t.kanji = rs.getString("kanji");
                t.hinsi = rs.getInt("hinsi");
                book.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return book;
    }
    public int count_book() {
        book_count = book.size();
        return book_count;
    }
    public boolean add_book(tuple new_book) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = "INSERT INTO bookmark VALUES (?, ?, ?, ?, ?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, new_book.kotoba);
            pstmt.setInt(2, new_book.hinsi);
            pstmt.setString(3, new_book.imi);
            pstmt.setString(4, new_book.bikou);
            pstmt.setString(5, new_book.kanji);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            add_book_success_flag = false;
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return add_book_success_flag;
    }
    public boolean del_book(String kotoba) {
        boolean t = false;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql__ = String.format("SELECT count(kotoba) FROM bookmark WHERE kotoba=?;");
            pstmt = conn.prepareStatement(sql__);
            pstmt.setString(1, kotoba);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if(rs.getInt(1) > 0){
                    t = true;
                }
            }
            pstmt.close();
            String sql = "DELETE FROM bookmark WHERE kotoba=?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, kotoba);
            pstmt.executeUpdate();
            pstmt.close();
            String sql_ = "VACUUM;";
            pstmt = conn.prepareStatement(sql_);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }
    public void create_dic(String lang) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql1 = "CREATE TABLE dic (" +
                    "kotoba CHAR(20), " +
                    "hinsi INT, " +
                    "imi CHAR(80), " +
                    "hatsuon BLOB, " +
                    "bikou CHAR(50), " +
                    "kanji CHAR(20), " +
                    "PRIMARY KEY(kotoba)" +
                    ");";
            pstmt = conn.prepareStatement(sql1);
            pstmt.executeUpdate();
            String sql2 = "CREATE TABLE bookmark (" +
                    "kotoba CHAR(20), " +
                    "hinsi INT, " +
                    "imi CHAR(80), " +
                    "bikou CHAR(50), " +
                    "kanji CHAR(20), " +
                    "PRIMARY KEY(kotoba)" +
                    ");";
            pstmt = conn.prepareStatement(sql2);
            pstmt.executeUpdate();
            String sql3 = "CREATE TABLE flag (" +
                    "lang TEXT" +
                    ");";
            pstmt = conn.prepareStatement(sql3);
            pstmt.executeUpdate();
            String sql4 = "INSERT INTO flag VALUES (?);";
            pstmt = conn.prepareStatement(sql4);
            pstmt.setString(1, lang);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public String load_lang() {
        String lang = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:dic.db");
            String sql = "SELECT lang FROM flag;";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                lang = rs.getString("lang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lang;
    }
    public boolean koutyakugo() {
        if (load_lang().equals("ja") || load_lang().equals("ko")){
            return true;
        } else {
            return false;
        }
    }
}