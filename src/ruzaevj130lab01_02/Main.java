package ruzaevj130lab01_02;

import java.util.Date;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        DbServer db = new DbServer();
        Author jv = new Author(777, "Jules Verne", "favorite");
        //Author jv = new Author(0, "Jules Verne", "favoriteFAVORITE");
        //int document_id, String title, String text, Date date, int author_id
        Date deti_granta_date = new Date(1867, 8, 11);
        Date earth_moon_date = new Date(1858, 3, 15);
        //Document deti_granta = new Document(1, "Deti kapitana Granta", "bla-bla-bla", deti_granta_date, 777);
        Document earth_moon = new Document(5, "From Earth to Moon", "u-la-la", earth_moon_date, 777);
        
        //проверка добавления/изменения автора:
//        try {
//            boolean addedAuthor = db.addAuthor(jv);
//            System.out.println("Автор добавлен " + addedAuthor);  //если false - значит не добавлен, а изменен.
//        } catch (DocumentException ex) {
//            System.out.println("ошибка в методе addAuthor: " + ex.getMessage());
//        }

        //проверка добавления/изменения документа:
        try {
            boolean addedDocument = db.addDocument(earth_moon, jv);
            System.out.println("Документ добавлен " + addedDocument);  //если false - значит не добавлен, а изменен.
        } catch (DocumentException ex) {
            System.out.println("ошибка в методе addDocument: " + ex.getMessage());
        }
//        //Содержимое таблицы Authors:
//        System.out.println("Таблица Authors:");
//        db.checkTable("Authors");

          //Содержимое таблицы Documents:
//        System.out.println("Таблица Documents:");
//        db.checkTable("Documents");

          //Список таблиц этой БД:  
//        System.out.println("Все пользовательские таблицы в этой БД:");
//        db.getAllUsersTables();
          
            //Список запросов из файла:
//          HashMap<String, String> map = db.getQueries();
          

    }
}
