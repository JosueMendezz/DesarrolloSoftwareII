package model.entities;

public class Customer {

    private String id;
    private String name;
    private boolean needsPreferentialSpace;

    public Customer(String id, String name, boolean needsPreferentialSpace) {
        this.id = id;
        this.name = name;
        this.needsPreferentialSpace = needsPreferentialSpace;
    }

    public Customer() {
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNeedsPreferentialSpace() {
        return needsPreferentialSpace;
    }

    public void setNeedsPreferentialSpace(boolean needsPreferentialSpace) {
        this.needsPreferentialSpace = needsPreferentialSpace;
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", name=" + name + ", needsPreferentialSpace=" + needsPreferentialSpace + '}';
    }

}
