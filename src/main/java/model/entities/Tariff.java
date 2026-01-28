package model.entities;

public class Tariff {

    private String vehicleType;
    private double hourPrice;
    private double dayPrice;
    private double weekPrice;
    private double monthPrice;

    public Tariff(String vehicleType, double hourPrice, double dayPrice, double weekPrice, double monthPrice) {
        this.vehicleType = vehicleType;
        this.hourPrice = hourPrice;
        this.dayPrice = dayPrice;
        this.weekPrice = weekPrice;
        this.monthPrice = monthPrice;
    }

    public Tariff() {
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getHourPrice() {
        return hourPrice;
    }

    public void setHourPrice(double hourPrice) {
        this.hourPrice = hourPrice;
    }

    public double getDayPrice() {
        return dayPrice;
    }

    public void setDayPrice(double dayPrice) {
        this.dayPrice = dayPrice;
    }

    public double getWeekPrice() {
        return weekPrice;
    }

    public void setWeekPrice(double weekPrice) {
        this.weekPrice = weekPrice;
    }

    public double getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(double monthPrice) {
        this.monthPrice = monthPrice;
    }

    @Override
    public String toString() {
        return "Tariff{" + "vehicleType=" + vehicleType + ", hourPrice=" + hourPrice + ", dayPrice=" + dayPrice + ", weekPrice=" + weekPrice + ", monthPrice=" + monthPrice + '}';
    }

}
