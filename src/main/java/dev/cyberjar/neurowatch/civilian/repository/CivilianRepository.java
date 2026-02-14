package dev.cyberjar.neurowatch.civilian.repository;

import dev.cyberjar.neurowatch.civilian.Civilian;
import dev.cyberjar.neurowatch.civilian.CivilianSummary;
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