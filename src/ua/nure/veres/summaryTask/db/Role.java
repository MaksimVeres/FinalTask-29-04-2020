package ua.nure.veres.summaryTask.db;

import ua.nure.veres.summaryTask.db.entity.User;

import java.util.Locale;

/**
 * Role entity.
 *
 * @author M.Veres
 */
public enum Role {
    ADMIN, CUSTOMER;

    public static Role getRole(User user) {
        int roleId = user.getRoleId();
        return Role.values()[roleId];
    }

    public String getName() {
        return name().toLowerCase(Locale.getDefault());
    }
}
