package ruzaevj130lab01_02;

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
        
        
        db.checkTable("Authors");
        
    }
}
