package fer.rassus.lab1.Server.dao;

import fer.rassus.lab1.Server.domain.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findBySensor_Id(Long id);
}
