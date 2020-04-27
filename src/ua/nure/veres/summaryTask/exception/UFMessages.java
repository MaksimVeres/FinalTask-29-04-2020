package ua.nure.veres.summaryTask.exception;

import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.authentication.LogInException;
import ua.nure.veres.summaryTask.exception.command.authentication.LogoutException;
import ua.nure.veres.summaryTask.exception.command.forward.ForwardCommandException;
import ua.nure.veres.summaryTask.exception.command.registration.RegisterException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.db.DBException;
import ua.nure.veres.summaryTask.exception.download.DownloadException;
import ua.nure.veres.summaryTask.exception.ee.security.SecurityManagerException;
import ua.nure.veres.summaryTask.exception.service.ServiceCreateException;
import ua.nure.veres.summaryTask.exception.service.ServiceUpdateException;
import ua.nure.veres.summaryTask.exception.tariff.TariffCreateException;
import ua.nure.veres.summaryTask.exception.tariff.TariffUpdateException;
import ua.nure.veres.summaryTask.exception.user.UserIdentityException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;

import java.util.HashMap;
import java.util.Map;

/**
 * User-friendly messages class.
 */
public class UFMessages {

    private static final Map<Class, String> UF_EXCEPTION_MESSAGE_MAP = new HashMap<>();

    private static final Map<Class, String> UF_EXCEPTION_TITLE_MAP = new HashMap<>();

    static {

        UF_EXCEPTION_MESSAGE_MAP.put(CommandAccessException.class, "ufe.command.access.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(CommandAccessException.class, "ufe.command.access.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(LogInException.class, "ufe.log.in.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(LogInException.class, "ufe.log.in.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(LogoutException.class, "ufe.log.out.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(LogoutException.class, "ufe.log.out.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(ForwardCommandException.class, "ufe.forward.command.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(ForwardCommandException.class, "ufe.forward.command.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(RegisterException.class, "ufe.register.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(RegisterException.class, "ufe.register.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(AttributeException.class, "ufe.attribute.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(AttributeException.class, "ufe.attribute.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(ParameterException.class, "ufe.parameter.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(ParameterException.class, "ufe.parameter.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(DBException.class, "ufe.db.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(DBException.class, "ufe.db.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(DownloadException.class, "ufe.download.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(DownloadException.class, "ufe.download.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(SecurityManagerException.class, "ufe.security.manager.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(SecurityManagerException.class, "ufe.security.manager.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(ServiceCreateException.class, "ufe.service.create.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(ServiceCreateException.class, "ufe.service.create.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(ServiceUpdateException.class, "ufe.service.update.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(ServiceUpdateException.class, "ufe.service.update.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(TariffCreateException.class, "ufe.tariff.create.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(TariffCreateException.class, "ufe.tariff.create.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(TariffUpdateException.class, "ufe.tariff.update.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(TariffUpdateException.class, "ufe.tariff.update.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(UserIdentityException.class, "ufe.user.identity.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(UserIdentityException.class, "ufe.user.identity.exception.title");

        UF_EXCEPTION_MESSAGE_MAP.put(UserUpdateException.class, "ufe.user.update.exception.message");
        UF_EXCEPTION_TITLE_MAP.put(UserUpdateException.class, "ufe.user.update.exception.title");

    }

    private UFMessages() {
        //no op
    }

    /**
     * Gets user friendly message from UF_EXCEPTION_MESSAGE_MAP.
     *
     * @param exceptionClass Class of an exception
     * @return User friendly exception message
     */
    static String getUFMessage(Class exceptionClass) {
        return UF_EXCEPTION_MESSAGE_MAP.get(exceptionClass);
    }

    /**
     * Gets user friendly title from UF_EXCEPTION_TITLE_MAP.
     *
     * @param exceptionClass Class of an exception
     * @return User friendly exception title
     */
    static String getUFTitle(Class exceptionClass) {
        return UF_EXCEPTION_TITLE_MAP.get(exceptionClass);
    }
}
