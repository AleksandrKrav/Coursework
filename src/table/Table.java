package table;

import com.sun.rowset.internal.Row;

import java.util.ArrayList;

public class Table {

    private RowOfTable[] table;

    public Table() {
        table = new RowOfTable[0];
    }

    public Table(RowOfTable[] table) {
        this.table = table;
    }

    public Table(ArrayList table) {
        this.table = new RowOfTable[table.size()];
        for (int i = 0; i < table.size(); i++) {
            this.table[i] = (RowOfTable) table.get(i);
        }
    }

    public int size() {
        return table.length;
    }

    public RowOfTable getRow(int id) {
        return table[id];
    }

    public void addRow(RowOfTable row) {
        RowOfTable[] temp = new RowOfTable[table.length + 1];
        for (int i = 0; i < table.length; i++) {
            temp[i] = table[i];
        }
        temp[table.length] = row;
        table = temp;
    }

    public void removeRow(int id) {
        RowOfTable[] temp = new RowOfTable[table.length - 1];
        int k = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i].getId() != id) {
                temp[k] = table[i];
                k++;
            }
        }
        table = new RowOfTable[temp.length];
        table = temp;
    }

    public void updateRow(int id, RowOfTable row) {
        for (int i = 0; i < table.length; i++) {
            if (table[i].getId() == id) {
                table[i] = row;
            }
        }
    }

    public Table findById(int id) {
        ArrayList<RowOfTable> temp = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (table[i].getId() == id) {
                temp.add(table[i]);
            }
        }
        return new Table(temp);
    }

    public Table findByKey(String key) {
        System.out.println("Find by key : " + key);
        ArrayList<RowOfTable> temp = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (table[i].getKey().equals(key)) {
                temp.add(table[i]);
            }
        }
        return new Table(temp);
    }


    public Table findByFunctional(int functional) {
//        System.out.println("Find by functional : " + functional);
        ArrayList<RowOfTable> temp = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (table[i].getFunctional() == functional) {
                temp.add(table[i]);
            }
        }
        return new Table(temp);
    }

    public Table findByFirstLetters(String str) {
        System.out.println("Find by first letters : " + str);
        ArrayList<RowOfTable> temp = new ArrayList<>();
        boolean isFound = false;
        while (!str.equals("")) {
            int k = -1;
            for (int i = 0; i < table.length; i++) {
                k = table[i].getKey().toLowerCase().indexOf(str.toLowerCase());
                if (k != -1) {
                    temp.add(table[i]);
                    isFound = true;
                }
            }
            if (!isFound) {
                str = str.substring(0, str.length() - 1);
            } else {
                break;
            }
        }
        System.out.println("Found a match for the letters: " + str);
        return new Table(temp);
    }

    @Override
    public String toString() {
        if (table.length == 0) {
            return "No matches found\n";
        }
        if (table.length == 1) {
            return table[0].toString() + "\n";
        }
        String str = "{";
        for (int i = 0; i < table.length; i++) {
            if (i != (table.length - 1)) {
                str += table[i].toString() + ", ";
            } else {
                str += table[i].toString();
            }
        }
        str+="}\n";
        return str;
    }
}
