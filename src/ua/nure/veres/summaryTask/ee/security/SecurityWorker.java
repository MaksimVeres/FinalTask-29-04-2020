package ua.nure.veres.summaryTask.ee.security;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.entity.User;

/**
 * Security user session updater.
 */
class SecurityWorker extends Thread {

    private static final Logger LOG = Logger.getLogger(SecurityWorker.class);

    static {
        BasicConfigurator.configure();
    }

    private User user;
    private long frequencyTime;

    SecurityWorker(User user, long frequencyTime) {
        this.user = user;
        this.frequencyTime = frequencyTime;
    }

    @Override
    public void run() {

        if (user != null) {
            SecurityManager securityManager = SecurityManager.getInstance();
            while (true) {
                LOG.debug(String.format("Start updating user [%s]", user.getLogin()));
                User loggedUser = securityManager.getLoggedUser(user.getLogin());

                if (loggedUser == null) {
                    LOG.trace(String.format("User [%s] is logged out, stopping security checking", user.getLogin()));
                    securityManager.stopSecurityCheck(user);
                    interrupt();
                    break;
                }

                securityManager.tryToRefreshUser(user);
                LOG.trace(String.format(
                        "User [%s] was updated by security worker [%s]", user.getLogin(), getName())
                );

                LOG.debug(String.format("End updating user [%s]", user.getLogin()));

                try {
                    sleep(frequencyTime);
                } catch (InterruptedException ex) {
                    LOG.error(ex.getMessage(), ex);
                    interrupt();
                    return;
                }

            }
        }

    }
}
