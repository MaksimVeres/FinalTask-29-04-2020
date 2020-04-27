package ua.nure.veres.summaryTask.web;

/**
 * Path holder (filter, controller commands).
 */
public final class Path {
    //pages

    public static final String PAGE_ERROR = "/WEB-INF/jsp/error/error_page.jsp";

    //commands

    private static final String COMMAND_PROCESS = "/process?command=";

    public static final String COMMAND_LOGIN = COMMAND_PROCESS + "login";

    public static final String COMMAND_LOGOUT = COMMAND_PROCESS + "logout";

    public static final String COMMAND_REDIRECT_TO_MAIN_PAGE = COMMAND_PROCESS + "toMainPage&forwardMethod=redirect";

    public static final String COMMAND_REDIRECT_TO_LOGIN_PAGE = COMMAND_PROCESS + "toLoginPage&forwardMethod=redirect";

    //for MoveToFilter

    private static final String MOVE_FORWARD = "/forward?page=";

    private static final String MOVE_REDIRECT = "/page?name=";

    public static final String MOVE_BY_FORWARD_TO_MAIN_PAGE = MOVE_FORWARD + "mainPage";

    public static final String MOVE_BY_REDIRECT_TO_MAIN_PAGE = MOVE_REDIRECT + "mainPage";

    public static final String MOVE_BY_FORWARD_TO_LOGIN_PAGE = MOVE_FORWARD + "loginPage";

    public static final String MOVE_BY_REDIRECT_TO_LOGIN_PAGE = MOVE_REDIRECT + "loginPage";

    public static final String MOVE_BY_FORWARD_TO_PERSONAL_CABINET_PAGE = MOVE_FORWARD + "personalCabinetPage";

    public static final String MOVE_BY_REDIRECT_TO_PERSONAL_CABINET_PAGE = MOVE_REDIRECT + "personalCabinetPage";

    public static final String MOVE_BY_FORWARD_TO_PERSONAL_CABINET_SERVICES_PAGE =
            MOVE_FORWARD + "personalCabinetServicesPage";

    public static final String MOVE_BY_FORWARD_TO_TARIFF_LIST = MOVE_FORWARD + "tariffListPage";

    public static final String MOVE_BY_FORWARD_TO_ORDER_PAGE = MOVE_FORWARD + "orderPage";

    public static final String MOVE_BY_REDIRECT_TO_ORDER_SUBMITTED_PAGE = MOVE_REDIRECT + "orderSubmittedPage";

    public static final String MOVE_BY_FORWARD_TO_DESCRIBE_PAGE = MOVE_FORWARD + "describePage";

    public static final String MOVE_BY_REDIRECT_TO_DESCRIBE_SUBMITTED_PAGE = MOVE_REDIRECT + "describeSubmittedPage";

    public static final String MOVE_BY_REDIRECT_TO_ADMINISTRATION_SUBSCRIBE_REQUESTS_PAGE
            = MOVE_REDIRECT + "administrationPanelSubscribeRequests";

    public static final String MOVE_BY_REDIRECT_TO_ADMINISTRATION_DESCRIBE_REQUESTS_PAGE
            = MOVE_REDIRECT + "administrationPanelDescribeRequests";

    public static final String MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST
            = MOVE_REDIRECT + "administrationPanelServiceList";

    public static final String MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST
            = MOVE_REDIRECT + "administrationPanelTariffList";

    public static final String MOVE_BY_FORWARD_TO_ADMINISTRATION_CHANGE_TARIFF_FORM
            = MOVE_FORWARD + "administrationPanelTariffChangeForm";

    public static final String MOVE_BY_REDIRECT_TO_ACCOUNT_REPLENISHED_PAGE
            = MOVE_REDIRECT + "accountReplenished";

    public static final String MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST
            = MOVE_REDIRECT + "administrationPanelUserList";

    private Path() {
        //no op
    }

}
