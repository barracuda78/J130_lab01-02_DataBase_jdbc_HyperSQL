
package ruzaevj130lab01_02;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;



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
        //если поле имя автора не инициализировано (и не пустая строка) и поле id автора не инициализировано нулем по-умолчанию,
        if(author.getAuthor() != null && author.getAuthor_id() != 0 && !"".equals(author.getAuthor())){
            try {
                //значит это новый автор, записываем его в базу:
                //setConnection(USER, PASSWORD);
                connection = Connector.getConnection();
                QUERY = "INSERT INTO Authors (auth_id, auth_name, auth_note) VALUES (?, ?, ?)";
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                /////////////ПРОВЕРКА работоспособности ResultSetConcurrency в HSQL//////////////////
//                DatabaseMetaData m = connection.getMetaData();
//                boolean bool = m.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//                System.out.println("supportsResultSetConcurrency = " + bool);
//            
                //////////////КОНЕЦ ПРОВЕРКИ//////////////////////////////////////////////////////////
                
                
                pst.setInt(1, author.getAuthor_id());
                pst.setString(2, author.getAuthor());
                pst.setString(3, author.getNotes());
                
                b = true;
            } catch (SQLException ex) {
                System.out.println("Error 1-0: " + ex.getMessage());
                throw new DocumentException("Ошибка записи автора в базу");
            }
            //иначе если поле имя автора не инициализировано, А поле id автора - заполнено,
        }else if((author.getAuthor() == null || "".equals(author.getAuthor())) && author.getAuthor_id() != 0){
            try {
                //тогда ищем автора по id и обновляем ему поля.
                //setConnection(USER, PASSWORD);
                connection = Connector.getConnection();
                //даже если auth_note == null:
                QUERY = "UPDATE Authors SET auth_note = ? WHERE auth_id = ?";
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                pst.setString(1, author.getNotes());
                pst.setInt(2, author.getAuthor_id());
                
            } catch (SQLException ex) {
                System.out.println("Error 1-1: " + ex.getMessage());
                throw new DocumentException("Ошибка обновления автора по id");
            }
            //иначе если поле имя автора  инициализировано, А поле id автора НЕ заполнено,
        }else if((author.getAuthor() != null && !"".equals(author.getAuthor())) && author.getAuthor_id() == 0){
            try {
                //тогда ищем автора по ИМЕНИ!!! и обновляем ему поля.
                //setConnection(USER, PASSWORD);
                connection = Connector.getConnection();
                //даже если auth_note == null:
                QUERY = "UPDATE Authors SET auth_note = ? WHERE auth_name = ?";
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
        if(doc.getDocument_id() !=0 && (doc.getTitle() != null && !"".equals(doc.getTitle())) && doc.getDate() != null && doc.getAuthor_id() != 0 && (author.getAuthor() != null && !"".equals(author.getAuthor())) && author.getAuthor_id() != 0){
            try{
            //1. если такого автора нет в таблице Authors - сначала добавляю Автора в таблицу Authors.
            if(!isElementInTable(author, "Authors")){
                addAuthor(author);
            }
            //setConnection(USER, PASSWORD);
            connection = Connector.getConnection();
            QUERY = "INSERT INTO Documents (doc_id, doc_name, doc_text, doc_date, doc_author_id) VALUES (?, ?, ?, ?, ?)";
            pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);            
            
            pst.setInt(1, doc.getDocument_id());
            pst.setString(2, doc.getTitle());
            pst.setString(3, doc.getText());
            //преобразую java.util.Date в java.sql.Date:
            java.sql.Date sqlDate = utilToSqlDate(doc.getDate());  //----------------------> вспомогательный метод внизу класса.
            pst.setDate(4, sqlDate);
            pst.setInt(5, doc.getAuthor_id());
            
            pst.execute();
            int n = pst.getUpdateCount();
            System.out.println("Количество обновленных элементов в таблице Documents: = " + n);

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
                //setConnection(USER, PASSWORD);
                connection = Connector.getConnection();
                //ПРИМЕР множественного обновления полей : UPDATE goods SET title = "утюг", price = 300 WHERE num = 2
                //обновляю документы:
                QUERY = "UPDATE Documents SET doc_name = ?, doc_text = ?, doc_date = ? WHERE doc_id = ?";
                
                pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pst.setString(1, doc.getTitle());
                pst.setString(2, doc.getText());
                //преобразую java.util.Date в java.sql.Date:
                java.sql.Date sqlDate = utilToSqlDate(doc.getDate());  //----------------------> вспомогательный метод внизу класса.
                pst.setDate(3, sqlDate);
                pst.setInt(4, doc.getDocument_id());
                
                pst.execute();
                int n = pst.getUpdateCount();
                System.out.println("Количество обновленных элементов в таблице Documents: = " + n);
                
                //теперь обновляю автора (по id автора):
                QUERY = "UPDATE Authors SET auth_name = ?, auth_note = ? WHERE auth_id = ?";
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

    
    /**
    * Метод производит поиск документов по их автору.
    *
    * @param author автор документа. Объект может содержать неполную информацию
    * об авторе. Например, объект может содержать только именные данные автора
    * или только его идентификатор. 
    * @return возвращает массив всех найденных документов. Еслив базе данных
    * не найдено ни одного документа, то возвращается значение null.
    * @throws DocumentException выбрасывается в случае, если поле объекта
    * author заполнены неправильно или нелья выполнить поиск по его полям.
    * Данное исключение также выбрасывается в случае общей ошибки доступа к базе данных
    */
    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {
        //Создаю ссылку на массив для возвращаемого значения:
        Document[] documents = null;
        //буду добавлять документы из выборки в лист. Потом на основе листа создам объект Document[];
        List<Document> list = new ArrayList<>();
        //1. Если auth_id не 0 - ищем по ID (принимаю, что если auth_id == 0 - поле auth_id не заполнено)
        if(author.getAuthor_id() != 0){
            //1.1. Драйвер загружен в конструкторе.
            try{
            //1.2. Соединение создано:
            connection = Connector.getConnection(); //---------------------------------> вызывается в конструкторе. Но уже может быть закрыто.
            QUERY = "SELECT Documents.doc_id, Documents.doc_name, Documents.doc_text, Documents.doc_date, Documents.doc_author_id "
                    + "FROM Documents INNER JOIN Authors "
                    + "ON (Documents.doc_author_id = Authors.auth_id) "
                    + "WHERE Authors.auth_id = ?";
            //1.3. Создаю препередСтейтмент:
            pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pst.setInt(1, author.getAuthor_id());
            }catch(SQLException ex){
                System.out.println("Метод findDocumentByAuthor()://1 Ошибка создания preparedStatement");
                ex.printStackTrace();
            }
            
            //1.4. Выполняю препередСтейтмент:
            try {
                pst.execute();
            } catch (SQLException ex) {
                System.out.println("Метод findDocumentByAuthor()://1 Ошибка execute preparedStatement");
                ex.printStackTrace();
            }
            
            //1.5. Получаю ResultSet:
            ResultSet rs = null;
            try {
                rs = pst.getResultSet();
            } catch (SQLException ex) {
                System.out.println("Метод findDocumentByAuthor()://1 Ошибка создания resultSet");
                ex.printStackTrace();
            }
            
            //1.5.1. - обрабатываю итоги ResultSet:
            try{
            if(rs != null){
                while (rs.next()){
                    int doc_id = rs.getInt("doc_id");
                    String doc_name = rs.getString("doc_name");
                    String doc_text = rs.getString("doc_text");
                    java.sql.Date sql_date = rs.getDate("doc_date");
                    Date doc_date = sqlToUtilDate(rs.getDate("doc_date"));
                    int doc_author_id = rs.getInt("doc_author_id");

                    list.add(new Document(doc_id, doc_name, doc_text, doc_date, doc_author_id));
                }
            }
            }catch(SQLException ex){
                System.out.println("Метод findDocumentByAuthor()://1 Ошибка получения данных из resultSet");
                ex.printStackTrace();
            }
        }
        //2. иначе если имя не "" (пустая строка) и не null (ну null не может быть из-за отсутствия такого конструктора, но...) - тогда ищем по имени
        else if(author.getAuthor() != null && !"".equals(author.getAuthor())){
            //2.1. Драйвер загружен в конструкторе.
            //2.2. Соединение создано в конструкторе
            //2.3. Создаю препередСтейтмент:
            try{
            //connection = Connector.getConnection(); //---------------------------------> вызывается в конструкторе.
            QUERY = "SELECT Documents.doc_id, Documents.doc_name, Documents.doc_text, Documents.doc_date, Documents.doc_author_id "
                    + "FROM Documents INNER JOIN Authors "
                    + "ON (Documents.doc_author_id = Authors.auth_id) "
                    + "WHERE Authors.auth_name = ?";
            pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, author.getAuthor());
            }catch(SQLException ex){
                System.out.println("Метод findDocumentByAuthor()//2: Ошибка создания preparedStatement");
                ex.printStackTrace();
            }
            
            //2.4. Выполняю препередСтейтмент:
            try {
                pst.execute();
            } catch (SQLException ex) {
                System.out.println("Метод findDocumentByAuthor()//2: Ошибка execute preparedStatement");
                ex.printStackTrace();
            }
            
            //2.5. Получаю ResultSet:
            ResultSet rs = null;
            try {
                rs = pst.getResultSet();
            } catch (SQLException ex) {
                System.out.println("Метод findDocumentByAuthor()://2 Ошибка создания resultSet");
                ex.printStackTrace();
            }
            
            //2.5.1. - обрабатываю итоги ResultSet:
            try{
            if(rs != null){
                while (rs.next()){
                    int doc_id = rs.getInt("doc_id");
                    String doc_name = rs.getString("doc_name");
                    String doc_text = rs.getString("doc_text");
                    java.sql.Date sql_date = rs.getDate("doc_date");
                    Date doc_date = sqlToUtilDate(rs.getDate("doc_date"));
                    int doc_author_id = rs.getInt("doc_author_id");

                    list.add(new Document(doc_id, doc_name, doc_text, doc_date, doc_author_id));
                }
            }
            }catch(SQLException ex){
                System.out.println("Метод findDocumentByAuthor()://2 Ошибка получения данных из resultSet");
                ex.printStackTrace();
            }
        }
        //3. иначе объект автор заполнен неправильно -кидаем исключение:
        else{
            System.out.println("Ошибка в методе findDocumentByAuthor()");
            throw new DocumentException("Поля автора заполнены некорректно.");
        }
        
        //6. теперь закрываю соединение:
        closeConnection();
        if(list != null){
            documents = new Document[list.size()];
            documents = list.toArray(documents);
        }else{
            System.out.println("Список авторов пуст!");
        }
        return documents;
    }

    
     /**
    * Метод производит поиск документов по их содержанию.
    * @param content фрагмент текста (ключевые слова), который должен
    * содержаться в заголовке или в основном тексте документа.
    * @return возвращает массив найденных документов.Если в базе данных не
    * найдено ни одного документа, удовлетворяющего условиям поиска, то
    * возвращается значение null.
    * @throws DocumentException выбрасывается в случае, если строка content
    * равна null или является пустой. Данное исключение также выбрасывается в
    * случае общей ошибки доступа к базе данных
    */
    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {
        if(content == null || "".equals(content)){
            throw new DocumentException("передаваемая строка - пустая или null");
        }
        //получаю воообще все документы из БД:
        List<Element> allElements = checkTable("Documents");
        
        //фильтрую все документы и получаю два листа - фильтр по содержимому из названия и из текста:
        List<Element> listFromTitle = allElements.stream().filter(e -> ((Document)e).getTitle().contains(content)).collect(Collectors.toList());

        List<Element> listFromText = allElements.stream().filter(e -> ((Document)e).getText().contains(content)).collect(Collectors.toList());

        //объединяю оба листа в один сет, таким образом ибавляясь от дублей.
        Set<Element> set = new HashSet<>();
        set.addAll(listFromTitle);
        set.addAll(listFromText);
        
        //содержимое сет записываю в массив:
        Document[] documents = new Document[set.size()];
        int i = 0;
        for(Element e : set){
            documents[i] = (Document)e;
            i++;
        }
        
        return documents;
    }

    /**
    * Метод удаляет автора из базы данных. Вместе с автором удаляются и все
    * документы, которые ссылаются на удаляемого автора. 
    * @param author удаляемый автор. Объект может содержать неполные данные автора, например, только идентификатор автора.
    * @return значение true, если запись автора успешно удалена, и значение false - в противном случае.
    * @throws DocumentException выбрасывается в случае, если поля объекта author заполнены неправильно 
    * или ссылка author равна null, а также случае общей ошибки доступа к базе данных.
    */    
    @Override
    public boolean deleteAuthor(Author author) throws DocumentException {
        if(author == null || author.getAuthor() == null || "".equals(author.getAuthor())){
            throw new DocumentException();
        }
        boolean b = false;
        
        //1. Если у автора заполнен id (если == 0 - считаем не заполнен) - то удаляем все его документы по его id и удаляем автора по id.
        if(author.getAuthor_id() != 0){
            //1.1. Драйвер загружен в конструкторе.
            try{
            //1.2. Соединение создано:
            connection = Connector.getConnection(); //---------------------------------> вызывается в конструкторе. Но уже может быть закрыто.

            QUERY = "DELETE FROM Documents WHERE doc_author_id = ?";
            //1.3.DOC: Создаю препередСтейтмент:
            pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pst.setInt(1, author.getAuthor_id());
            }catch(SQLException ex){
                System.out.println("Метод deleteAuthor(Author author)://1.3.DOC: Ошибка создания preparedStatement");
                ex.printStackTrace();
            }
            
            //1.4.DOC: Выполняю препередСтейтмент:
            try {
                pst.execute();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://1.4.DOC: Ошибка execute preparedStatement");
                ex.printStackTrace();
            }
            
            //1.5.DOC: Получаю количество удаленных записей из таблицы Documents:
            int n = 0;
            try {
                n = pst.getUpdateCount();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://1.5.DOC: Ошибка execute getUpdateCount()");
                ex.printStackTrace();
            }
            System.out.println("Количество удаленных Документов: = " + n);
            
            
            QUERY = "DELETE FROM Authors WHERE auth_id = ?";
            //1.3.AUTH: Создаю препередСтейтмент:
            PreparedStatement pst2 = null;   //создаю еще один объект PreparedStatement, потому что pst уже использован и  умер.    
            try{
                pst2 = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pst2.setInt(1, author.getAuthor_id());
            }catch(SQLException ex){
                System.out.println("Метод deleteAuthor(Author author)://1.3.AUTH: Ошибка создания preparedStatement");
                ex.printStackTrace();
            }
            
            //1.4.AUTH: Выполняю препередСтейтмент:
            try {
                pst2.execute();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://1.4.AUTH: Ошибка execute preparedStatement");
                ex.printStackTrace();
            }
            
            //1.5.AUTH: Получаю количество удаленных записей из таблицы Documents:
            int m = 0;
            try {
                m = pst2.getUpdateCount();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://1.5.AUTH: Ошибка execute getUpdateCount()");
                ex.printStackTrace();
            }
            System.out.println("Количество удаленных Авторов: = " + m);           
            b = m > 0;
        }
        
        
        //2. Иначе удаляем все документы по имени автора и самого автора удаляем по имени.
        else{
            //2.1. Драйвер загружен в конструкторе.
            try{
            //2.2. Соединение создано:
            connection = Connector.getConnection(); //---------------------------------> вызывается в конструкторе. Но уже может быть закрыто.
            //упс.. а тут все равно по id... по тому что он всегда есть - поумолчанию это поле автора инициализируется нулем.)
            int auth_id = author.getAuthor_id();
            QUERY = "DELETE FROM Documents WHERE doc_author_id = ?";
            //2.3.DOC: Создаю препередСтейтмент:
            pst = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pst.setInt(1, auth_id);
            }catch(SQLException ex){
                System.out.println("Метод deleteAuthor(Author author)://2.3.DOC: Ошибка создания preparedStatement");
                ex.printStackTrace();
            }
            
            //2.4.DOC: Выполняю препередСтейтмент:
            try {
                pst.execute();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://2.4.DOC: Ошибка execute preparedStatement");
                ex.printStackTrace();
            }
            
            //2.5.DOC: Получаю количество удаленных записей из таблицы Documents:
            int n = 0;
            try {
                n = pst.getUpdateCount();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://2.5.DOC: Ошибка execute getUpdateCount()");
                ex.printStackTrace();
            }
            System.out.println("Количество удаленных Документов: = " + n);
            
            
            QUERY = "DELETE FROM Authors WHERE auth_name = ?";
            //2.3.AUTH: Создаю препередСтейтмент:
            PreparedStatement pst2 = null; //создаю еще один объект PreparedStatement, потому что pst уже использован и  умер.  
            try{
            pst2 = connection.prepareStatement(QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pst2.setString(1, author.getAuthor());
            }catch(SQLException ex){
                System.out.println("Метод deleteAuthor(Author author)://2.3.AUTH: Ошибка создания preparedStatement");
                ex.printStackTrace();
            }
            
            //2.4.AUTH: Выполняю препередСтейтмент:
            try {
                pst2.execute();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://2.4.AUTH: Ошибка execute preparedStatement");
                ex.printStackTrace();
            }
            
            //2.5.DOC: Получаю количество удаленных записей из таблицы Documents:
            int m = 0;
            try {
                m = pst2.getUpdateCount();
            } catch (SQLException ex) {
                System.out.println("Метод deleteAuthor(Author author)://2.5.AUTH: Ошибка execute getUpdateCount()");
                ex.printStackTrace();
            }
            System.out.println("Количество удаленных Авторов: = " + m);           
            b = m > 0;
        }
        
        //6. закрываю соединение.
        closeConnection();
        return b;
    }

    @Override
    public boolean deleteAuthor(int id) throws DocumentException {
        throw new DocumentException();
    }
    
    ///////////////////////////////////////////вспомогательные методы://////////////////////////////////////////
    
    //1. - загрузка драйвера - через систему.
    //2. - установка соединения - этот метод не использую, делаю через статический метод класса Connector.
    private void setConnection (String user, String password){
        try {
            System.out.println("2. Соединение установлено.");
            connection = DriverManager.getConnection(URL, user, password);

        } catch (SQLException ex) {
            System.out.println("Error 2: Соединение не установлено. " + ex.getMessage());
        }
    }
    
    /**
    * просмотр таблицы авторов/документов и вывод строк таблицы в консоль
    * Выводит список в консоль.
    * @param tableName - название таблицы в базе данных.
    * @return список всех авторов/документов из БД.
    */
    protected List<Element> checkTable(String tableName){
        //устанавливаем соединение:
        //setConnection(USER, PASSWORD);
        //connection = null;
        List<Element> list = new ArrayList<>();
        connection = Connector.getConnection();
        
        if(tableName.equals("Authors")){
            QUERY = "SELECT * FROM Authors ORDER BY auth_id ASC";
        }else if(tableName.equals("Documents"))
            QUERY = "SELECT * FROM Documents ORDER BY doc_id ASC";
        Statement statement = null;
        
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            System.out.println("3: метод checkTable(): Statement не создано");
        }
        
        
        boolean b = false;
        try {
            b = statement.execute(QUERY);
        } catch (SQLException ex) {
            System.out.println("4: метод checkTable(): Statement не execut'нулось");
            System.out.println("ex.getMessage() = " + ex.getMessage());
            ex.printStackTrace();
        }

        
        if(b){
            ResultSet rs = null;
            try {
                rs = statement.getResultSet();
            } catch (SQLException ex) {
                System.out.println("5: метод checkTable(): ResultSet не получен.");
            }
            System.out.println("5: метод checkTable(): Получили ResultSet (Выборку)");
            list = printResult(rs, tableName);
        }
        
        closeConnection();
        return list;
    }
    
    /**
    * Данный метод вызывается только из метода checkTAble(),
    * просмотр таблицы авторов/документов.
    * @param tableName - название таблицы в базе данных.
    * @return список всех авторов/документов из БД.
    */
    //метод получения списка Элементов (Author, Document) из указанной таблицы полученного resultSet:
    private List<Element> printResult(ResultSet rs, String tableName){
        List<Element> list = new ArrayList<>();
        try{
            if(rs != null){
                while (rs.next()){
                    if(tableName.equals("Authors")){
                        //для таблицы Authors:
                        int id = rs.getInt(1);
                        String name = rs.getString("auth_name");
                        String note = rs.getString("auth_note");
                        //System.out.println("id = " + id + ". name = " + name + ". note = " + note);
                        Author author = new Author(id, name, note);
                        list.add(author);
                    }else if(tableName.equals("Documents")){
                        //для таблицы Documents:
                        //int document_id, String title, String text, Date date, int author_id
                        int id = rs.getInt(1);
                        String docName = rs.getString("doc_name");
                        String docText = rs.getString("doc_text");
                        Date date = sqlToUtilDate(rs.getDate("doc_date")); //вспомогательный метод - ниже в этом классе.
                        int docAuthID = rs.getInt("doc_author_id");
                        
                        //System.out.println("id = " + id + ". name = " + docName + ". text = " + docText + ". date = " + sqlDate + ". doc_author_id = " + docAuthID);
                        Document document = new Document(id, docName, docText, date, docAuthID);
                        list.add(document);
                    }
                }
            }
        }catch(SQLException ex){
            System.out.println("5: ResultSet не напечатан. " + ex.getMessage());
        }
        return list;
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
                System.out.println("6. метод closeConnection(): Соединение закрыто");
            }
        } catch (SQLException ex) {
                //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("6: метод closeConnection(): Ошибка закрытия соединения. " + ex.getMessage());
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
            System.out.println("Не удалось получить объект DatabaseMetaData.");
        }
        tables.stream().forEach(System.out::println);
        closeConnection();
        return tables;
    }
    
    /**
    * вспомогательный метод - для получения списка всех заранее подготовленных SQL запросов
    * из файла queries.sql в пакете resources.
    * Выводит список в консоль.
    * @return словарь(мапу) всех заранее подготовленных SQL запросов.
    */
    protected HashMap<String, String> getQueries(){
        List<String> queries = new ArrayList<>();
       
        Path path = Paths.get("D:\\coding\\politeh\\RuzaevJ130Lab01\\src\\resources\\queries.txt");
        //Path path = Paths.get("resources/queries.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(new File(path.toString())))){
            while(br.ready()){
                queries.add(br.readLine());
            }
        }catch(IOException ex){
            System.out.println("Ошибка чтения из файла resources/queries.txt");
            ex.printStackTrace();
        }
        
        HashMap<String, String> map = new HashMap<>();
        
        for(String s : queries){
            String key = s.substring(0, s.indexOf("="));
            String value = s.substring(s.indexOf("=") + 1);
            
            map.put(key, value);
            System.out.println(key + " : " + value);
        }
              
        return map;
    }
    
    /**
    * вспомогательный метод - для получения списка всех заранее подготовленных SQL запросов
    * из файла queries.sql в пакете resources.
    * Выводит список в консоль.
    * @return словарь(мапу) всех заранее подготовленных SQL запросов.
    */
    private boolean isElementInTable(Element e, String tableName){
        List<Element> elements = checkTable("Authors");
        return elements.contains(e);
    }
    
    /**
    * вспомогательный метод - для получения даты в формате java.util.Date из java.sql.Date;
    * @param sql_date - дата в формате java.sql.Date;
    * @return дата в формате java.util.Date, соответствующая дате аргумента переданного в виде параметра метода sql_date.
    */    
    private java.util.Date sqlToUtilDate(java.sql.Date sql_date) {
        if (sql_date != null) {
            String s = sql_date.toString();//2008-08-22
            int year = Integer.parseInt(s.substring(0, s.indexOf("-")));
            int month = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.lastIndexOf("-")));
            int day = Integer.parseInt(s.substring(s.lastIndexOf("-")));
            //int year, int month, int date
            Date doc_date = new Date(year, month, day);
            return doc_date;
        }else{
            return null;
        }
    }
    
    /**
    * вспомогательный метод - для получения даты в формате java.sql.Date из java.util.Date;
    * @param util_date - дата в формате java.util.Date;
    * @return дата в формате java.sql.Date, соответствующая дате аргумента переданного в виде параметра метода util_date.
    */       
    private java.sql.Date utilToSqlDate(java.util.Date util_date) {
        //преобразую java.util.Date в java.sql.Date:
        if(util_date != null){
            util_date.setYear(util_date.getYear() - 1900); // --------------------> уйти от проблемы разницы во времени в 1900 лет.
            //SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            //String s = sdf.format(date);
            //java.sql.Date sqlDate = java.sql.Date.valueOf(s);
            return new java.sql.Date(util_date.getTime());
        }else{
            return null;
        }
    }
}
