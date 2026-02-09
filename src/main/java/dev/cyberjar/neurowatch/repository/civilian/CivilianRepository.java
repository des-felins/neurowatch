package dev.cyberjar.neurowatch.repository.civilian;

import dev.cyberjar.neurowatch.entity.Civilian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CivilianRepository extends MongoRepository<Civilian, String>, CivilianRepositoryCustom {

    Optional<Civilian> findById(String id);

    Page<Civilian> findByNationalId(String nationalId, Pageable pageable);

    long countByNationalId(String nationalId);

    boolean existsById(String id);

    boolean existsByNationalId(String nationalId);

    List<CivilianSummary> findAllByUnderSurveillance(boolean underSurveillance);



}