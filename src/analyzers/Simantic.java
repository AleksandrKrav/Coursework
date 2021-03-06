package analyzers;

import enums.Symbols;
//import enums.TypesOfNumber;
import table.RowOfTable;
import table.Table;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simantic {
    int funcGlobalBegin;
    int funcBegin;
    Map<String, Double> real = new HashMap<>();
    Map<String, Integer> integer = new HashMap<>();
    Map<String, Integer> local = new HashMap<>();
    Table nodes;
    Table nodes2;
    ArrayList<Integer> root = new ArrayList<>();
    final ArrayList<RowOfTable> tree = new ArrayList<>();
    ArrayList<Integer> idVarTypes = new ArrayList<>();
    ArrayList<Integer> idBeginTypes = new ArrayList<>();
    int blockEnd;

    public int getFuncBegin() {
        return funcBegin;
    }

    //    int parentId;
    Pattern pattern = Pattern.compile("[a-zA-Z]+");
    Matcher matcher;

    public Simantic(Table nodes, Table nodes2) {
        this.nodes = nodes;
        this.nodes2 = nodes2;
    }

    public void intialization() {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.getRow(i).getFunctional() == -1) {
                root.add(i);
            }
            RowOfTable rot = new RowOfTable(nodes.getRow(i).getId(), nodes.getRow(i).getKey(), nodes.getRow(i).getFunctional() );
            tree.add(rot);
        }

        funcBegin = root.get(1);
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.getRow(i).getFunctional() == 0) {
                idVarTypes.add(i);
            }
            if (nodes.getRow(i).getFunctional() == funcBegin) {
                idBeginTypes.add(i);
            }
        }
        for (int i = 0; i < idVarTypes.size(); i += 2) {
            Table temp = nodes.findByFunctional(idVarTypes.get(i));
            initilize(temp);
        }
    }

    public void simanticParse(int subParentId) {

        idBeginTypes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.getRow(i).getFunctional() == subParentId) {
                idBeginTypes.add(i);
            }
        }
        final ArrayList<RowOfTable> r = new ArrayList<>();
        for (int i = 0; i < nodes2.size(); i++) {
            r.add(new RowOfTable(nodes2.getRow(i).getId(), nodes2.getRow(i).getKey(), nodes2.getRow(i).getFunctional()));
        }
        final Table t2 = new Table(r);
//        t2 = nodes;
        for (int i = 0; i < idBeginTypes.size(); i += 2) {
            int d = subParentId;
            String k = "";
            boolean b = false;

            if (nodes.findById(idBeginTypes.get(i)).getRow(0).getKey().equals("while")) {
                int whileId = idBeginTypes.get(i);
                d = idBeginTypes.get(i + 1);
                while (b = parseContition(nodes.findByFunctional(whileId).getRow(0))) {
                    final ArrayList<RowOfTable> f = new ArrayList<>();
                    for (int l = 0; l < nodes2.size(); l++) {
                        f.add(new RowOfTable(t2.getRow(l).getId(), t2.getRow(l).getKey(), t2.getRow(l).getFunctional()));
                    }

                    nodes = new Table(f);
//                    System.out.println(b);

                    if (nodes.findById(d + 1).getRow(0).getKey().equals("begin")) {
//                        for (int j = 0; j < nodes.findByFunctional(idBeginTypes.get(d) + 1).size(); j += 2) {
                        simanticParse(d + 1);
//                            k = parseTable2(nodes.findByFunctional(idBeginTypes.get(d) + 1).getRow(j), idBeginTypes.get(d) + 1);
//                        }
                    } else {
                        parseTable2(nodes.findByFunctional(d).getRow(0), d);
                    }
//                    for (Map.Entry<String, Integer> m : integer.entrySet()) {
//                        System.out.println(m.getKey() + " " + m.getValue());
//                    }

                }
            } else if (nodes.findById(idBeginTypes.get(i)).getRow(0).getKey().equals("if")) {
                b = parseContition(nodes.findByFunctional(idBeginTypes.get(i)).getRow(0));
                System.out.println(b);
                d = i + 1;
                if (b) {
                    if (nodes.findById(idBeginTypes.get(d) + 1).getRow(0).getKey().equals("begin")) {
//                        for (int j = 0; j < nodes.findByFunctional(idBeginTypes.get(d) + 1).size(); j += 2) {
                        simanticParse(d + 1);
//                            k = parseTable2(nodes.findByFunctional(idBeginTypes.get(d) + 1).getRow(j), idBeginTypes.get(d) + 1);
//                        }
                    } else {
                        parseTable2(nodes.findByFunctional(idBeginTypes.get(d)).getRow(0), idBeginTypes.get(d));
                    }
                }
                if (idBeginTypes.size() > (i + 2)) {
                    if (nodes.findById(idBeginTypes.get(i + 2)).getRow(0).getKey().equals("else")) {
                        d = i + 2;
                        i += 1;
                        if (!b) {
                            if (nodes.findById(idBeginTypes.get(d) + 1).getRow(0).getKey().equals("begin")) {
                                for (int j = 0; j < nodes.findByFunctional(idBeginTypes.get(d) + 1).size(); j += 2) {
                                    simanticParse(d + 1);
//                                    k = parseTable2(nodes.findByFunctional(idBeginTypes.get(d) + 1).getRow(j), idBeginTypes.get(d) + 1);
                                }
                            } else {
                                parseTable2(nodes.findByFunctional(idBeginTypes.get(d)).getRow(0), idBeginTypes.get(d));
                            }
                        }
                    }
                }
            } else if (nodes.findById(idBeginTypes.get(i)).getRow(0).getKey().equals("for")) {
                Table tFor = nodes.findByFunctional(idBeginTypes.get(i));
                String[] cond = parseFor(tFor.getRow(0));
                String start = cond[1];
                local.put(cond[0], Integer.valueOf(cond[1]));
                int idTo = idBeginTypes.get(i + 1);
                RowOfTable row = nodes.findByFunctional(idTo).getRow(0);
                String finish = parseTo(row);
                int idDO = idBeginTypes.get(i + 2);
                i += 2;
                for (int j = Integer.parseInt(start); j < Integer.parseInt(finish) + 1; j++) {
                    local.put(cond[0], j);
                    final ArrayList<RowOfTable> f = new ArrayList<>();
                    for (int l = 0; l < nodes2.size(); l++) {
                        f.add(new RowOfTable(t2.getRow(l).getId(), t2.getRow(l).getKey(), t2.getRow(l).getFunctional()));
                    }
                    nodes = new Table(f);
                    d = idDO;
                    if (nodes.findById(d + 1).getRow(0).getKey().equals("begin")) {
//                        for (int l = 0; l < nodes.findByFunctional(d + 1).size(); l += 2) {
                            simanticParse(d + 1);
//                            k = parseTable2(nodes.findByFunctional(d + 1).getRow(l), d + 1);
//                        }
                    } else {
                        parseTable2(nodes.findByFunctional(d).getRow(0), d);
                    }
                }
                local.remove(cond[0]);
            } else if (nodes.findById(idBeginTypes.get(i)).getRow(0).getKey().equals(":=")) {
                k = parseTable2(nodes.findById(idBeginTypes.get(i)).getRow(0), subParentId);
            } else if (nodes.findById(idBeginTypes.get(i)).getRow(0).getKey().equals("writeln")) {
                System.out.println(parseValue(nodes.findByFunctional(idBeginTypes.get(i)).getRow(0).getKey()));
                i -= 1;
            } else {
                return;
            }

//                System.out.println(k);
        }
        for (Map.Entry<String, Integer> m : integer.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
        for (Map.Entry<String, Double> m : real.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }

    void initilize(Table t) {
        String type = t.getRow(t.size() - 1).getKey();
        for (int i = 0; i < t.size() - 1; i++) {
            String name = t.getRow(i).getKey();
            if (type.equals("real"))
                real.put(name, 0.0);
            else
                integer.put(name, 0);
        }
    }

    void logic(RowOfTable table, RowOfTable parent) {

    }

    String[] parseFor(RowOfTable rowFor) {
        String[] result = new String[2];
        Table tFor = nodes.findByFunctional(rowFor.getId());
        result[0] = tFor.getRow(0).getKey();
        result[1] = parseValue(tFor.getRow(1).getKey());
        return result;
    }

    String parseTo(RowOfTable rowFor) {
//        Table tFor = nodes.findByFunctional(rowFor.getId());
        return parseValue(rowFor.getKey());
    }

    boolean parseContition(RowOfTable conditionRow) {
        boolean result = false;
        String condition = conditionRow.getKey();
        Table t = nodes.findByFunctional(conditionRow.getId());
        String left = parseTable2(t.getRow(0), conditionRow.getId());
        String right = parseTable2(t.getRow(1), conditionRow.getId());
        Double leftValue = Double.parseDouble(parseValue(left));
        Double rightValue = Double.parseDouble(parseValue(right));
        if (condition.equals(Symbols.LESS.string)) {
            result = leftValue < rightValue;
        } else if (condition.equals(Symbols.MORE.string)) {
            result = leftValue > rightValue;
        } else if (condition.equals(Symbols.MORE_EQUAL.string)) {
            result = leftValue >= rightValue;
        } else if (condition.equals(Symbols.LESS_EQUAL.string)) {
            result = leftValue <= rightValue;
        } else if (condition.equals((Symbols.NOT_EQUAL))) {
            result = leftValue != rightValue;
        }
        return result;
    }

    String parseValue(String value) {
        String leftName;
        Number leftValue = null;
        matcher = pattern.matcher(value);
        if (matcher.matches()) {
            if (integer.containsKey(value)) { // есть в мапе интежеров
                leftName = value;
                leftValue = integer.get(leftName);
            } else if (local.containsKey(value)) {
                leftName = value;
                leftValue = local.get(leftName);
            } else if (real.containsKey(value)) { // есть в мапе real
                leftName = value;
                leftValue = real.get(leftName);
            } else {
                System.out.println("невідома змінна " + value);
                System.exit(-1);
            }
        } else {
            if (value.contains(".")) {
                leftValue = Double.parseDouble(value);
            } else {
                leftValue = Integer.parseInt(value);
            }
        }
        return leftValue.toString();
    }

    String parseTable2(RowOfTable table, int finish) {
        Table t = nodes.findByFunctional(table.getId());
        RowOfTable parent;
        if (t.size() > 0) {
            if (nodes.findByFunctional(t.getRow(0).getId()).size() > 0) {
                parseTable2(t.getRow(0), finish);
            } else if (nodes.findByFunctional(t.getRow(1).getId()).size() > 0) {
                parseTable2(t.getRow(1), finish);
            } else parseTable2(t.getRow(0), finish);
        } else if (table.getFunctional() == finish) {
            return table.getKey();
        } else {
            RowOfTable childTable = nodes.findById(table.getFunctional()).getRow(0);
            Table childList = nodes.findByFunctional(childTable.getId());
            String s;
            if (childList.getRow(0) == table) {
                if (nodes.findByFunctional(childList.getRow(1).getId()).size() > 0) {
                    parseTable2(childTable, finish);
                    return childTable.getKey();
                }
                s = mathOperation(table, nodes.findByFunctional(table.getFunctional()).getRow(1), nodes.findById(table.getFunctional()).getRow(0).getKey());
            } else {
                s = mathOperation(nodes.findByFunctional(table.getFunctional()).getRow(0), table, nodes.findById(table.getFunctional()).getRow(0).getKey());
            }
            parent = nodes.findById(table.getFunctional()).getRow(0);
            parent.setKey(s);
            int childParent = table.getFunctional();
            nodes.removeRow(nodes.findByFunctional(childParent).getRow(0).getId());
            nodes.removeRow(nodes.findByFunctional(childParent).getRow(0).getId());

            parseTable2(parent, finish);

        }
        return table.getKey();
    }

    //функция для проверки ли это листья
    String parseTable(RowOfTable table) {
        Table t = nodes.findByFunctional(table.getId());
        RowOfTable parent;
        if (t.size() > 0) {
            if (nodes.findByFunctional(t.getRow(0).getId()).size() > 0) {
                parseTable(t.getRow(0));
            } else if (nodes.findByFunctional(t.getRow(1).getId()).size() > 0) {
                parseTable(t.getRow(1));
            } else parseTable(t.getRow(0));
        } else if (table.getFunctional() == funcBegin) {
            return table.getKey();
        } else {
            RowOfTable childTable = nodes.findById(table.getFunctional()).getRow(0);
            Table childList = nodes.findByFunctional(childTable.getId());
            String s;
            if (childList.getRow(0) == table) {
                if (nodes.findByFunctional(childList.getRow(1).getId()).size() > 0) {
                    parseTable(childTable);
                    return childTable.getKey();
                }
                s = mathOperation(table, nodes.findByFunctional(table.getFunctional()).getRow(1), nodes.findById(table.getFunctional()).getRow(0).getKey());
            } else {
                s = mathOperation(nodes.findByFunctional(table.getFunctional()).getRow(0), table, nodes.findById(table.getFunctional()).getRow(0).getKey());
            }
            parent = nodes.findById(table.getFunctional()).getRow(0);
            parent.setKey(s);
            int childParent = table.getFunctional();
            nodes.removeRow(nodes.findByFunctional(childParent).getRow(0).getId());
            nodes.removeRow(nodes.findByFunctional(childParent).getRow(0).getId());

            parseTable(parent);

        }
        return table.getKey();
    }

    String mathOperation(RowOfTable left, RowOfTable right, String key) {
        String result = null;
        Number leftValue = 0;
        Number rightValue = 0;
        String leftName = "";
        String rightName;

        matcher = pattern.matcher(left.getKey());
        if (matcher.matches()) {
            if (integer.containsKey(left.getKey())) { // есть в мапе интежеров
                leftName = left.getKey();
                leftValue = integer.get(leftName);
            } else if (local.containsKey(left.getKey())) {
                leftName = left.getKey();
                leftValue = local.get(leftName);
            } else if (real.containsKey(left.getKey())) { // есть в мапе real
                leftName = left.getKey();
                leftValue = real.get(leftName);
            } else {
                leftName = left.getKey();
                System.out.println("Невідома змінна \"" + leftName + "\"");
            }
        } else {
            if (left.getKey().contains(".")) {
                leftValue = Double.parseDouble(left.getKey());
            } else {
                leftValue = Integer.parseInt(left.getKey());
            }
        }
        matcher = pattern.matcher(right.getKey());
        if (matcher.matches()) {
            if (integer.containsKey(right.getKey())) { // есть в мапе интежеров
                rightName = right.getKey();
                rightValue = integer.get(rightName);
            } else if (local.containsKey(right.getKey())) {
                rightName = right.getKey();
                rightValue = local.get(rightName);
            } else if (real.containsKey(right.getKey())) { // есть в мапе real
                rightName = right.getKey();
                rightValue = real.get(rightName);
            } else {
                rightName = right.getKey();
                System.out.println("Невідома змінна - \"" + rightName + "\"");
            }
        } else {
            if (right.getKey().contains(".")) {
                rightValue = Double.parseDouble(right.getKey());
            } else {
                rightValue = Integer.parseInt(right.getKey());
            }
        }

        if (key.equals(":=") && (!leftName.equals(""))) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = rightValue;
                integer.put(leftName, (Integer) leftValue);
                result = leftValue.toString();
                return result;
            } else if ((leftValue instanceof Double) && (rightValue instanceof Double)) {
                leftValue = rightValue;
                real.put(leftName, (Double) leftValue);
                result = leftValue.toString();
                return result;
            } else {
                System.out.println("Невірне приведення типів ");
                System.exit(-1);
            }
        } else if (key.equals("+")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue + (Integer) rightValue;
                result = leftValue.toString();
                return result;
            } else if ((leftValue instanceof Double) && (rightValue instanceof Double)) {
                leftValue = (Double) leftValue + (Double) rightValue;
                result = leftValue.toString();
                return result;
            } else {
                leftValue = Double.parseDouble(leftValue.toString()) + Double.parseDouble(rightValue.toString());
                result = leftValue.toString();
                return result;
            }
        } else if (key.equals("*")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue * (Integer) rightValue;
                result = leftValue.toString();
                return result;
            } else {
                leftValue = Double.parseDouble(leftValue.toString()) * Double.parseDouble(rightValue.toString());
                result = leftValue.toString();
                return result;
            }
        } else if (key.equals("/")) {
            leftValue = Double.valueOf(leftValue.toString()) / Double.valueOf(rightValue.toString());
            result = leftValue.toString();
            return result;
        }else if (key.equals("mod")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue % (Integer) rightValue;
                result = leftValue.toString();
                return result;
            }else{
                System.out.println("Невірний тип даних для виконная операції mod");
                System.exit(-1);
            }
        }
        return result;
    }


    public void printResult(String fileName) {
        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        String s;
        for (int i = 0; i < tree.size(); i++) {
            s = tree.get(i).getKey();
            if (s.equals(":")) {
                sb.append("тип " + " ");
            } else if (s.equals("integer")) {
                sb.append("integer ");
            } else if (s.equals(";")) {
                sb.append("\n");
            } else if (s.equals("real")) {
                sb.append("real ");
            } else if (s.equals("begin")) {
                if (tree.get(i).getFunctional() == -1) {
                    sb.append("Початок програми" + "\n");
                } else sb.append("Початок блоку" + "\n");
            } else if (s.equals("end")) {
                if (tree.get(i).getFunctional() == -1) {
                    sb.append("Кінець програми" + "\n");
                } else sb.append("Кінець блоку");
            } else if (s.equals("writeln")){
                sb.append("Вивід на екран: " + parseValue(tree.get(i+1).getKey()) + "\n");
                i++;
            } else if (s.equals(Symbols.ASSIGN.string)){
                sb.append("присвоїти ");
            } else if (s.equals("+")){
                sb.append("додати ");
            } else if (s.equals("-")){
                sb.append("відняти ");
            } else if (s.equals("/")){
                sb.append("поділити ");
            } else if (s.equals("*")){
                sb.append("помножити ");
            } else if (s.equals("=")){
                sb.append("дорівнює ");
            } else if (s.equals(">")){
                sb.append("більше ");
            }  else if (s.equals("<")){
                sb.append("менше ");
            } else if (s.equals(">=")){
                sb.append("більше або дорівнює ");
            } else if (s.equals("<=")){
                sb.append("менше або дорівнює ");
            } else if (s.equals("<>")){
                sb.append("не дорівнює ");
            } else if (s.equals("if")){
                sb.append("Якщо ");
            } else if (s.equals("then")){
                sb.append("то \n\t");
            } else if (s.equals("else")){
                sb.append("інакше \n");
            } else if (s.equals("for")){
                sb.append("від ");
            } else if (s.equals("to")){
                sb.append("до ");
            } else if (s.equals("do")){
                sb.append("виконати \n");
            } else if (s.equals("while")){
                sb.append("доки ");
            } else if (s.equals("readln")){
                sb.append("зчитати з консолі змінну : ");
            } else if(s.equals(".")) {
                continue;
            } else if(s.equals("var")) {
                sb.append("Змінні : \n");
            }else{
             sb.append(s + " ");}
        }


        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                out.print(sb.toString());
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}