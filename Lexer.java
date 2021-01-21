import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Lexer {
    enum States { S, IDENTIFIER, NUMERIC, CHAR_STRING_LITERAL, OPERATOR, SPECIAL_SYMBOL, F };
    private LinkedList<Lex> lexemes = new LinkedList<>();

    public Lexer(String inputFilePath,
                 Map<String, String> keywords,
                 Map<String, String> operators,
                 Map<String, String> special_symbols,
                 Map<String, String> charMap,
                 Map<String, String> stringMap,
                 Map<String, String> intMap,
                 Map<String, String> doubleMap,
                 Map<String, String> methodsMap,
                 Map<String, String> booleanMap,
                 Map<String, String> packageMap,
                 Map<String, String> classMap) {
        doWork(inputFilePath, keywords, operators, special_symbols, charMap, stringMap, intMap, doubleMap, methodsMap, booleanMap, packageMap, classMap);
    }

    private void doWork(String inputFilePath,
                        Map<String, String> keywords,
                        Map<String, String> operators,
                        Map<String, String> special_symbols,
                        Map<String, String> charMap,
                        Map<String, String> stringMap,
                        Map<String, String> intMap,
                        Map<String, String> doubleMap,
                        Map<String, String> methodsMap,
                        Map<String, String> booleanMap,
                        Map<String, String> packageMap,
                        Map<String, String> classMap) {
        States state = States.S;
        try (FileReader fr = new FileReader(inputFilePath)) {
            File idFile = new File("./in/maps/identifiers.txt");
            String buf = "";
            int flag = 0;
            int flagstring = 0;
            int flagchar = 0;
            int flagint = 0;
            int flagbool = 0;
            int flagdouble = 0;
            int flagpackage = 0;
            int flagclass = 0;
            int flagmethod = 0;
            int flagstr = 0;
            String delstr = "";
            String impstr = "";
            int inputChar = fr.read();
            while (state != state.F) {
                switch (state) {
                    case S:
                        //System.out.println("state = S");
                        while (inputChar == ' ' || inputChar == '\t' || inputChar == '\r' || inputChar == '\n')
                            inputChar = fr.read();
                        if (inputChar == '_' || inputChar == '$' || Character.isLetter(inputChar)) {
                            state = States.IDENTIFIER;
                            break;
                        } else if (Character.isDigit(inputChar)) {
                            state = States.NUMERIC;
                            break;
                        } else if (inputChar == '"' || inputChar == '\'') {
                            state = States.CHAR_STRING_LITERAL;
                            break;
                        } else if (special_symbols.containsKey(String.valueOf((char)inputChar))) {
                            state = States.SPECIAL_SYMBOL;
                            break;
                        } else if (operators.containsKey(String.valueOf((char)inputChar)) ||
                                inputChar == '!' || inputChar == '&' || inputChar == '|') {
                            state = States.OPERATOR;
                            break;
                        } else if (inputChar == -1)
                            state = States.F;
                        break;
                    case IDENTIFIER:
                        //System.out.println("state = IDENTIFIER");
                        boolean foundKeyword       = false;
                        boolean foundBoolean       = false;
                        boolean foundNullReference = false;
                        while ((Character.isLetter(inputChar) || Character.isDigit(inputChar) ||
                                inputChar == '_' || inputChar == '$') && inputChar != -1) {
                            buf += (char)inputChar;
                            inputChar = fr.read();
                        }
                        if (keywords.containsKey(buf)) {
                            lexemes.add(new Lex("KEYWORD", buf, keywords.get(buf)));
                            foundKeyword = true;
                        } else if (buf.equals("true")) {
                            lexemes.add(new Lex("LITERAL", buf, ""));
                            foundBoolean = true;
                        } else if (buf.equals("false")) {
                            lexemes.add(new Lex("LITERAL", buf, ""));
                            foundBoolean = true;
                        } else if (buf.equals("null")) {
                            lexemes.add(new Lex("LITERAL", buf, "nullReference"));
                            foundNullReference = true;
                        }
                        if (!foundKeyword && !foundBoolean && !foundNullReference) {
                            if (lexemes.isEmpty()) {

                            } else {
                                if (buf.equals("String")){
                                    if (!stringMap.containsValue(buf)){
                                        stringMap.put(Integer.toString(flagint), buf);
                                        flagstring++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("int")){
                                    if (!intMap.containsValue(buf)){
                                        intMap.put(Integer.toString(flagint), buf);
                                        flagint++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("char")){
                                    if (!charMap.containsValue(buf)) {
                                        charMap.put(Integer.toString(flagchar), buf);
                                        flagchar++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("boolean")){
                                    if (!booleanMap.containsValue(buf)) {
                                        booleanMap.put(Integer.toString(flagbool), buf);
                                        flagbool++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("String")) {/////
                                    if (!stringMap.containsValue(buf)) {
                                        stringMap.put(Integer.toString(flagstring), buf);
                                        flagstring++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;////////
                                }
                                else if (lexemes.getLast().getLexValue().equals("double")) {
                                    if (!doubleMap.containsValue(buf)) {
                                        doubleMap.put(Integer.toString(flagdouble), buf);
                                        flagdouble++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("import")) {
                                    if (!packageMap.containsValue(buf)) {
                                        impstr = impstr + buf + " ";
                                        packageMap.put(Integer.toString(flagpackage), buf);
                                        flagpackage++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals(".")){
                                    if (!packageMap.containsValue(buf)) {
                                        impstr = impstr + buf + " ";
                                        packageMap.put(Integer.toString(flagpackage), buf);
                                        flagpackage++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("class")){
                                    if (!classMap.containsValue(buf)) {
                                        classMap.put(Integer.toString(flagclass), buf);
                                        flagclass++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("void")){
                                    if (!methodsMap.containsValue(buf)) {
                                        methodsMap.put(Integer.toString(flagmethod), buf);
                                        flagmethod++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("private")){
                                    if (!methodsMap.containsValue(buf)) {
                                        methodsMap.put(Integer.toString(flagmethod), buf);
                                        flagmethod++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("public")) {
                                    if (methodsMap.containsValue(buf)) {
                                        methodsMap.put(Integer.toString(flagmethod), buf);
                                        flagmethod++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                } else if (lexemes.getLast().getLexValue().equals("String")){
                                    if (!stringMap.containsKey(buf)) {
                                        stringMap.put(Integer.toString(flagstr), buf);
                                        flagstr++;
                                    }
                                    lexemes.add(new Lex("IDENTIFIER", buf, Integer.toString(flag)));
                                    flag++;
                                }
                                System.out.println(stringMap);
                            }
                        }
                        buf = "";
                        state = States.S;
                        break;
                    case NUMERIC:
                        //System.out.println("state = NUMERIC");
                        boolean foundPeriod = false;
                        while ((Character.isDigit(inputChar) || (inputChar == '.' && !foundPeriod)) && inputChar != -1) {
                            if (inputChar == '.')
                                foundPeriod = true;
                            buf += (char)inputChar;
                            inputChar = fr.read();
                        }
                        lexemes.add(new Lex("LITERAL", buf, "NUMERIC"));
                        buf = "";
                        state = States.S;
                        break;
                    case CHAR_STRING_LITERAL:
                        if (inputChar == '"') {
                            inputChar = fr.read();
                            if (inputChar != '"') {
                                while (inputChar != '"' && inputChar != -1) {
                                    buf += (char) inputChar;
                                    inputChar = fr.read();
                                }
                                lexemes.add(new Lex("LITERAL", "\"" + buf + "\"", "STRING"));
                            }
                        } else {
                            inputChar = fr.read();
                            while ((inputChar != '\'' || buf.charAt(buf.length() - 1) == '\\' && buf.length() == 1) && inputChar != -1) {
                                buf += (char)inputChar;
                                inputChar = fr.read();
                            }
                            if (buf.length() == 1 || buf.equals("\\0") || buf.equals("\\a") || buf.equals("\\b") ||
                                    buf.equals("\\t") || buf.equals("\\n") || buf.equals("\\v") || buf.equals("\\f") ||
                                    buf.equals("\\r") || buf.equals("\\e") || buf.equals("\\'") || buf.equals("\\\"") || buf.equals("\\\\"))
                                lexemes.add(new Lex("LITERAL", "\'" + buf + "\'", "CHARACTER"));
                        }
                        buf = "";
                        inputChar = fr.read();
                        state = States.S;
                        break;
                    case SPECIAL_SYMBOL:
                        //System.out.println("state = SPECIAL_SYMBOL");
                        if (lexemes.getLast().getLexValue().equals(";")){
                            impstr = "";
                        }
                        lexemes.add(new Lex("SPECIAL_SYMBOL", String.valueOf((char)inputChar), special_symbols.get(String.valueOf((char)inputChar))));
                        inputChar = fr.read();
                        state = States.S;
                        break;
                    case OPERATOR:
                        //System.out.println("state = OPERATOR");
                        buf += (char)inputChar;
                        inputChar = fr.read();
                        if (operators.containsKey(buf+(char)inputChar)) {
                            lexemes.add(new Lex("OPERATOR", buf + String.valueOf((char)inputChar), operators.get(buf + (char)inputChar)));
                            inputChar = fr.read();
                        } else
                            lexemes.add(new Lex("OPERATOR", buf, operators.get(buf)));
                        buf = "";
                        state = States.S;
                        break;
                    case F:
                        //System.out.println("state = F");
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void printLexemes() {
        lexemes.forEach(lex -> System.out.println(lex.getLexClass() + " | " + lex.getLexValue() + " | " + lex.getLexAttribute()));
    }

    public LinkedList<Lex> getLexemes() {
        return lexemes;
    }
}