package ua.nure.veres.summaryTask.db;

import ua.nure.veres.summaryTask.db.entity.User;

import java.util.Locale;

/**
 * Status entity.
 */
public enum Status {
    NORMAL, STOPPED, BLOCKED;

    public static Status getStatus(User user) {
        int statusId = user.getStatusId();
        return Status.values()[statusId];
    }

    public String getName() {
        return name().toLowerCase(Locale.getDefault());
    }
}
