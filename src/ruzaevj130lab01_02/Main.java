package ruzaevj130lab01_02;

public class Main {
    public static void main(String[] args) {
        DbServer db = new DbServer();
        //int author_id, String author, String notes
        Author edgarPo = new Author(666, "Edgar Alan Po", "favorite");
        
        try {
            boolean addedAuthor = db.addAuthor(edgarPo);
        } catch (DocumentException ex) {
            System.out.println("ошибка в методе addAuthor: " + ex.getMessage());
        }
    }
}
