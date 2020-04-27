package ua.nure.veres.summaryTask.web.command;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.ee.PaymentManager;
import ua.nure.veres.summaryTask.ee.data.DataSynchronizer;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.web.command.authentication.LoginCommand;
import ua.nure.veres.summaryTask.web.command.authentication.LogoutCommand;
import ua.nure.veres.summaryTask.web.command.change.service.ChangeServiceCommand;
import ua.nure.veres.summaryTask.web.command.change.tariff.ChangeTariffCommand;
import ua.nure.veres.summaryTask.web.command.change.user.ChangeFullNameCommand;
import ua.nure.veres.summaryTask.web.command.change.user.ChangePasswordCommand;
import ua.nure.veres.summaryTask.web.command.change.user.ChangeUserRoleCommand;
import ua.nure.veres.summaryTask.web.command.change.user.ChangeUserStatusCommand;
import ua.nure.veres.summaryTask.web.command.create.CreateServiceCommand;
import ua.nure.veres.summaryTask.web.command.create.CreateTariffCommand;
import ua.nure.veres.summaryTask.web.command.decline.request.DeclineDescribeRequestCommand;
import ua.nure.veres.summaryTask.web.command.decline.request.DeclineSubscribeRequestCommand;
import ua.nure.veres.summaryTask.web.command.delete.DeleteServiceCommand;
import ua.nure.veres.summaryTask.web.command.delete.DeleteTariffCommand;
import ua.nure.veres.summaryTask.web.command.delete.DeleteUserCommand;
import ua.nure.veres.summaryTask.web.command.describe.DescribeServiceCommand;
import ua.nure.veres.summaryTask.web.command.forward.ToMainPageCommand;
import ua.nure.veres.summaryTask.web.command.forward.ToPersonalCabinetCommand;
import ua.nure.veres.summaryTask.web.command.forward.ToLoginPageCommand;
import ua.nure.veres.summaryTask.web.command.forward.request.ToDescribeFormCommand;
import ua.nure.veres.summaryTask.web.command.forward.request.ToOrderFormCommand;
import ua.nure.veres.summaryTask.web.command.forward.tariff.ToTariffChangeFormCommand;
import ua.nure.veres.summaryTask.web.command.get.request.GetUserDescribeRequestsCommand;
import ua.nure.veres.summaryTask.web.command.get.request.GetUserSubscribeRequestsCommand;
import ua.nure.veres.summaryTask.web.command.get.service.GetServicesCommand;
import ua.nure.veres.summaryTask.web.command.get.tariff.GetTariffsCommand;
import ua.nure.veres.summaryTask.web.command.get.tariff.GetTariffsTxtCommand;
import ua.nure.veres.summaryTask.web.command.get.user.GetUsersCommand;
import ua.nure.veres.summaryTask.web.command.locale.LanguageSwitchCommand;
import ua.nure.veres.summaryTask.web.command.open.OpenTariffsCommand;
import ua.nure.veres.summaryTask.web.command.order.OrderServiceCommand;
import ua.nure.veres.summaryTask.web.command.submit.register.SubmitRegisterCommand;
import ua.nure.veres.summaryTask.web.command.submit.request.SubmitDescribeRequestCommand;
import ua.nure.veres.summaryTask.web.command.submit.request.SubmitSubscribeRequestCommand;
import ua.nure.veres.summaryTask.web.command.user.ReplenishUserAccountCommand;
import ua.nure.veres.summaryTask.web.command.user.UpdateUserServicesCommand;

import java.util.Map;
import java.util.TreeMap;

/**
 * Holder for all commands
 */
public final class CommandContainer {

    private static final Logger LOG = Logger.getLogger(CommandContainer.class);

    private static Map<String, Command> commands = new TreeMap<>();

    static {
        //log4j
        BasicConfigurator.configure();

        commands.put("login", new LoginCommand(
                SecurityManager.getInstance(),
                new UserDaoFactory(),
                PaymentManager.getInstance()));

        commands.put("logout", new LogoutCommand());

        commands.put("submitRegister", new SubmitRegisterCommand(
                new UserDaoFactory(),
                SecurityManager.getInstance()
        ));

        commands.put("updateUserServices", new UpdateUserServicesCommand(
                new ServiceDaoFactory(),
                new TariffDaoFactory(),
                new UserServiceDaoFactory()
        ));

        commands.put("changeFullName", new ChangeFullNameCommand(
                new UserDaoFactory()
        ));

        commands.put("changePassword", new ChangePasswordCommand(
                new UserDaoFactory(),
                SecurityManager.getInstance()
        ));

        commands.put("changeService", new ChangeServiceCommand(
                new ServiceDaoFactory(),
                DataSynchronizer.getInstance()
        ));

        commands.put("changeTariff", new ChangeTariffCommand(
                new TariffDaoFactory(),
                new ServiceDaoFactory()
        ));

        commands.put("changeUserStatus", new ChangeUserStatusCommand(
                new UserDaoFactory()
        ));

        commands.put("changeUserRole", new ChangeUserRoleCommand(
                new UserDaoFactory()
        ));

        commands.put("orderService", new OrderServiceCommand(
                new UserOrderDaoFactory(),
                new UserDaoFactory()
        ));

        commands.put("describeService", new DescribeServiceCommand(
                new UserDescribeDaoFactory(),
                new UserServiceDaoFactory(),
                new UserDaoFactory()
        ));

        commands.put("getUserSubscribeRequests", new GetUserSubscribeRequestsCommand(
                new UserOrderDaoFactory(),
                new UserDaoFactory(),
                new TariffDaoFactory(),
                new ServiceDaoFactory()
        ));

        commands.put("getUserDescribeRequests", new GetUserDescribeRequestsCommand(
                new UserDescribeDaoFactory(),
                new UserServiceDaoFactory(),
                new UserDaoFactory(),
                new TariffDaoFactory(),
                new ServiceDaoFactory()
        ));

        commands.put("getServices", new GetServicesCommand());

        commands.put("getTariffs", new GetTariffsCommand(
                new TariffDaoFactory()
        ));

        commands.put("getTariffsTxt", new GetTariffsTxtCommand());

        commands.put("getUsers", new GetUsersCommand(
                new UserDaoFactory()
        ));

        commands.put("openTariffList", new OpenTariffsCommand(
                new TariffDaoFactory(),
                new ServiceDaoFactory()
        ));

        commands.put("createService", new CreateServiceCommand(
                new ServiceDaoFactory(),
                DataSynchronizer.getInstance()
        ));

        commands.put("createTariff", new CreateTariffCommand(
                new TariffDaoFactory(),
                new ServiceDaoFactory()
        ));

        commands.put("submitSubscribeRequest", new SubmitSubscribeRequestCommand(
                new UserOrderDaoFactory(),
                new UserServiceDaoFactory()
        ));

        commands.put("submitDescribeRequest", new SubmitDescribeRequestCommand(
                new UserServiceDaoFactory(),
                new UserDescribeDaoFactory()
        ));

        commands.put("declineSubscribeRequest", new DeclineSubscribeRequestCommand(
                new UserOrderDaoFactory(),
                new TariffDaoFactory(),
                new UserDaoFactory()
        ));

        commands.put("declineDescribeRequest", new DeclineDescribeRequestCommand(
                new UserDescribeDaoFactory()
        ));

        commands.put("deleteService", new DeleteServiceCommand(
                new ServiceDaoFactory(),
                new TariffDaoFactory(),
                new UserDescribeDaoFactory(),
                new UserOrderDaoFactory(),
                new UserServiceDaoFactory(),
                DataSynchronizer.getInstance()
        ));

        commands.put("deleteTariff", new DeleteTariffCommand(
                new TariffDaoFactory(),
                new UserDescribeDaoFactory(),
                new UserOrderDaoFactory(),
                new UserServiceDaoFactory()
        ));

        commands.put("deleteUser", new DeleteUserCommand(
                new UserDaoFactory(),
                new UserServiceDaoFactory(),
                new UserOrderDaoFactory(),
                new UserDescribeDaoFactory()
        ));

        commands.put("toMainPage", new ToMainPageCommand());
        commands.put("toLoginPage", new ToLoginPageCommand());
        commands.put("toPersonalCabinet", new ToPersonalCabinetCommand());
        commands.put("toOrderForm", new ToOrderFormCommand());
        commands.put("toDescribeForm", new ToDescribeFormCommand());

        commands.put("toTariffChangeForm", new ToTariffChangeFormCommand(
                new TariffDaoFactory()
        ));

        commands.put("languageSwitch", new LanguageSwitchCommand());

        commands.put("replenishUserAccount", new ReplenishUserAccountCommand(
                new UserDaoFactory()
        ));

        commands.put("noCommand", new NoCommand());

        LOG.debug("Command container was successfully initialized");
        LOG.trace("Number of commands --> " + commands.size());
    }

    private CommandContainer() {
        //no op
    }

    /**
     * Returns command object with the given name.
     *
     * @param commandName Name of the command.
     * @return Command object.
     */
    public static Command get(String commandName) {
        if (commandName == null || !commands.containsKey(commandName)) {
            LOG.trace("Command not found, name --> " + commandName);
            return commands.get("noCommand");
        }

        return commands.get(commandName);
    }
}
