package ua.nure.veres.summaryTask.db;

public final class Fields {

    //entities
    public static final String ENTITY_ID = "id";

    public static final String TARIFF_NAME = "name";
    public static final String TARIFF_CONNECTION_PAYMENT = "connection_payment";
    public static final String TARIFF_MONTH_PAYMENT = "month_payment";
    public static final String TARIFF_FEATURE = "feature";
    public static final String TARIFF_SERVICE_ID = "service_id";

    public static final String USER_SERVICE_USER_ID = "user_id";
    public static final String USER_SERVICE_TARIFF_ID = "tariff_id";
    public static final String USER_SERVICE_ADDRESS = "address";
    public static final String USER_SERVICE_LAST_PAYMENT_DATE = "last_payment_date";

    public static final String USER_LOGIN = "login";
    public static final String USER_PASSWORD = "password";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_ROLE_ID = "role_id";
    public static final String USER_STATUS_ID = "status_id";
    public static final String USER_ACCOUNT_STATE = "account_state";

    public static final String SERVICE_NAME = "name";

    public static final String USER_ORDER_USER_ID = "user_id";
    public static final String USER_ORDER_TARIFF_ID = "tariff_id";
    public static final String USER_ORDER_USER_PHONE = "user_phone";
    public static final String USER_ORDER_COMMENT = "comment";
    public static final String USER_ORDER_ORDER_DATE = "order_date";

    public static final String USER_DESCRIBE_USER_SERVICE_ID = "user_service_id";
    public static final String USER_DESCRIBE_USER_PHONE = "user_phone";
    public static final String USER_DESCRIBE_COMMENT = "comment";
    public static final String USER_DESCRIBE_DESCRIBE_DATE = "describe_date";

    private Fields() {
        //no op
    }
}
