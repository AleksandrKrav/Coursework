import analyzers.LexicalAnalyser;
import analyzers.Parser;
import enums.Types;

import java.util.ArrayList;

public class Main {

        public static void main(String args[]) {

        String string = "var a, b: integer; \n " + "  c: real; \n" +
                "begin \n" +
//                "    a := a + 1; \n" +
//                "    while ( (b + 6) < (a + (20.0 *3 + f)) ) do \n" +
//                "        begin \n" +
//                "            a:=4;  \n" +
//                "            b:= 6 + (a * b); \n" +
//                "        end; \n" +
//                "for i:=1 to n do \n" +
//                "    a:=i; \n" +
//                "if ((90+30)/(7+5)> hj) then \n" +
//                "        k := a; \n" +
//                "        sd := 6;\n" +
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
