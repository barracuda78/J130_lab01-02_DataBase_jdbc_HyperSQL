package ruzaevj130lab01_02;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

/*
Только для тестирования и отладки:
Класс для заполнения базы данных и для проверки. Использую для отладки.
*/
public class DataBase {
    public static final String URL = "jdbc:hsqldb:file:../projdatabase/test3"; 
    private Connection con;
    private Statement st;
    private static final String QUERY00 = "CREATE TABLE Authors (auth_id INTEGER PRIMARY KEY, auth_name VARCHAR(64) NOT NULL, auth_note VARCHAR(255));";
    private static final String QUERY01 = "INSERT INTO Authors (auth_id, auth_name) VALUES (1, 'Arnold Grey');";
    private static final String QUERY02 = "INSERT INTO Authors (auth_id, auth_name, auth_note) VALUES (2, 'Tom Hawkins', 'new author');";
    private static final String QUERY03 = "INSERT INTO Authors (auth_id, auth_name) VALUES (3, 'Jim Beam');";
    private static final String QUERY04 = "INSERT INTO Authors (auth_id, auth_name) VALUES (4, 'Test Author');";
    private static final String QUERY0TEST = "SELECT * FROM Authors;";
    
    private static final String QUERY10 = "CREATE TABLE Documents ("
            + "doc_id INTEGER PRIMARY KEY,"
            + "doc_name VARCHAR(64) NOT NULL,"
            + "doc_text VARCHAR(1024),"
            + "doc_date DATE NOT NULL,"
            + "doc_author_id INTEGER NOT NULL,"
            + "CONSTRAINT doc_author_fk FOREIGN KEY (doc_author_id) REFERENCES Authors (auth_id));";
    private static final String QUERY11 = "INSERT INTO Documents (doc_id, doc_name, doc_text, doc_date, doc_author_id) VALUES (1, 'Project plan', 'First content', '2008-08-22', 1);";
    private static final String QUERY12 = "INSERT INTO Documents (doc_id, doc_name, doc_text, doc_date, doc_author_id) VALUES (2, 'First report', 'Second content', '2009-09-29', 2);";
    private static final String QUERY13 = "INSERT INTO Documents (doc_id, doc_name, doc_text, doc_date, doc_author_id) VALUES (3, 'Test result', 'Third content', '2010-10-30', 2);";
    private static final String QUERY14 = "INSERT INTO Documents (doc_id, doc_name, doc_text, doc_date, doc_author_id) VALUES (4, 'Second report', 'Report content', '2011-11-01', 3);";
    private static final String QUERY1TEST = "SELECT * FROM Documents;";
    
    private static final String QUERY30 = "UPDATE Authors SET auth_note = 'No data' WHERE auth_note IS NULL;";
    
    
    
    //1
    private void checkDriver(){
        Enumeration<Driver> e = DriverManager.getDrivers();
        while(e.hasMoreElements()){
            Driver d = e.nextElement();
            //System.out.println(d.getClass().getCanonicalName());
        }
        
        try {
            Driver d = DriverManager.getDriver(URL);
            System.out.println("1. Драйвер проверен: " + d.getClass().getCanonicalName());
        } catch (SQLException ex) {
            //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error 1: " + ex.getMessage());
        }
    }
    
    //2
    private void setConnection (String user, String password){
        try {
            System.out.println("2. Connection is opened");
            con = DriverManager.getConnection(URL, user, password);
            if(con == null){
                System.out.println("con = null");
            }
        } catch (SQLException ex) {
            //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error 2: " + ex.getMessage());
        }
    }
    
    //3
    private void setStatement(){
        try {
            st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println("3. Statement object created");
        } catch (SQLException ex) {
            //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error 3: " + ex.getMessage());
        }
    }
    
    //4
    private void executeQuery(String[] query){
        try {
            for(String q : query){
                boolean b = st.execute(q);
                if(b){
                    ResultSet rs = st.getResultSet();
                    System.out.println("Получили ResultSet (Выборку)");
                    printResult(rs);
                }else{
                    int n = st.getUpdateCount();
                    System.out.println("Обновлено строк: " + n);
                }
            }

        } catch (SQLException ex) {
            //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error 4: " + ex.getMessage());
        }
    }
    
    //5
    private void printResult(ResultSet rs){
        try{
            if(rs != null){
                while (rs.next()){
                    //для таблицы Authors:
                    System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString("auth_name") + ". note = " + rs.getString("auth_note"));
                    //для таблицы Documents:
                    //System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString("doc_name") + ". text = " + rs.getString("doc_text") + ". date = " + rs.getDate("doc_date") + ". doc_author_id = " + rs.getInt("doc_author_id"));
                }
            }
        }catch(SQLException ex){
            System.out.println("Error 5: " + ex.getMessage());
        }
    }
    
    //6
    private void closeConnection(){
        try {
            if(con != null && !con.isClosed()){
                con.close();
                System.out.println("6. Connection closed");
            }
        } catch (SQLException ex) {
                //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error 6: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
           DataBase demo = new DataBase();
           demo.checkDriver();
           demo.setConnection("root", "root");
           demo.setStatement();
           demo.executeQuery(new String[] {QUERY0TEST});
           demo.closeConnection();
        //Driver d = DriverManager.getDriver("");
    }
    
}
