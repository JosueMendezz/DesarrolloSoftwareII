package model.entities;

public class Customer {

    private String id;
    private String name;
    private boolean preferential;

    public Customer() {
    }

    public Customer(String id, String name, boolean preferential) {
        this.id = id;
        this.name = name;
        this.preferential = preferential;
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

    public boolean isPreferential() {
        return preferential;
    }

    public void setPreferential(boolean preferential) {
        this.preferential = preferential;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + ", preferential=" + preferential + "]";
    }
}
