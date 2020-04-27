package ua.nure.veres.summaryTask.ee.security;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.PaymentManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.ee.security.SecurityManagerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class that provides methods of application security.
 */
public class SecurityManager {

    private static final long REFRESH_FREQUENCY_MS = 15000;

    private static final Map<String, Thread> refreshMap = new HashMap<>();

    private static final Map<String, User> loggedUsers = new HashMap<>();

    private static final String ALGORITHM_MD5 = "MD5";

    private static final Logger LOG = Logger.getLogger(SecurityManager.class);

    private static final String ATTRIBUTE_USER = "user";

    private static SecurityManager instance;

    static {
        BasicConfigurator.configure();
    }

    private SecurityManager() {
        //no op
    }

    /**
     * Gets logged user from loggedUsers.
     * @param login User login
     * @return Logged user
     */
    public User getLoggedUser(String login) {
        return loggedUsers.get(login);
    }

    /**
     * Puts logged user to loggedUsers
     * @param user User to put
     */
    public void putLoggedUser(User user) {
        LOG.debug("Start put logged user");
        LOG.trace(String.format("Logged user --> %s", user));

        if (user == null) {
            throw new IllegalArgumentException();
        }

        loggedUsers.put(user.getLogin(), user);
        LOG.info("Logged user was putted, total number of logged users --> " + loggedUsers.size());
        LOG.debug("End put logged user");
    }

    /**
     * Removes logged user out from loggedUser.
     * @param user Logged user to remove
     */
    public void removeLoggedUser(User user) {
        LOG.debug("Start remove logged user");
        LOG.trace(String.format("Logged user --> %s", user));

        if (user == null) {
            throw new IllegalArgumentException();
        }

        loggedUsers.remove(user.getLogin());
        LOG.info("Logged user was removed, total number of logged users --> " + loggedUsers.size());
        LOG.debug("End remove logged user");
    }

    /**
     * Does refreshing of user in loggedUsers.
     * @param user User to refresh
     */
    private void doRefresh(User user) throws AppException {
        Dao<User, Long> userDao = new UserDaoFactory().getDao();
        User dbUser = userDao.customReadOne("READ ONE login", user.getLogin());
        LOG.trace("Found in DB: user --> " + dbUser);

        if (dbUser != null) {
            PaymentManager paymentManager = PaymentManager.getInstance();
            paymentManager.checkEconomicStability(dbUser);
            loggedUsers.put(dbUser.getLogin(), dbUser);
        } else {
            loggedUsers.remove(user.getLogin());
        }
    }

    /**
     * Refreshes current session user by given request.
     */
    public boolean refreshUser(HttpServletRequest request) {
        LOG.debug("Start refresh user");
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute(ATTRIBUTE_USER);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, sessionUser));

        if (sessionUser == null) {
            return false;
        }

        String login = sessionUser.getLogin();
        if (!refreshMap.containsKey(login)) {
            startSecurityCheck(sessionUser);
        }
        User mapUser = getLoggedUser(sessionUser.getLogin());

        if (mapUser == null) {
            return false;
        }

        if (!sessionUser.equals(mapUser)) {
            session.setAttribute(ATTRIBUTE_USER, mapUser);
        }

        LOG.debug("End refresh user");
        return true;
    }


    /**
     * Comparing given password (which hashes to MD5) to password of User entity.
     *
     * @param password a password need to be compared.
     * @param user     an user whose password would be compared.
     * @return a password is equal to an user password or not.
     */
    public boolean checkPassword(String password, User user) throws SecurityManagerException {
        try {
            String hashedPassword = hash(password);
            return hashedPassword.equals(user.getPassword().toUpperCase(Locale.getDefault()));
        } catch (NoSuchAlgorithmException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SecurityManagerException(SecurityManagerException.MESSAGE_ALGORITHM_EXCEPTION);
        }
    }

    /**
     * Wrapper of hash method.
     *
     * @param string string need to be hashed
     * @return hashed string
     */
    public String hashString(String string) throws NoSuchAlgorithmException {
        return hash(string);
    }

    /**
     * Hashes given string with MD5 algorithm.
     *
     * @param input string need to be hashed.
     * @return hashed string.
     */
    private String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM_MD5);
        messageDigest.update(input.getBytes());
        byte[] bytes = messageDigest.digest();
        StringBuilder hashedString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = hexHashFormatter(Integer.toHexString(Integer.parseInt(Integer.toString(aByte, 2), 2)));
            hashedString.append(hex);
        }
        return hashedString.toString();
    }

    /**
     * Formats digit with 2-byte format.
     *
     * @param digit digit need to be formatted
     * @return formatted hashed bytes
     */
    private String hexHashFormatter(String digit) {
        if (digit.length() > 2) {
            final char c = Character.toUpperCase(digit.charAt(digit.length() - 2));
            return String.format("%s%s", c, Character.toUpperCase(digit.charAt(digit.length() - 1)));
        }
        if (digit.length() < 2) {
            return String.format("%d%s", 0, Character.toUpperCase(digit.charAt(0)));
        }
        return String.format("%s%s", Character.toUpperCase(digit.charAt(0)), Character.toUpperCase(digit.charAt(1)));
    }

    /**
     * Tries to refresh user.
     * @param user User to refresh
     */
    void tryToRefreshUser(User user) {
        try {
            doRefresh(user);
        } catch (AppException ex) {
            LOG.error(ex.getMessage(), ex);
            removeLoggedUser(user);
        }
    }

    /**
     * Starts SecurityWorker to check User.
     * @param user User to check
     */
    private void startSecurityCheck(User user) {
        Thread securityWorker = new SecurityWorker(user, REFRESH_FREQUENCY_MS);
        securityWorker.start();
        refreshMap.put(user.getLogin(), securityWorker);
    }

    /**
     * Stops SecurityWorker to check User.
     * @param user User to stop check
     */
    void stopSecurityCheck(User user) {
        Thread securityWorker = refreshMap.get(user.getLogin());
        securityWorker.interrupt();
        refreshMap.remove(user.getLogin());
    }

    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

}
