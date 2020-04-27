package ua.nure.veres.summaryTask.db.entity;

/**
 * Tariff entity.
 */
public class Tariff extends Entity {

    private static final long serialVersionUID = 68770991323564710L;

    private String name;

    private Double connectionPayment;

    private Double monthPayment;

    private String feature;

    private long serviceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getConnectionPayment() {
        return connectionPayment;
    }

    public void setConnectionPayment(double connectionPayment) {
        this.connectionPayment = connectionPayment;
    }

    public Double getMonthPayment() {
        return monthPayment;
    }

    public void setMonthPayment(Double monthPayment) {
        this.monthPayment = monthPayment;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "Tariff{" +
                "name='" + name + '\'' +
                ", connectionPayment=" + connectionPayment +
                ", monthPayment=" + monthPayment +
                ", feature='" + feature + '\'' +
                ", serviceId=" + serviceId +
                '}';
    }
}
