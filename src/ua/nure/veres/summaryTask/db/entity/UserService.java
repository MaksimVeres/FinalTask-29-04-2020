package ua.nure.veres.summaryTask.db.entity;

import java.util.Date;

/**
 * User service entity.
 */
public class UserService extends Entity {

    private static final long serialVersionUID = -26663010623798L;

    private long userId;

    private long tariffId;

    private String address;

    private Date lastPaymentDate;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTariffId() {
        return tariffId;
    }

    public void setTariffId(long tariffId) {
        this.tariffId = tariffId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    @Override
    public String toString() {
        return "UserService{" +
                "userId=" + userId +
                ", tariffId=" + tariffId +
                ", address='" + address + '\'' +
                ", lastPaymentDate=" + lastPaymentDate +
                '}';
    }

}
