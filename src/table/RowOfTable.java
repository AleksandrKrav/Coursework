package table;


import java.util.ArrayList;

public class RowOfTable {

    private int id;
    private String key;
    private int functional;
    private ArrayList<Integer> leftChilds;
    private ArrayList<Integer> rightChilds;

    public RowOfTable(int id, String key, int functional) {
        this.id = id;
        this.key = key;
        this.functional = functional;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getFunctional() {
        return functional;
    }

    public void setParent(int functional) {
        this.functional = functional;
    }

    public void addLeftChild(int id) {
        leftChilds.add(id);
    }

    public void addRightChild(int id) {
        rightChilds.add(id);
    }

    @Override
    public String toString() {
        return ("{id:" + id + " key:\"" + key + "\" parentId:" + functional + "}");
//        return (key);
    }
}
