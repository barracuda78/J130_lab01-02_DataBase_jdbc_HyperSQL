
package ruzaevj130lab01_02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DbServer implements  IDbService{
//    public static final String URL = "jdbc:hsqldb:file:../projdatabase/test3";  
//    private static final String USER = "root";
//    private static final String PASSWORD = "root";
    public static String URL;  
    private static String USER;
    private static String PASSWORD;
    private static String QUERY;
    
    private Connection connection;
    private PreparedStatement pst;
    
    //инициализация полей в конструкторе значениями из файла database.properties: лежит в пакете resources
    public DbServer(){
        connection = Connector.getConnection();
        Properties properties = Connector.getProperties();
        
        
        for(Map.Entry<Object, Object> pair : properties.entrySet()){
            String key = (String)pair.getKey();
            String value = (String)pair.getValue();
            
            if(key.equals("url")){
                URL = value;
            }else if(key.equals("user")){
                USER = value;
            }else if(key.equals("password")){
                PASSWORD = value;
            }
            //проверка:
            System.out.println(key + " : " + value);
        }
    }
    

 /**
 * Метод добавляет нового автора к базе данных, если все обязательные поля 
 * объекта author определены. В противном случае, метод пытается обновить
 * уже существующие записи, используя заполненные поля класса для поиска
 * подходящих записей. Например, если в объекте author указан id автора,
 * поле имени автора пусто, а поле примечаний содержит текст, то у записи с
 * заданным идентификатором обновляется поле примечаний.
 *
 * @param author именные данные автора.
 * @return возвращает значение true, если создана новая запись, и значение
 * false, если обновлена существующая запись.
 * @throws DocumentException выбрасывается в случае, если поля объекта
 * author заполнены неправильно и не удаётся создать новую запись или
 * обновить уже существующую. Данное исключение также выбрасывается в случае
 * общей ошибки доступа к базе данных
 */
    @Override
    public boolean addAuthor(Author author) throws DocumentException {
        boolean b = false; // - возвращаю из метода.
        //если поле имя автора не инициализировано и поле id автора не инициализировано нулем по-умолчанию,
        if(author.getAuthor() != null && author.getAuthor_id() != 0){
            try {
                //значит это новый автор, записываем его в базу:
                setConnection(USER, PASSWORD);
                QUERY = "INSERT INTO Authors (auth_id, auth_name, auth_note) VALUES (?, ?, ?);";
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                /////////////ПРОВЕРКА
                DatabaseMetaData m = connection.getMetaData();
                boolean bool = m.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                System.out.println("supportsResultSetConcurrency = " + bool);
            
                /////////////
                
                
                pst.setInt(1, author.getAuthor_id());
                pst.setString(2, author.getAuthor());
                pst.setString(3, author.getNotes());
                
                b = true;
            } catch (SQLException ex) {
                System.out.println("Error 1-0: " + ex.getMessage());
                throw new DocumentException("Ошибка записи автора в базу");
            }
            //иначе если поле имя автора не инициализировано, А поле id автора - заполнено,
        }else if(author.getAuthor() == null && author.getAuthor_id() != 0){
            try {
                //тогда ищем автора по id и обновляем ему поля.
                setConnection(USER, PASSWORD);
                //даже если auth_note == null:
                QUERY = "UPDATE Authors SET auth_note = ? WHERE auth_id = ?;";
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                pst.setString(1, author.getNotes());
                pst.setInt(2, author.getAuthor_id());
                
            } catch (SQLException ex) {
                System.out.println("Error 1-1: " + ex.getMessage());
                throw new DocumentException("Ошибка обновления автора по id");
            }
            //иначе если поле имя автора  инициализировано, А поле id автора НЕ заполнено,
        }else if(author.getAuthor() != null && author.getAuthor_id() == 0){
            try {
                //тогда ищем автора по ИМЕНИ!!! и обновляем ему поля.
                setConnection(USER, PASSWORD);
                //даже если auth_note == null:
                QUERY = "UPDATE Authors SET auth_note = ? WHERE auth_name = ?;";
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                pst.setString(1, author.getNotes());
                pst.setString(2, author.getAuthor());
                
            } catch (SQLException ex) {
                System.out.println("Error 1-2: " + ex.getMessage());
                throw new DocumentException("Ошибка обновления автора по имени");
            }
        }else{
            throw new DocumentException("Поля автора заполнены некорректно");
        }
            
        //теперь выполняем PreparedStatement и получаем кол-во обновленных элементов:
        try {
            pst.execute();
            int n = pst.getUpdateCount();
            System.out.println("Количество обновленных элементов = " + n);
        } catch (SQLException ex) {
            System.out.println("Error 1-3: " + ex.getMessage());
            throw new DocumentException("Ошибка выполнения выражения execute prepared statement");
        }
        
        //теперь закрываем соединение:
        closeConnection();
            
        return b;    
    }

     /**
 * Метод добавляет новый документ к базе данных, если все обязательные поля
 * объектов doc и author определены. В противном случае, метод пытается
 * обновить уже существующие записи, используя заполненные поля объектов для
 * поиска подходящих записей.
 *
 * @param doc добавляемый или обновляемый документ.
 * @param author ссылка на автора документа.
 * @return возвращает значение true, если создан новый документ, и значение
 * false, если обновлена уже существующая запись.
 * @throws DocumentException выбрасывается в случае, если поля объектов doc
 * и author заполнены неправильно и не удаётся создать новую запись или
 * обновить уже существующую. Данное исключение также выбрасывается в случае
 * общей ошибки доступа к базе данных
 */
    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {
        boolean b = false; // - возвращаю из метода.
        //если все обязательные поля объектов doc и author определены - добавляю новый документ к базе данных:
        //обязательные поля документа: document_id, title, date, author_id;
        //обязательные поля автора: author_id, author;
        if(doc.getDocument_id() !=0 && doc.getTitle() != null && doc.getDate() != null && doc.getAuthor_id() != 0 && author.getAuthor() != null && author.getAuthor_id() != 0){
            try{
            setConnection(USER, PASSWORD);
            QUERY = "INSERT INTO Documents (doc_id, doc_name, doc_text, doc_date, doc_author_id) VALUES (?, ?, ?, ?, ?);";
            pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);            
            
            pst.setInt(1, doc.getDocument_id());
            pst.setString(2, doc.getTitle());
            pst.setString(3, doc.getText());
            //преобразую java.util.Date в java.sql.Date:
            Date date = doc.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            String s = sdf.format(date);
            java.sql.Date sqlDate = java.sql.Date.valueOf(s);
            pst.setDate(4, sqlDate);
            pst.setInt(5, doc.getAuthor_id());
            
            b = true;
            
            }catch(SQLException ex){
                System.out.println("Error 2-0: " + ex.getMessage());
                throw new DocumentException("Ошибка записи документа в базу");
            }
        }
        //В противном случае, метод пытается обновить уже существующие записи,
        //используя заполненные поля объектов для поиска подходящих записей.
        else if (doc.getDocument_id() !=0 && doc.getAuthor_id() != 0){
            //если не все обязательные поля заполнены,
            //но заполнен id документа и id автора: обновляю ВСЕ поля кроме id в обеих таблицах:
            try{
                setConnection(USER, PASSWORD);
                //ПРИМЕР множественного обновления полей : UPDATE goods SET title = "утюг", price = 300 WHERE num = 2
                //обновляю документы:
                QUERY = "UPDATE Documents SET doc_name = ?, doc_text = ?, doc_date = ? WHERE doc_id = ?;";
                
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pst.setString(1, doc.getTitle());
                pst.setString(2, doc.getText());
                //преобразую java.util.Date в java.sql.Date:
                Date date = doc.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
                String s = sdf.format(date);
                java.sql.Date sqlDate = java.sql.Date.valueOf(s);
                pst.setDate(3, sqlDate);
                pst.setInt(4, doc.getDocument_id());
                
                pst.execute();
                int n = pst.getUpdateCount();
                System.out.println("Количество обновленных элементов в таблице Documents: = " + n);
                
                //теперь обновляю автора (по id автора):
                QUERY = "UPDATE Authors SET auth_name = ?, auth_note = ? WHERE auth_id = ?;";
                //для обновления автора создаю новую переменную и новый PreparedStatement, тк старый уже отработал:
                PreparedStatement pst2 = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pst2.setString(1, author.getAuthor());
                pst2.setString(2, author.getNotes());
                pst2.setInt(3, author.getAuthor_id());
                
                pst2.execute();
                int m = pst2.getUpdateCount();
                System.out.println("Количество обновленных элементов в таблице Authors: = " + m);
                
                
                //теперь закрываю соединение:
                closeConnection();
                
                
            }catch(SQLException ex){
                System.out.println("Error 2-2: " + ex.getMessage());
                throw new DocumentException("Ошибка обновления документа/автора в базе");
            }
        }
        return b;
    }

    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {
       throw new DocumentException();
    }

    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {
        throw new DocumentException();
    }

    @Override
    public boolean deleteAuthor(Author author) throws DocumentException {
        throw new DocumentException();
    }

    @Override
    public boolean deleteAuthor(int id) throws DocumentException {
        throw new DocumentException();
    }
    
    //вспомогательные методы:
    //1. - загрузка драйвера - через систему.
    //2.
    private void setConnection (String user, String password){
        try {
            System.out.println("2. Соединение установлено.");
            connection = DriverManager.getConnection(URL, user, password);

        } catch (SQLException ex) {
            System.out.println("Error 2: Соединение не установлено. " + ex.getMessage());
        }
    }
    
    //просмотр списка авторов/документов и вывод их в консоль:
    protected void checkTable(String tableName){
        //устанавливаем соединение:
        setConnection(USER, PASSWORD);
        
        if(tableName.equals("Authors")){
            QUERY = "SELECT * FROM Authors;";
        }else if(tableName.equals("Documents"))
            QUERY = "SELECT * FROM Documents;";
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            boolean b = statement.execute(QUERY);
            if(b){
                    ResultSet rs = statement.getResultSet();
                    System.out.println("Получили ResultSet (Выборку)");
                    printResult(rs, tableName);
                }
            
        } catch (SQLException ex) {
            System.out.println("4: Statement не создано");
        }
        
        closeConnection();
    }
    
    //метод вывода полученного resultSet в консоль:
    private void printResult(ResultSet rs, String tableName){
        try{
            if(rs != null){
                while (rs.next()){
                    if(tableName.equals("Authors")){
                        //для таблицы Authors:
                        System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString("auth_name") + ". note = " + rs.getString("auth_note"));
                    }else if(tableName.equals("Documents")){
                        //для таблицы Documents:
                        System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString("doc_name") + ". text = " + rs.getString("doc_text") + ". date = " + rs.getDate("doc_date") + ". doc_author_id = " + rs.getInt("doc_author_id"));
                    }
                }
            }
        }catch(SQLException ex){
            System.out.println("5: ResultSet не напечатан. " + ex.getMessage());
        }
    } 
    
    //Усовершенствованный метод вывода полученного resultSet в консоль: используется ResultSetMetaData: просто потренироваться:
    private void printResult(ResultSet rs){
        try {
            if(rs != null){
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                
                List<String> columnNames = new ArrayList<>();
                for(int i = 0; i < columns; i++){
                    columnNames.add(md.getColumnName(i));
                }
                
                for(String s : columnNames){
                    System.out.println("Колонка: " + s);
                }
                
//                if(columns == 3){
//                    //значит это таблица Authors:
//                    //System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString("auth_name") + ". note = " + rs.getString("auth_note"));
//                    System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString(columnNames.get(1)) + ". note = " + rs.getString(columnNames.get(2)));
//                }else{
//                    //значит это таблица Documents:
//                    System.out.println("id = " + rs.getInt(1) + ". name = " + rs.getString("doc_name") + ". text = " + rs.getString("doc_text") + ". date = " + rs.getDate("doc_date") + ". doc_author_id = " + rs.getInt("doc_author_id"));
//                }
            }
            
        } catch (SQLException ex) {
            System.out.println("Не удалось получить объект ResultSetMetaData.");
        }
        
    }
    
    //метод для закрытия соединения:
    private void closeConnection(){
        try {
            if(connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("6. Соединение закрыто");
            }
        } catch (SQLException ex) {
                //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("6: Ошибка закрытия соединения. " + ex.getMessage());
        }
    }   
    
    
    /**
    * вспомогательный метод - для получения списка всех таблиц пользователя из БД.
    * Выводит список в консоль.
    * @return список строк - названий таблиц пользователя в БД.
    */
    public List<String> getAllUsersTables(){
        List<String> tables = new ArrayList<>();
        try {
            connection = Connector.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while(resultSet.next()){
                tables.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        tables.stream().forEach(System.out::println);
        closeConnection();
        return tables;
    }
}
