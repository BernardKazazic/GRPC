package fer.rassus.lab1.Server.rest.controller;

import fer.rassus.lab1.Server.domain.Measurement;
import fer.rassus.lab1.Server.domain.Sensor;
import fer.rassus.lab1.Server.rest.dto.ClosestSensorResponseDTO;
import fer.rassus.lab1.Server.rest.dto.MeasurementDTO;
import fer.rassus.lab1.Server.rest.dto.RegisterDTO;
import fer.rassus.lab1.Server.rest.dto.SaveMeasurementResponseDTO;
import fer.rassus.lab1.Server.service.abstractService.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("")
public class Controller {

    @Qualifier("mainServiceJpa")
    @Autowired
    MainService service;

    @PostMapping("/registerSensor")
    public ResponseEntity<Sensor> registerSensor(@Validated @RequestBody RegisterDTO registerDTO) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("").toUriString());
        return ResponseEntity.created(uri).body(service.registerSensor(registerDTO));
    }

    @GetMapping("/getClosestSensor")
    public ResponseEntity<ClosestSensorResponseDTO> getClosestSensor(@RequestParam double longitude, @RequestParam double latitude) {
        ClosestSensorResponseDTO responseDTO = service.getClosestSensor(longitude, latitude);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/saveMeasurement")
    public ResponseEntity<SaveMeasurementResponseDTO> saveMeasurement(@RequestParam long sensorId, @RequestBody MeasurementDTO measurementDTO) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("").toUriString());
        return ResponseEntity.created(uri).body(service.saveMeasurement(sensorId, measurementDTO));
    }

    @GetMapping("/listSensors")
    public ResponseEntity<List<Sensor>> listSensors() {
        return ResponseEntity.ok().body(service.listSensors());
    }

    @GetMapping("/listMeasurements")
    public ResponseEntity<List<Measurement>> listMeasurements(@RequestParam long sensorId) {
        return ResponseEntity.ok().body(service.listMeasurements(sensorId));
    }

}
