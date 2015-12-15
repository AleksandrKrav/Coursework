import analyzers.LexicalAnalyser;
import analyzers.Parser;
import analyzers.Simantic;
import enums.Types;

import java.util.*;

public class Main {

    public static void main(String args[]) {

        String string = "var a , b: integer; \n " + "  c: real; \n" +
                "begin \n" +
//                "    a := 3; \n" +
////                "  \n"+
//                "    if (b < 4) then \n" +
//                "       a := 2;  \n" +
//                "for i:=1 to a do \n" +
//                "  begin \n" +
//                "    b := b +1; \n" +
//                "  end; \n" +
//                "while ( i < a ) do \n" +
//                "  b:= b + 2; \n" +
//                "                \n" +
//
//                "b := a; \n" +
//                " else  \n"+
//                "c := a;  \n"+
                "a := 5 mod 2; \n" +
                "writeln(a);\n" +
                "end.";
        ArrayList<String> commands;
        ArrayList<Types> types;

        LexicalAnalyser lexicalAnalyser = new LexicalAnalyser(string);
        commands = lexicalAnalyser.analyze();
        types = lexicalAnalyser.getTypes();

        Parser parser = new Parser(commands, types);
        parser.parse();
        Simantic s = new Simantic(parser.getNodes(), parser.getNodes());
        s.intialization();
        s.simanticParse(s.getFuncBegin());
        s.printResult("file.txt");
    }
}
