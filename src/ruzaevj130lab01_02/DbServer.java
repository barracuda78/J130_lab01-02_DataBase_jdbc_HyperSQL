
package ruzaevj130lab01_02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DbServer implements  IDbService{
    public static final String URL = "jdbc:hsqldb:file:../projdatabase/test3"; 
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static String QUERY;
    
    private Connection connection;
    private PreparedStatement pst;
    

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
        //throw new DocumentException();
        //если поле имя автора не инициализировано и поле id автора не инициализировано нулем по-умолчанию,
        if(author.getAuthor() != null && author.getAuthor_id() != 0){
            try {
                //значит это новый автор, записываем его в базу:
                setConnection(USER, PASSWORD);
                QUERY = "INSERT INTO Authors (auth_id, auth_name, auth_note) VALUES (?, ?, ?);";
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                pst.setInt(1, author.getAuthor_id());
                pst.setString(2, author.getAuthor());
                pst.setString(3, author.getNotes());
                
                b = true;
            } catch (SQLException ex) {
                System.out.println("Error 3-0: " + ex.getMessage());
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
                System.out.println("Error 3-1: " + ex.getMessage());
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
                System.out.println("Error 3-2: " + ex.getMessage());
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
                System.out.println("Error 3-2: " + ex.getMessage());
                throw new DocumentException("Ошибка выполнения выражения execute prepared statement");
            }
        return b;    
    }

    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {
        throw new DocumentException();
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
            System.out.println("Error 2: " + ex.getMessage());
        }
    }
    
    
}
