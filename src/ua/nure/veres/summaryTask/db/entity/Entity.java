package ua.nure.veres.summaryTask.db.entity;

import java.io.Serializable;

/**
 * Root of all entities which have id field.
 */
public abstract class Entity implements Serializable, Comparable<Entity> {

    private static final long serialVersionUID = 642875491361367L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Entity o) {
        long compare = id - o.id;
        if (compare > 0) {
            return 1;
        } else if (compare == 0) {
            return 0;
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() == obj.getClass()) {
            return getId().equals(((Entity) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 37 * getId().hashCode();
    }
}
