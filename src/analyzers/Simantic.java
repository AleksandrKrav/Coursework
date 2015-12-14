package analyzers;

import com.sun.rowset.internal.Row;
import enums.Symbols;
import enums.TypesOfNumber;
import table.RowOfTable;
import table.Table;

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
    Table nodes;

    public Simantic(Table nodes) {
        this.nodes = nodes;
    }

    ArrayList<Integer> root = new ArrayList<>();
    ArrayList<Integer> idVarTypes = new ArrayList<>();
    ArrayList<Integer> idBeginTypes = new ArrayList<>();

    int blockEnd = 10;

    public void simanticParse() {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.getRow(i).getFunctional() == -1) {
                root.add(i);
            }
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
        for (int i = 0; i < idBeginTypes.size(); i += 2) {
            Table t = nodes.findByFunctional(idBeginTypes.get(i));
//            blockEnd = idBeginTypes.get(i + 1);
            parseTable(nodes.findById(idBeginTypes.get(i)).getRow(0));
        }


        for (int i = 0; i < idVarTypes.size(); i++) {

        }
        for (Map.Entry<String, Integer> m : integer.entrySet()) {
            System.out.println(m.getKey() + "   " + m.getValue());
        }
        for (Map.Entry<String, Double> m : real.entrySet()) {
            System.out.println(m.getKey() + "   " + m.getValue());
        }
    }

    void initilize(Table t) {
        String type = t.getRow(t.size() - 1).getKey();
        for (int i = 0; i < t.size() - 1; i++) {
            String name = t.getRow(i).getKey();
            if (type.equals(TypesOfNumber.REAL.string))
                real.put(name, 0.0);
            else
                integer.put(name, 0);
        }
    }

    int parentId;

    void logic(RowOfTable table, RowOfTable parent){

    }

    //функция для проверки ли это листья
    void parseTable(RowOfTable table) {
        Table t = nodes.findByFunctional(table.getId());
        RowOfTable parent;
        if (t.size() > 0) {
            if(nodes.findByFunctional(t.getRow(0).getId()).size() > 0){
                parseTable(t.getRow(0));
            } else if(nodes.findByFunctional(t.getRow(1).getId()).size() > 0) {
                parseTable(t.getRow(1));
            }
            else parseTable(t.getRow(0));
        } else if (table.getFunctional() == funcBegin) {
            return;
        } else {
            RowOfTable childTable = nodes.findById(table.getFunctional()).getRow(0);
            Table childList = nodes.findByFunctional(childTable.getId());
            String s;
            if (childList.getRow(0) == table){
                 s = mathOperation(table, nodes.findByFunctional(table.getFunctional()).getRow(1), nodes.findById(table.getFunctional()).getRow(0).getKey());
            }else {
                 s = mathOperation(nodes.findByFunctional(table.getFunctional()).getRow(0), table, nodes.findById(table.getFunctional()).getRow(0).getKey());
            }
            parent =  nodes.findById(table.getFunctional()).getRow(0);
            parent.setKey(s);
            int childParent = table.getFunctional();
            nodes.removeRow(nodes.findByFunctional(childParent).getRow(0).getId());
            nodes.removeRow(nodes.findByFunctional(childParent).getRow(0).getId());

            parseTable(parent);
        }

//        RowOfTable left = t.getRow(0);
//        RowOfTable right = t.getRow(1);
//

//        Table leftTable = nodes.findByFunctional(left.getId());
//        while (leftTable.size() != 0) {
//            parseTable(leftTable, left.getKey());
//        }
//
//        Table rightTable = nodes.findByFunctional(right.getId());
//        while (rightTable.size() != 0) {
//            parseTable(rightTable, right.getKey());
//        }
//
//
////        parentId = left.getFunctional();
//        String newParent = mathOperation(left, right, key);
//        Table ta = nodes.findById(parentId);
//        int parentFunctional = ta.getRow(0).getFunctional();
//        RowOfTable newRow = new RowOfTable(parentId, newParent, parentFunctional);
//        nodes.updateRow(parentId, newRow);
//        nodes.removeRow(left.getId());
//        nodes.removeRow(right.getId());

//        return;
//        parentId = newRow.getFunctional();
//        if (blockEnd == right.getId()){
//            return;
//        }
//        Table t1 = nodes.findById(parentId);
//        if (parentId == -1){
//            return;
//        }
//        if (t1.getRow(0).getKey().equals("begin")){
//            return;
//        }
//        Table temp = nodes.findByFunctional(parentId);
//        parseTable(temp, t1.getRow(0).getKey());


    }

    Pattern pattern = Pattern.compile("[a-zA-Z]");
    Matcher matcher;

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
            } else if (real.containsKey(left.getKey())) { // есть в мапе real
                leftName = left.getKey();
                leftValue = real.get(leftName);
            } else {
//                System.exit(-1);
                System.out.println("problem in matcher");
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
            } else if (real.containsKey(right.getKey())) { // есть в мапе real
                rightName = right.getKey();
                rightValue = real.get(rightName);
            } else {
                System.exit(-1);
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
            }
        } else if (key.equals("+")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue + (Integer) rightValue;
//                integer.put(leftName, (Integer) leftValue);
                result = leftValue.toString();
                return result;
            } else if ((leftValue instanceof Double) && (rightValue instanceof Double)) {
                leftValue = (Double) leftValue + (Double) rightValue;
//                real.put(leftName, (Double) leftValue);
                result = leftValue.toString();
                return result;
            }
        }
        return result;
    }


    void ifCycle(String leftExp, String rightExp, String condition) {
        if (condition.equals(Symbols.EQUAL)) {

        }
    }
}
