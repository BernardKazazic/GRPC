package fer.rassus.lab1;


import com.opencsv.bean.CsvToBeanBuilder;
import fer.rassus.lab1.grpc.GrpcClient;
import fer.rassus.lab1.grpc.GrpcServer;
import fer.rassus.lab1.grpc.GrpcService;
import fer.rassus.lab1.http.DTO.*;
import fer.rassus.lab1.http.HttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

public class Sensor {
    private static final Logger logger = Logger.getLogger(Sensor.class.getName());
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String REST_URL = "http://localhost:8080/";
    private long id;
    private final Double longitude;
    private final Double latitude;
    private final String host;
    private final String port;
    private List<Measurement> measurements;
    private final long startTime;

    public Sensor(String host, String port) {
        this.id = -1;
        this.host = host;
        this.port = port;
        this.longitude = Double.valueOf(df.format(Math.random() * (16.0 - 15.87) + 15.87));
        this.latitude = Double.valueOf(df.format(Math.random() * (45.85 - 45.75) + 45.75));
        this.measurements = new ArrayList<>();
        this.startTime = System.currentTimeMillis() / 1000;
    }

    public void loadMeasurements(Path measurementsFile) {
        try {
            List<MeasurementCSV> measurementCSVS = new CsvToBeanBuilder<MeasurementCSV>(new FileReader(measurementsFile.toString()))
                    .withType(MeasurementCSV.class)
                    .withSkipLines(1)
                    .build()
                    .parse();
            for(MeasurementCSV m : measurementCSVS) {
                this.measurements.add(new Measurement(m));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    public Measurement getMeasurement() {
        int index = (int) ((System.currentTimeMillis() / 1000 - startTime) % 100);
        return measurements.get(index);
    }

    public static void main(String[] args) {
        // load args
        String sensorIp = "127.0.0.1";
        String sensorPort = args[0];
        int grcpPort = Integer.parseInt(sensorPort) + 1000;
        String measurementsPath = args[1];

        // create sensor instance
        Sensor sensor = new Sensor(sensorIp, sensorPort);

        // create http client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpClient httpClient = retrofit.create(HttpClient.class);

        // create data object for register request
        RegisterData registerData = new RegisterData();
        registerData.setLongitude(sensor.longitude);
        registerData.setLatitude(sensor.latitude);
        registerData.setIp(sensor.host);
        registerData.setPort(sensor.port);

        // send post request to REST server
        Call<RegisterResponseData> registerCall = httpClient.register(registerData);

        try {
            Response<RegisterResponseData> response = registerCall.execute();
            sensor.id = response.body().getId();
            logger.info("Sensor: Successfully registered");
        }
        catch (Exception e) {
            logger.info("Sensor: Failed registration");
            e.printStackTrace();
            exit(1);
        }

        // load readings
        sensor.loadMeasurements(Path.of(measurementsPath));

        // get closest sensor
        Call<GetClosestResponseData> closestSensorCall = httpClient.getClosestSensor(sensor.longitude, sensor.latitude);
        String otherIp = null;
        String otherPort = null;

        try {
            Response<GetClosestResponseData> response = closestSensorCall.execute();
            GetClosestResponseData responseData = response.body();
            if(responseData != null) {
                otherIp = responseData.getIp();
                otherPort = responseData.getPort();
                logger.info("Sensor: Successfully received closest sensor data: ip -> " + otherIp + " port -> " + otherPort);
            }
        }
        catch (Exception e) {
            logger.info("Sensor: Failed receiving closest sensor data");
            e.printStackTrace();
        }

        // start grpc server
        GrpcServer grpcServer = new GrpcServer(new GrpcService(), grcpPort);
        try {
            grpcServer.start();
            logger.info("Sensor: Started GRPC server");
        }
        catch(Exception e) {
            logger.info("Sensor: Failed starting GRPC server");
            e.printStackTrace();
        }

        // create grpc client
        GrpcClient grpcClient = new GrpcClient(sensorIp, grcpPort);
        logger.info("Sensor: Created GRPC client");

        if(otherIp != null && otherPort != null) {
            grpcClient.createOtherConnection(otherIp, Integer.parseInt(otherPort) + 1000);
            logger.info("Sensor: Connected GRPC client to closest sensor");
        }

        // add shutdown hook for client
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                grpcClient.stop();
            }
            catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }));

        // start generating measurements
        while(true) {
            // get measurement for current time
            Measurement currentMeasurement = sensor.getMeasurement();

            // store measurement on grpc
            grpcClient.sendMeasurement(currentMeasurement);

            // get measurement from closest sensor
            Measurement otherMeasurement = grpcClient.getMeasurement();

            // average this sensor measurement with the closest sensor measurement
            SaveMeasurementData saveMeasurementData = new SaveMeasurementData();
            double averageTemperature;
            double averagePressure;
            double averageHumidity;
            Double averageCo;
            Double averageNo2;
            Double averageSo2;
            if(otherMeasurement != null) {
                averageTemperature = (currentMeasurement.getTemperature() + otherMeasurement.getTemperature()) / 2.0;
                averagePressure = (currentMeasurement.getPressure() + otherMeasurement.getPressure()) / 2.0;
                averageHumidity = (currentMeasurement.getHumidity() + otherMeasurement.getHumidity()) / 2.0;
                if(currentMeasurement.coExists()) {
                    if(otherMeasurement.coExists() && otherMeasurement.getCo() > 0) {
                        averageCo = (currentMeasurement.getCo() + otherMeasurement.getCo()) / 2.0;
                    }
                    else {
                        averageCo = Double.valueOf(currentMeasurement.getCo());
                    }
                }
                else {
                    averageCo = null;
                }
                if(currentMeasurement.no2Exists()) {
                    if(otherMeasurement.no2Exists() && otherMeasurement.getNo2() > 0) {
                        averageNo2 = (currentMeasurement.getNo2() + otherMeasurement.getNo2()) / 2.0;
                    }
                    else {
                        averageNo2 = Double.valueOf(currentMeasurement.getNo2());
                    }
                }
                else {
                    averageNo2 = null;
                }
                if(currentMeasurement.so2Exists()) {
                    if(otherMeasurement.so2Exists() && otherMeasurement.getSo2() > 0) {
                        averageSo2 = (currentMeasurement.getSo2() + otherMeasurement.getSo2()) / 2.0;
                    }
                    else {
                        averageSo2 = Double.valueOf(currentMeasurement.getCo());
                    }
                }
                else {
                    averageSo2 = null;
                }
            }
            else {
                averageTemperature = currentMeasurement.getTemperature();
                averagePressure = currentMeasurement.getPressure();
                averageHumidity = currentMeasurement.getHumidity();
                averageCo = currentMeasurement.coExists() ? Double.valueOf(currentMeasurement.getCo()) : null;
                averageNo2 = currentMeasurement.no2Exists() ? Double.valueOf(currentMeasurement.getNo2()) : null;
                averageSo2 = currentMeasurement.so2Exists() ? Double.valueOf(currentMeasurement.getSo2()) : null;
            }
            saveMeasurementData.setTemperature(averageTemperature);
            saveMeasurementData.setHumidity(averageHumidity);
            saveMeasurementData.setPressure(averagePressure);
            saveMeasurementData.setCo(averageCo);
            saveMeasurementData.setNo2(averageNo2);
            saveMeasurementData.setSo2(averageSo2);

            // send measurement to rest server
            Long sensorId = Long.valueOf(sensor.id);
            Call<SaveMeasurementResponseData> saveMeasurementCall = httpClient.saveMeasurement(sensorId, saveMeasurementData);

            try {
                Response<SaveMeasurementResponseData> response = saveMeasurementCall.execute();
                logger.info("Sensor: Sent measurement data to rest server");
            }
            catch (Exception e) {
                logger.info("Sensor: Failed sending measurement data to rest server");
                e.printStackTrace();
            }

            // sleep 1 second
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
