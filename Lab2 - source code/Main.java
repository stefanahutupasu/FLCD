import model.ST;

public class Main {

    public static void main(String[] args) {
        System.out.println("Inserting values ...");
        ST symTable = new ST(29);

        // Inserting some values
        symTable.insert("a");
        symTable.insert("b");
        symTable.insert("c");
        symTable.insert("ab");
        symTable.insert("ba");
        symTable.insert("a");

        symTable.find("d");
    }
}