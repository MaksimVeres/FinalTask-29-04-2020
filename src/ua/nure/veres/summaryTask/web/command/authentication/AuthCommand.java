package ua.nure.veres.summaryTask.web.command.authentication;

import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.web.command.Command;

/**
 * Abstract auth command.
 */
abstract class AuthCommand extends Command {

    static final String ATTRIBUTE_USER = "user";

    transient SecurityManager securityManager;

    AuthCommand(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    /**
     * Checks if same user already authorized.
     *
     * @param login of user
     */
    boolean checkAuthorized(String login) {
        return securityManager.getLoggedUser(login) != null;
    }

    /**
     * Sets logged user to SecurityManager map.
     *
     * @param user Logged user
     */
    void setLoggedUser(User user) {
        securityManager.putLoggedUser(user);
    }

    /**
     * Removes logged user out from SecurityManager loggedUsers map.
     *
     * @param user User to remove
     */
    void removeLoggedUser(User user) {
        securityManager.removeLoggedUser(user);
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}
