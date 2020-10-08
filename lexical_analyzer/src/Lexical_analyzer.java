import javax.swing.*;
import java.io.*;
import java.util.*;

public class Lexical_analyzer {
    enum States { START, SKIP, READ, STOP };
    static ArrayList<Tokens> Lexems = new ArrayList<Tokens>();

    static String[] KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "exports", "extends", "final", "finally", "float" ,"for",
            "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "module", "native", "new",
            "open", "opens", "package", "private", "protected", "provides", "public", "requires", "return",
            "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "to",
            "transient", "transitive", "try", "uses", "void", "volatile", "while", "with" };
    static String[] OPERATORS = { "+" , "–" , "/" , "*" , "%",
            "++" , "––", "!",
            "=" , "+=" , "-=" , "*=" , "/=" , "%=" , "^=",
            "==", "!=" , "<" , ">", "<=" , ">=",
            "&&" , "||",
            "&" , "|" ,"^" , "~",
            "<<" , ">>" , ">>>" };
    static String[] SPECIAL_SYMBOLS = { ",", ":", "*", "(", ")", "{", "}", "[", "]" };
    String[] IDENTIFIERS = { };
    String[] NUMERIC = { };

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static void LexCheck(String Lex){
        boolean checked = false;
        for (String i : KEYWORDS)
            if (Lex.equals(i)) {
                Tokens t = new Tokens(1, Lex);
                Lexems.add(t);
                return;
            }
        for (String i : OPERATORS)
            if (Lex.equals(i)) {
                Tokens t = new Tokens(2, Lex);
                Lexems.add(t);
                return;
            }
        for (String i : SPECIAL_SYMBOLS)
            if (Lex.equals(i)) {
                Tokens t = new Tokens(3, Lex);
                Lexems.add(t);
                return;
            }
        if (isNumeric(Lex))
        {
            Tokens t = new Tokens(5, Lex);
            Lexems.add(t);
        }
        else {
            Tokens t = new Tokens(4, Lex);
            Lexems.add(t);
        }
    }

    public static void main(String[] args) {

        States state = States.START;

        try (FileReader fr = new FileReader("input.java"))
        {
            String buf = "";
            int inputChar = fr.read();
            while (state != state.STOP) {
                switch (state) {
                    case START:
                        //System.out.println("START ");

                        state = States.SKIP;

                        break;
                    case SKIP:
                        //System.out.println("SKIP ");

                        while (inputChar == ' ' || inputChar == '\t' || inputChar == '\r' || inputChar == '\n')
                            inputChar = fr.read();
                        state = States.READ;

                        break;
                    case READ:
                        //System.out.println("READ ");

                        while (inputChar != ' ' && inputChar != -1) {
                            buf += (char) inputChar;
                            inputChar = fr.read();
                        }
                        //System.out.print(buf + " ");
                        Lexical_analyzer.LexCheck(buf);
                        buf = "";
                        if (inputChar == ' ')
                            state = States.SKIP;
                        else state = States.STOP;

                        break;
                    case STOP:
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        for(Tokens tokens : Lexems){
            tokens.displayToken();
        }
    }
}