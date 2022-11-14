package fer.rassus.lab1.Server.service.impl;

import fer.rassus.lab1.Server.dao.MeasurementRepository;
import fer.rassus.lab1.Server.dao.SensorRepository;
import fer.rassus.lab1.Server.domain.Measurement;
import fer.rassus.lab1.Server.domain.Sensor;
import fer.rassus.lab1.Server.rest.dto.ClosestSensorResponseDTO;
import fer.rassus.lab1.Server.rest.dto.MeasurementDTO;
import fer.rassus.lab1.Server.rest.dto.RegisterDTO;
import fer.rassus.lab1.Server.rest.dto.SaveMeasurementResponseDTO;
import fer.rassus.lab1.Server.service.abstractService.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MainServiceJpa implements MainService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Override
    public Sensor registerSensor(RegisterDTO registerDTO) {
        Sensor sensor = new Sensor();

        sensor.setLatitude(registerDTO.getLatitude());
        sensor.setLongitude(registerDTO.getLongitude());
        sensor.setIp(registerDTO.getIp());
        sensor.setPort(registerDTO.getPort());

        sensorRepository.save(sensor);
        return sensorRepository.findByLongitudeAndLatitude(sensor.getLongitude(), sensor.getLatitude());
    }

    @Override
    public ClosestSensorResponseDTO getClosestSensor(double longitude, double latitude) {
        List<Sensor> sensorList = sensorRepository.findAll();
        Sensor closestSensor = new Sensor();
        closestSensor.setId(null);
        closestSensor.setLongitude(null);
        closestSensor.setLatitude(null);
        closestSensor.setIp(null);
        closestSensor.setPort(null);
        double minDistance = -1;
        double R = 6371;

        for(Sensor sensor : sensorList) {
            if(sensor.getLatitude() == latitude && sensor.getLongitude() == longitude) continue;
            double dlon = sensor.getLongitude() - longitude;
            double dlat = sensor.getLatitude() - latitude;
            double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(latitude) * Math.cos(sensor.getLatitude()) * Math.pow(Math.sin(dlon/2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c;

            if((minDistance == -1 || minDistance > d)) {
                minDistance = d;
                closestSensor = sensor;
            }
        }
        ClosestSensorResponseDTO response = new ClosestSensorResponseDTO();
        response.setId(closestSensor.getId());
        response.setIp(closestSensor.getIp());
        response.setPort(closestSensor.getPort());
        response.setLongitude(closestSensor.getLongitude());
        response.setLatitude(closestSensor.getLatitude());
        return response;
    }

    @Override
    public SaveMeasurementResponseDTO saveMeasurement(long sensorId, MeasurementDTO measurementDTO) {
        Measurement measurement = new Measurement();
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        Sensor sensor = optionalSensor.get();
        measurement.setTemperature(measurementDTO.getTemperature());
        measurement.setPressure(measurementDTO.getPressure());
        measurement.setHumidity(measurementDTO.getHumidity());
        measurement.setCo(measurementDTO.getCo());
        measurement.setNo2(measurementDTO.getNo2());
        measurement.setSo2(measurementDTO.getSo2());
        measurement.setSensor(sensor);
        measurementRepository.save(measurement);

        SaveMeasurementResponseDTO response = new SaveMeasurementResponseDTO();
        response.setTemperature(measurement.getTemperature());
        response.setPressure(measurement.getPressure());
        response.setHumidity(measurement.getHumidity());
        response.setCo(measurement.getCo());
        response.setNo2(measurement.getNo2());
        response.setSo2(measurement.getSo2());
        return response;
    }

    @Override
    public List<Sensor> listSensors() {
        return sensorRepository.findAll();
    }

    @Override
    public List<Measurement> listMeasurements(long sensorId) {
        return measurementRepository.findBySensor_Id(sensorId);
    }
}
