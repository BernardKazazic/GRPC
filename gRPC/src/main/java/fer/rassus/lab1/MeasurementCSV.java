package fer.rassus.lab1;

import com.opencsv.bean.CsvBindByPosition;

public class MeasurementCSV {
    @CsvBindByPosition(position = 0)
    private int temperature;
    @CsvBindByPosition(position = 1)
    private int pressure;
    @CsvBindByPosition(position = 2)
    private int humidity;
    @CsvBindByPosition(position = 3)
    private Integer co;
    @CsvBindByPosition(position = 4)
    private Integer no2;
    @CsvBindByPosition(position = 5)
    private Integer so2;

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Integer getCo() {
        return co;
    }

    public void setCo(Integer co) {
        this.co = co;
    }

    public Integer getNo2() {
        return no2;
    }

    public void setNo2(Integer no2) {
        this.no2 = no2;
    }

    public Integer getSo2() {
        return so2;
    }

    public void setSo2(Integer so2) {
        this.so2 = so2;
    }

    @Override
    public String toString() {
        return "MeasurementCSV{" +
                "temperature=" + temperature +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", co=" + co +
                ", no2=" + no2 +
                ", so2=" + so2 +
                '}';
    }
}
