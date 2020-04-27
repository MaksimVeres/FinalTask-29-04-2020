package ua.nure.veres.summaryTask.db.bean;

import ua.nure.veres.summaryTask.db.entity.*;

import java.io.Serializable;
import java.sql.Date;

/**
 * User request bean.
 */
public class UserRequestBean implements Serializable {

    private static final long serialVersionUID = 456798222325766776L;

    private long userId;

    private String userLogin;

    private String userFirstName;

    private String userLastName;

    private long tariffId;

    private String tariffName;

    private Double tariffConnectionPayment;

    private Double tariffMonthPayment;

    private String tariffFeature;

    private long serviceId;

    private String serviceName;

    private String userPhone;

    private long userRequestId;

    private String comment;

    private Date requestDate;

    public UserRequestBean() {
        //no op
    }

    public UserRequestBean(User user, Tariff tariff, Service service, UserOrder userOrder) {
        userId = user.getId();
        userLogin = user.getLogin();
        userFirstName = user.getFirstName();
        userLastName = user.getLastName();
        tariffId = tariff.getId();
        tariffName = tariff.getName();
        tariffConnectionPayment = tariff.getConnectionPayment();
        tariffMonthPayment = tariff.getMonthPayment();
        tariffFeature = tariff.getFeature();
        serviceId = service.getId();
        serviceName = service.getName();
        userPhone = userOrder.getUserPhone();
        userRequestId = userOrder.getId();
        comment = userOrder.getComment();
        requestDate = userOrder.getOrderDate();
    }

    public UserRequestBean(User user, Tariff tariff, Service service, UserDescribe userDescribe) {
        userId = user.getId();
        userLogin = user.getLogin();
        userFirstName = user.getFirstName();
        userLastName = user.getLastName();
        tariffId = tariff.getId();
        tariffName = tariff.getName();
        tariffConnectionPayment = tariff.getConnectionPayment();
        tariffMonthPayment = tariff.getMonthPayment();
        tariffFeature = tariff.getFeature();
        serviceId = service.getId();
        serviceName = service.getName();
        userPhone = userDescribe.getUserPhone();
        userRequestId = userDescribe.getId();
        comment = userDescribe.getComment();
        requestDate = userDescribe.getDescribeDate();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public long getTariffId() {
        return tariffId;
    }

    public void setTariffId(long tariffId) {
        this.tariffId = tariffId;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public Double getTariffConnectionPayment() {
        return tariffConnectionPayment;
    }

    public void setTariffConnectionPayment(Double tariffConnectionPayment) {
        this.tariffConnectionPayment = tariffConnectionPayment;
    }

    public Double getTariffMonthPayment() {
        return tariffMonthPayment;
    }

    public void setTariffMonthPayment(Double tariffMonthPayment) {
        this.tariffMonthPayment = tariffMonthPayment;
    }

    public String getTariffFeature() {
        return tariffFeature;
    }

    public void setTariffFeature(String tariffFeature) {
        this.tariffFeature = tariffFeature;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public long getUserRequestId() {
        return userRequestId;
    }

    public void setUserRequestId(long userRequestId) {
        this.userRequestId = userRequestId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public String toString() {
        return "UserRequestBean{" +
                "userId=" + userId +
                ", userLogin='" + userLogin + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", tariffId=" + tariffId +
                ", tariffName='" + tariffName + '\'' +
                ", tariffConnectionPayment=" + tariffConnectionPayment +
                ", tariffMonthPayment=" + tariffMonthPayment +
                ", tariffFeature='" + tariffFeature + '\'' +
                ", serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", comment='" + comment + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }
}
