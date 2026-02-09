package dev.cyberjar.neurowatch.repository.civilian;

import dev.cyberjar.neurowatch.entity.Civilian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CivilianRepositoryCustom {

    Optional<Civilian> findAByImplantSerialNumber(String implantSerialNumber);

    List<Civilian> findAllByImplantLotNumber(int lotNumber, Pageable pageable);

    List<Civilian> findAllByImplantLotNumberGreaterThanEqual(int lotNumber, Pageable pageable);

    List<Civilian> findAllByImplantLotNumberLessThanEqual(int lotNumber, Pageable pageable);

    long countByLotNumber(int lotNumber);
    long countByLotNumberGreaterThanEqual(int lotNumber);
    long countByLotNumberLessThanEqual(int lotNumber);

}
