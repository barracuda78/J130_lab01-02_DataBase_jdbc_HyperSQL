package ruzaevj130lab01_02;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/*
Утилитарный класс для загрузки драйвера, получения параметров соединения из файла .properties и создания соединения.
Используется в конструкторе класса DbServer;
*/
public class Connector {
    private static Connection connection;
    
    
    //метод для получения параметров подключения:
    protected static Properties getProperties(){
        Properties properties = new Properties();
        try(InputStream inputStream = ClassLoader.getSystemResourceAsStream("resources/database.properties")){
            properties.load(inputStream);
            System.out.println("Файл properties прочитан");
        }catch(IOException ex){
            System.out.println("Ошибка чтения файла properties");
        }
        
        System.out.println("Параметры подключения:");

        
        return properties;
    }
    
    //метод для получения соединения:
    public static Connection getConnection(){
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка проверки активности соединения");
        }
        
        //загрузим драйвер. Если загрузился драйвер - будем возвращщать соединение:
        if(loadDriver()){
            Properties properties = getProperties();
            String connectionString = properties.getProperty("url") + ";user=" + 
                                      properties.getProperty("user") + ";password=" + 
                                      properties.getProperty("password"); 
            System.out.println("Строка подключения: " + connectionString);
            
            try {
                connection = DriverManager.getConnection(connectionString);
                System.out.println("Соединение создано");
            } catch (SQLException ex) {
                System.out.println("Ошибка получения соединения");
            }
        }
        

        return connection;
    }
    
    //метод для загрузки драйвера:
    private static boolean loadDriver(){
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            System.out.println("Драйвер загружен");
            return true;
        } catch (ClassNotFoundException ex) {
            System.out.println("Ошибка загрузки драйвера");
            return false;
        }
    }
    
}
