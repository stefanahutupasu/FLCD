package model;

import exceptions.ScannerException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CScanner {
    private final String program;
    private final List<String> tokens;
    private final ST symbolTable;
    private final List<Pair<String,Integer>> pif;
    private int index;
    private int currentLine;
    private String lastFoundToken = "";
    private FA fa_const;

    private String faultToken;

    public CScanner(String program, List<String> tokens, FA fa_const) {
       //System.out.println(program);
       // System.out.println(tokens);
        this.program = program;
        this.tokens = tokens;
        this.symbolTable = new ST(100);
        this.pif = new ArrayList<>();
        this.index = 0;
        this.currentLine = 1;
        this.fa_const = fa_const;
    }

    public String getFaultToken() {
        return faultToken;
    }


    //skips characters that are considered blank/whitespace and returns true if such a skip happens
    private boolean skipWhiteSpace() {
        boolean skip = false;
        while (index < program.length() && Character.isWhitespace(program.charAt(index))) {
            if (program.charAt(index) == '\n') {
                currentLine++;
                skip = true;
            }
            index++;
        }
        return skip;
    }

    //skips current line if a comment is found (marked with $) and returns true if such a skip happens
    private boolean skipComment() {
        boolean changed = false;
        if (program.startsWith("$", index)) {
            changed = true;
            while (index < program.length() && program.charAt(index) != '\n') {
                index++;
            }
        }
        return changed;
    }

    //use regex to match string constants.<br>
    private boolean stringConstant() throws ScannerException {
        //must begin with " and end with " and must contain only letters, digits, _ and space
        Pattern strRegex = Pattern.compile("^\"([a-zA-z0-9_ ]*)\"");
        Matcher matcher = strRegex.matcher(program.substring(index));
        if (matcher.find()) {
            String token = matcher.group(1);
            pif.add(new Pair<>("strConst", -2));
            symbolTable.insert("\""+token+"\"");
            index += matcher.end();
            System.out.println("Found string constant: " + token);
            lastFoundToken = token;
            return true;
        }

        //starts with "  contains at least one character not " and ends with "
        strRegex = Pattern.compile("^\"[^\"]+\"");
        matcher = strRegex.matcher(program.substring(index));
        if (matcher.find()) {
            throw new ScannerException("Lexical error: Invalid characters inside string on line " + currentLine);
        }

        //start with "
        strRegex = Pattern.compile("^\"");
        matcher = strRegex.matcher(program.substring(index));
        if (matcher.find())
            throw new ScannerException("Lexical error: String not closed on line " + currentLine);
        return false;
    }

    //use regex to match int constants
    private boolean intConstant() {

        boolean found = true;
        int end_index = 1;
        //String currentToken = program.substring(index, index+end_index);
        if(program.charAt(index) == '-')
            end_index = 2;
        String currentToken = program.substring(index, index+end_index);

        while(fa_const.checkAccepted(currentToken))
        {
            end_index +=1;
            currentToken = program.substring(index, index+end_index);

        }
        if(end_index == 1)
            return false;
        else
        {   String token = program.substring(index, index + end_index - 1);
            if (pif.size() > 0) {
            Integer pif_last = pif.get(pif.size() - 1).getValue();
            //System.out.println(pif_last);
            //special case. If the last element in the pif was not in token.in (token list) then we do not consider
            //it an intConstant (it would have been if pif_last was 0)
            if ((token.charAt(0) == '+' || token.charAt(0) == '-') && (pif_last == -1 || pif_last == -2)) {
                //System.out.println("of1");
                return false;
            }
            //contains requires a String, so we concatenate the character to an empty string
            //System.out.println(program.charAt(index+ matcher.end()));
            //System.out.println(Character.isDigit(program.charAt(index+ matcher.end())));

            System.out.println("Found int constant: " + token);
                pif.add(new Pair<>("intConst", -1));
                symbolTable.insert(token);
                lastFoundToken = token;
                index += (end_index - 1);
                return true;
        }
            }
        return false;
        /*Pattern intRegex = Pattern.compile("^([-]?[1-9]\\d*|0)");
        Matcher matcher = intRegex.matcher(program.substring(index));
        if (matcher.find()) {
            String token = matcher.group(1);
            //System.out.println(token);
            if (pif.size() > 0) {
                Integer pif_last = pif.get(pif.size() - 1).getValue();
                //System.out.println(pif_last);
                //special case. If the last element in the pif was not in token.in (token list) then we do not consider
                //it an intConstant (it would have been if pif_last was 0)
                if ((token.charAt(0) == '+' || token.charAt(0) == '-') && (pif_last == -1 || pif_last == -2)) {
                    //System.out.println("of1");
                    return false;
                }
                //contains requires a String, so we concatenate the character to an empty string
                //System.out.println(program.charAt(index+ matcher.end()));
                //System.out.println(Character.isDigit(program.charAt(index+ matcher.end())));
                if (program.charAt(index + matcher.end()) != ' ' && !tokens.contains(""+program.charAt(index+ matcher.end())) && !Character.isDigit(program.charAt(index+ matcher.end())))
                {


                return false;}
                System.out.println("Found int constant: " + token);
            }
            pif.add(new Pair<>("intConst", -1));
            symbolTable.insert(token);
            lastFoundToken = token;
            index += matcher.end();
            return true;
        }
        return false;*/
    }

    //checks for token from token.in
    private boolean tokenFromList() {
        //if its read or print it has to be followed by (
        for (String token : tokens) {
            if (program.startsWith(token, index)) {
                index += token.length();
                if(isIO(token) && !program.startsWith("(", index))
                {index -= token.length();return false;}
                //if it is a (, there has to be another ) on the same line

                //if last token is fun, then we wont consider the current one as one from the list
                //as it is expected for it to be an identifier
                if(isFun(lastFoundToken))
                {index -= token.length();return false;}
                pif.add(new Pair<>(token, 0));

                System.out.println("Found token from list: " + token);
                lastFoundToken = token;
                return true;
            }
        }
        return false;
    }

    //checks if given string is a token representing a function declaration
    private boolean isFun(String lastFoundToken) {
        return lastFoundToken.equals("fun");
    }

    //checks if given string is either a token representing a read or print operation
    private boolean isIO(String token) {
        if(token.equals(tokens.get(21)))
            return true;
        return token.equals(tokens.get(17));
    }

    //uses regex to match identifiers
    private boolean identifier() {
        Pattern idRegex = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = idRegex.matcher(program.substring(index));
        if (matcher.find()) {
            String token = matcher.group(1);

            if(symbolTable.find(token) == -1 && !isTypeToken(lastFoundToken) && !isFun(lastFoundToken))
                return false;
            System.out.println("Found identifier: " + token);
             if(symbolTable.insert(token))
                pif.add(new Pair<>("id", symbolTable.find(token)));

            lastFoundToken = token;
            index += matcher.end();
            return true;
        }
        return false;
    }

    //checks if given string is a token representing a type
    private boolean isTypeToken(String s) {
        //18 19 20
        if(s.equals(tokens.get(18))) return true;
        if(s.equals(tokens.get(19))) return true;
        if(s.equals(tokens.get(20))) return true;
        if(s.equals(tokens.get(35))) return true;
        return false;
    }

    //process a token (tests current index of program's parsing as string for a match)
    private void nextToken() throws ScannerException {
        while (true) {
            if (!skipWhiteSpace() && !skipComment())
                break;
        }
        if (index == program.length())
            return;
        if (stringConstant() || intConstant() || identifier() || tokenFromList() ) {
            return;
        }
        StringBuilder faultTokenSb = new StringBuilder();
        while (index < program.length() && (!Character.isWhitespace(program.charAt(index)) || program.charAt(index) == '\n') && !tokens.contains(program.charAt(index) + "")) {
            faultTokenSb.append(program.charAt(index));
            index++;
        }
        this.faultToken = faultTokenSb.toString();
        throw new ScannerException("Lexical error: Cannot classify token " + faultToken + " on line " + currentLine);
    }

    //scans the program
    public void scan() throws ScannerException {
        while (index < program.length()) {
            nextToken();
        }
    }

    //prints ST to file
    public void printToSTFile() throws IOException {
        FileWriter writer = new FileWriter("st/ST.out");
        writer.write(symbolTable.toString());
        writer.close();
    }

   //prints PIF to file
    public void printToPIFFile() throws IOException {
        FileWriter writer = new FileWriter("pif/PIF.out");
        StringBuilder str = new StringBuilder();
        pif.forEach(e -> str.append(e.getKey()).append(" -> ").append(e.getValue()).append('\n'));
        writer.write(str.toString());
        writer.close();
    }
}