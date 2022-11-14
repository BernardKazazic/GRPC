package fer.rassus.lab1.Server.dao;

import fer.rassus.lab1.Server.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Sensor findByLongitudeAndLatitude(Double longitude, Double latitude);

}
