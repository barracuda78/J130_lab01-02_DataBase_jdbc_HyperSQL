package ruzaevj130lab01_02;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DbServer db = new DbServer();
        //int author_id, String author, String notes
        Author edgarPo = new Author(777, "Jules Verne", "favorite");
        
//        try {
//            boolean addedAuthor = db.addAuthor(edgarPo);
//            System.out.println("Автор добавлен: " + addedAuthor);
//        } catch (DocumentException ex) {
//            System.out.println("ошибка в методе addAuthor: " + ex.getMessage());
//        }
        
        System.out.println("Таблица Authors:");
        db.checkTable("Authors");
        System.out.println("Таблица Documents:");
        db.checkTable("Documents");
        System.out.println("Все пользовательские таблицы в этой БД:");
        db.getAllUsersTables();
        
    }
}
