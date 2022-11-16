import exceptions.ScannerException;
import model.CScanner;
import model.ST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        System.out.println("finding c:" + symTable.find("c"));
        System.out.println("finding d:" + symTable.find("d"));

        try {
            Scanner programReader = new java.util.Scanner(new File("programs/p2"));
            StringBuilder program = new StringBuilder();
            while (programReader.hasNextLine()) {
                program.append(programReader.nextLine()).append('\n');
            }
            programReader.close();
            Scanner tokensReader = new java.util.Scanner(new File("scanner_input/token"));
            List<String> tokens = new ArrayList<>();
            while (tokensReader.hasNextLine()) {
                tokens.add(tokensReader.nextLine());
            }
            CScanner scanner = new CScanner(program.toString(), tokens);
            try {
                scanner.scan();
            } catch (ScannerException e) {
                System.out.println(e.getMessage());
                scanner.printToSTFile();
                scanner.printToPIFFile();
                return;
            }
            scanner.printToSTFile();
            scanner.printToPIFFile();
            System.out.println("lexically correct");
        } catch (FileNotFoundException e) {
            System.out.println("Source file not found");
        } catch (IOException e) {
            System.out.println("Can't write output");
        }
    }
 }
