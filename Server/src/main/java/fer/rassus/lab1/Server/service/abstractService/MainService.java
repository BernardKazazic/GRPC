package fer.rassus.lab1.Server.service.abstractService;

import fer.rassus.lab1.Server.domain.Measurement;
import fer.rassus.lab1.Server.domain.Sensor;
import fer.rassus.lab1.Server.rest.dto.ClosestSensorResponseDTO;
import fer.rassus.lab1.Server.rest.dto.MeasurementDTO;
import fer.rassus.lab1.Server.rest.dto.RegisterDTO;
import fer.rassus.lab1.Server.rest.dto.SaveMeasurementResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MainService {

    Sensor registerSensor(RegisterDTO registerDTO);

    ClosestSensorResponseDTO getClosestSensor(double longitude, double latitude);

    SaveMeasurementResponseDTO saveMeasurement(long sensorId, MeasurementDTO measurementDTO);

    List<Sensor> listSensors();

    List<Measurement> listMeasurements(long sensorId);
}
