package ua.nure.veres.summaryTask.db.entity;

/**
 * Service entity.
 */
public class Service extends Entity {

    private static final long serialVersionUID = -433378197553L;

    private String name;

    public Service() {
        //no op
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return 37 * name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() == obj.getClass()) {
            return ((Service) obj).name.equals(name);
        }
        return false;
    }
}
