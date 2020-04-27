package ua.nure.veres.summaryTask.db.entity;

/**
 * User entity.
 */
public class User extends Entity {

    private static final long serialVersionUID = -56645991103566L;

    private String login;

    private String password;

    private String firstName;

    private String lastName;

    private int roleId;

    private int statusId;

    private double accountState;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
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
        return "User{" +
                "login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roleId=" + roleId +
                ", statusId=" + statusId +
                ", accountState=" + accountState +
                '}';
    }

    @Override
    public int hashCode() {
        return 37 * (login.hashCode() + password.hashCode() + firstName.hashCode()
                + lastName.hashCode() + roleId + statusId + Double.hashCode(accountState));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() == obj.getClass()) {
            User other = (User) obj;
            return login.equals(other.getLogin()) && password.equals(other.getPassword())
                    && firstName.equals(other.firstName) && lastName.equals(other.lastName)
                    && roleId == other.roleId && statusId == other.statusId && accountState == other.accountState;
        }
        return false;
    }
}
