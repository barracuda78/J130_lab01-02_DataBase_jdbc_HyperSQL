package ruzaevj130lab01_02;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        DbServer db = new DbServer();
        Author jv = new Author(777, "Jules Verne", "favorite");
        //Author jv = new Author(0, "Jules Verne", "not my favorite");
        //int document_id, String title, String text, Date date, int author_id
        Date deti_granta_date = new Date(1867, 8, 11);
        Date earth_moon_date = new Date(1858, 3, 15);
        Date earth_centre_date = new Date(1864, 4, 20);
        //Document deti_granta = new Document(1, "Deti kapitana Granta", "bla-bla-bla", deti_granta_date, 777);
        Document earth_moon = new Document(5, "From Earth to Moon", "u-la-la", earth_moon_date, 777);
        Document earth_centre = new Document(6, "Voyage au centre de la Terre", "tra-ta-ta", earth_centre_date, 777);
        

        //проверка добавления/изменения автора:
//        try {
//            boolean addedAuthor = db.addAuthor(jv);
//            System.out.println("Автор добавлен " + addedAuthor);  //если false - значит не добавлен, а изменен.
//        } catch (DocumentException ex) {
//            System.out.println("ошибка в методе addAuthor: " + ex.getMessage());
//        }

        //проверка добавления документа:
//        try {
//            boolean addedDocument = db.addDocument(earth_centre, jv);
//            System.out.println("Документ добавлен " + addedDocument);  //если false - значит не добавлен, а изменен.
//        } catch (DocumentException ex) {
//            System.out.println("ошибка в методе addDocument: " + ex.getMessage());
//        }

        //проверка изменения документа и автора
        //домумент и автор для тестирования изменеия документа и автора:
//        Document earth_moon2 = new Document(5, null, "u-la-la - 2", earth_moon_date, 777);
//        Author jv2 = new Author(777, null, "favorite - 2");
//        try {
//            boolean addedDocument2 = db.addDocument(earth_moon2, jv2);
//            System.out.println("Документ изменен " + !addedDocument2);  //если false - значит не добавлен, а изменен.
//        } catch (DocumentException ex) {
//            System.out.println("ошибка в методе addDocument: " + ex.getMessage());
//        }


        //тестирование метода findDocumentByAuthor()
        try {
            System.out.println("Все документы по автору: " + jv);
            Document[] d = db.findDocumentByAuthor(jv);
            Arrays.stream(d).forEach(System.out::println);
        } catch (DocumentException ex) {
            System.out.println("ошибка в методе findDocumentByAuthor(): " + ex.getMessage());
        }

        
//        //Содержимое таблицы Authors:
//        System.out.println("Таблица Authors:");
//        db.checkTable("Authors").stream().forEach(System.out::println);
        
        
          //Содержимое таблицы Documents:
//        System.out.println("Таблица Documents:");
//        db.checkTable("Documents").stream().forEach(System.out::println);

          //Список таблиц этой БД:  
//        System.out.println("Все пользовательские таблицы в этой БД:");
//        db.getAllUsersTables();
          
            //Список запросов из файла:
//          HashMap<String, String> map = db.getQueries();
          

    }
}
