package ua.nure.veres.summaryTask.exception.user;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on user update error.
 */
public class UserUpdateException extends AppException {

    private static final long serialVersionUID = 999123656758456L;

    public static final String MESSAGE_CANNOT_DO_USER_UPDATE = "Cannot do user update";

    public static final String MESSAGE_OLD_PASSWORD_DO_NOT_MUCH
            = "Cannot do user update: old password do not much to the entered one";

    public static final String MESSAGE_NOT_ENOUGH_MONEY_ON_ACCOUNT
            = "Cannot do user update: not enough money on account to do update";

    public static final String MESSAGE_USER_IS_BLOCKED = "Cannot do user update: user is blocked";

    public static final String MESSAGE_NO_SUCH_USER_IN_DB = "Cannot do user update: no such user in db";

    public UserUpdateException(String message) {
        super(message);
    }
}
