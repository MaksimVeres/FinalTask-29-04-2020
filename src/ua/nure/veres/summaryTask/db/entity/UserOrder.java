package ua.nure.veres.summaryTask.db.entity;

import java.sql.Date;

/**
 * User order entity.
 */
public class UserOrder extends Entity {

    private static final long serialVersionUID = 156654010623798L;

    private long userId;

    private long tariffId;

    private String userPhone;

    private String comment;

    private Date orderDate;

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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "userId=" + userId +
                ", tariffId=" + tariffId +
                ", userPhone='" + userPhone + '\'' +
                ", comment='" + comment + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}
