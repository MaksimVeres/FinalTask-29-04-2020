package ua.nure.veres.summaryTask.db.entity;

import java.sql.Date;

/**
 * User describe entity.
 */
public class UserDescribe extends Entity {

    private static final long serialVersionUID = -306654010623798L;

    private long userServiceId;

    private String userPhone;

    private String comment;

    private Date describeDate;

    public long getUserServiceId() {
        return userServiceId;
    }

    public void setUserServiceId(long userServiceId) {
        this.userServiceId = userServiceId;
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

    public Date getDescribeDate() {
        return describeDate;
    }

    public void setDescribeDate(Date describeDate) {
        this.describeDate = describeDate;
    }

    @Override
    public String toString() {
        return "UserDescribe{" +
                "userServiceId=" + userServiceId +
                ", userPhone='" + userPhone + '\'' +
                ", comment='" + comment + '\'' +
                ", describeDate=" + describeDate +
                '}';
    }
}
