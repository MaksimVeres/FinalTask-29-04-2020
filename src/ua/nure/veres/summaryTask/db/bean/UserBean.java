package ua.nure.veres.summaryTask.db.bean;

import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.Status;
import ua.nure.veres.summaryTask.db.entity.User;

import java.io.Serializable;
import java.util.Locale;

/**
 * User bean.
 */
public class UserBean implements Serializable {

    private static final long serialVersionUID = 188324236566666L;

    private long id;

    private String login;

    private String firstName;

    private String lastName;

    private String role;

    private int roleId;

    private String status;

    private int statusId;

    private double accountState;

    public UserBean() {
        //no op
    }

    public UserBean(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = Role.getRole(user).toString().toLowerCase(Locale.getDefault());
        this.roleId = user.getRoleId();
        this.status = Status.getStatus(user).toString().toLowerCase(Locale.getDefault());
        this.statusId = user.getStatusId();
        this.accountState = user.getAccountState();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public double getAccountState() {
        return accountState;
    }

    public void setAccountState(double accountState) {
        this.accountState = accountState;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", roleId=" + roleId +
                ", status='" + status + '\'' +
                ", statusId=" + statusId +
                ", accountState=" + accountState +
                '}';
    }
}
