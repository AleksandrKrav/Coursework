import analyzers.LexicalAnalyser;
import analyzers.Parser;
import analyzers.Simantic;
import enums.Types;

import java.util.*;

public class Main {

    public static void main(String args[]) {

        String string = "var a , b: integer; \n " + "  c: real; \n" +
                "begin \n" +
                "    a := i; \n" +
//                "  \n"+
//                "           if (b < 4) then \n" +
//                "            a:= 2;  \n" +
//                "for i:=1 to a do \n" +
//                "    begin \n" +
//                "    while ( i < a ) do \n" +
//                "            b:= b + 2; \n" +
//                "                \n" +
//                "    end; \n" +

//                "        b := a; \n" +
//                "else  \n"+
//                " a := b;  \n"+
//                "a := 20; \n" +
//                " writeln(a);\n" +
                "end.";
//        String string = "begin u := a + (b + (k*h) + o) + d + g; end.";
//        String string = "begin u := a + b * (c + d * (e + f)); end.";
//        String string = "begin for a:= 1 to n to begin for b:=1 to n do begin a:=m; end; end; end.";
//        String string = "var a, b: integer; c: real; begin if ( a > b) then if (c = d) then a := b; end.";
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
        s.printResult();
//        System.out.println(s.simanticParse());
//        System.out.println(s.simanticParse());



//        System.out.println();
//
//        String[][] table = new String[commands.size()][3];
//        for (int i = 0; i < table.length; i++) {
//            table[i][0] = Integer.toString(i);
//            table[i][1] = commands.get(i);
//            table[i][2] = Integer.toString(i + 1);
//            System.out.println(table[i][0] + "\t" + table[i][1] + "\t\t" + table[i][2]);
//        }
//        table[table.length - 1][2] = "end";

    }
}
