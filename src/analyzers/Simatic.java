package analyzers;

import enums.Symbols;
import enums.TypesOfNumber;
import table.RowOfTable;
import table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PetroOV on 12/12/2015.
 */
public class Simatic {

    int funcGlobalBegin;
    int funcBegin;
    Map<String,Double> real = new HashMap<>();
    Map<String,Integer> integer = new HashMap<>();
    Table nodes;

    public Simatic(Table nodes) {
        this.nodes = nodes;
    }
    ArrayList<Integer> root = new ArrayList<>();
    ArrayList<Integer> idTypes = new ArrayList<>();
    public void siParse(){
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.getRow(i).getFunctional() == -1) {
                root.add(i);
            }
        }
        funcBegin = root.get(1);
        for (int i = 0; i < nodes.size(); i++) {
            if(nodes.getRow(i).getFunctional() == 0) {
                idTypes.add(i);
            }
        }
        for (int i = 0; i < idTypes.size(); i+=2) {
            Table temp = nodes.findByFunctional(idTypes.get(i));
            initilize(temp);
        }
        for(Map.Entry<String,Integer> m: integer.entrySet()){
            System.out.println(m.getKey() + "   " + m.getValue());
        }
    }

    void initilize(Table t){
        String type = t.getRow(t.size()-1).getKey();
        for (int i = 0; i < t.size() -1; i++) {
            String name = t.getRow(i).getKey();
            if (type.equals(TypesOfNumber.REAL.string))
                real.put(name, 0.0);
            else
                integer.put(name, 0);
        }

    }


    void ifCycle(String leftExp, String rightExp, String condition){
        if(condition.equals(Symbols.EQUAL)){

        }
    }
}
