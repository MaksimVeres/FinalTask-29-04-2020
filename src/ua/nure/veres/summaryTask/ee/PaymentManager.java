package ua.nure.veres.summaryTask.ee;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.Status;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

/**
 * Payment manager class, checks user debts and do payment for it.
 */
public class PaymentManager {

    private static final Logger LOG = Logger.getLogger(PaymentManager.class);

    private static PaymentManager instance;

    static {
        BasicConfigurator.configure();
    }

    private PaymentManager() {
        //no op
    }

    /**
     * Changes user status to chosen one.
     *
     * @param user     User entity
     * @param statusId Status id
     * @param userDao  User DAO
     */
    private void changeUserStatus(User user, int statusId, Dao<User, Long> userDao) throws AppException {
        user.setStatusId(statusId);
        userDao.customUpdate(user, "UPDATE statusId"
                , user.getStatusId());
        LOG.info(String.format("Status of user with login [%s] was changed to %s"
                , user.getLogin(), Status.getStatus(user))
        );
    }

    /**
     * Obtains total month payment for user services for now.
     *
     * @param userServices   List of database user services entities
     * @param userServiceDao UserService DAO
     * @return total payment for services
     */
    private double obtainTotalPayment(List<UserService> userServices, Dao<UserService, Long> userServiceDao) throws AppException {
        double totalPayment = 0;

        for (UserService userService : userServices) {
            java.sql.Date date = new java.sql.Date(userService.getLastPaymentDate().getTime());
            LocalDate localDate = date.toLocalDate();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            LocalDate currentLocalDate = currentDate.toLocalDate();
            Period period = Period.between(localDate, currentLocalDate);
            long totalMonth = period.toTotalMonths();

            if (totalMonth > 0) {
                DaoFactory daoFactory = new TariffDaoFactory();
                Dao<Tariff, Long> tariffDao = daoFactory.getDao();

                Tariff tariff = tariffDao.read(userService.getTariffId());
                LOG.trace("Found in DB: tariff --> " + tariff);

                userService.setLastPaymentDate(new java.sql.Date(System.currentTimeMillis()));
                userServiceDao.update(userService);
                LOG.info(String.format("Last payment date of user service with id [%s] was changed to %s"
                        , userService.getId(), userService.getLastPaymentDate())
                );

                double payment = tariff.getMonthPayment();
                totalPayment += payment * totalMonth;
            }
        }
        return totalPayment;
    }

    /**
     * Checks total debts of user,
     * does payment for it or blocks user if account state is below 0.
     *
     * @param user User entity
     */
    public void checkEconomicStability(User user) throws AppException {
        LOG.debug("Start check economic stability");
        LOG.trace("User --> " + user);

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        Status userStatus = Status.getStatus(user);
        LOG.info("User status --> " + userStatus.toString().toLowerCase(Locale.getDefault()));

        DaoFactory daoFactory = new UserDaoFactory();
        Dao<User, Long> userDao = daoFactory.getDao();

        if (user.getAccountState() < 0) {
            userStatus = Status.BLOCKED;
            changeUserStatus(user, userStatus.ordinal(), userDao);
        }

        if (userStatus == Status.BLOCKED && user.getAccountState() >= 0) {
            user.setStatusId(Status.NORMAL.ordinal());
            userStatus = Status.getStatus(user);
            userDao.customUpdate(user, "UPDATE statusId"
                    , user.getStatusId());
            LOG.info(String.format("Status of user with login [%s] was changed to %s"
                    , user.getLogin(), userStatus)
            );
        }

        if (userStatus != Status.BLOCKED && userStatus != Status.STOPPED) {
            double accountState = user.getAccountState();

            daoFactory = new UserServiceDaoFactory();
            Dao<UserService, Long> userServiceDao = daoFactory.getDao();

            List<UserService> userServices = userServiceDao.customReadMany(
                    "READ MANY User.login", user.getLogin()
            );
            LOG.trace(String.format("Found in DB: services of user [%s], size --> %d"
                    , user.getLogin(), userServices.size())
            );

            double totalPayment = obtainTotalPayment(userServices, userServiceDao);
            LOG.info(String.format("Total payment for user [%s] --> %s", user.getLogin(), totalPayment));

            accountState = accountState - totalPayment;

            if (accountState < 0) {
                changeUserStatus(user, Status.BLOCKED.ordinal(), userDao);
            }

            User dbUser = userDao.customReadOne("READ ONE login", user.getLogin());
            LOG.trace("Found in DB: user --> " + dbUser);

            dbUser.setAccountState(accountState);
            userDao.update(dbUser);
            LOG.info(String.format(
                    "Account of user with login [%s] was decreased with an amount of --> %s, new account state --> %s"
                    , dbUser.getLogin(), totalPayment, dbUser.getAccountState()
            ));

            LOG.debug("End check economic stability");
        }
    }

    public static synchronized PaymentManager getInstance() {
        if (instance == null) {
            instance = new PaymentManager();
        }
        return instance;
    }
}
