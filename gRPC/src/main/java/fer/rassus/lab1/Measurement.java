package fer.rassus.lab1;

public class Measurement {

    private int temperature;

    private int pressure;

    private int humidity;

    private Integer co;

    private Integer no2;

    private Integer so2;

    public Measurement(MeasurementGrpc measurement) {
        temperature = measurement.getTemperature();
        pressure = measurement.getPressure();
        humidity = measurement.getHumidity();
        if(measurement.getCo().hasNull()) {
            co = null;
        }
        else {
            co = measurement.getCo().getCo();
        }
        if(measurement.getSo2().hasNull()) {
            so2 = null;
        }
        else {
            so2 = measurement.getSo2().getSo2();
        }
        if(measurement.getNo2().hasNull()) {
            no2 = null;
        }
        else {
            no2 = measurement.getNo2().getNo2();
        }
    }

    public Measurement(MeasurementCSV measurement) {
        temperature = measurement.getTemperature();
        pressure = measurement.getPressure();
        humidity = measurement.getHumidity();
        co = measurement.getCo();
        no2 = measurement.getNo2();
        so2 = measurement.getSo2();
    }

    public boolean coExists() {
        return co != null;
    }

    public boolean no2Exists() {
        return no2 != null;
    }

    public boolean so2Exists() {
        return so2 != null;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public Integer getCo() {
        return co;
    }

    public Integer getNo2() {
        return no2;
    }

    public Integer getSo2() {
        return so2;
    }
}
