package dev.cyberjar.neurowatch.repository.civilian;

import dev.cyberjar.neurowatch.entity.Civilian;

import java.util.List;
import java.util.Optional;

public interface CivilianRepositoryCustom {

    Optional<Civilian> findAByImplantSerialNumber(String implantSerialNumber);

    List<Civilian> findAllByImplantLotNumber(int lotNumber);

    List<Civilian> findAllByImplantLotNumberGreaterThanEqual(int lotNumber);

    List<Civilian> findAllByImplantLotNumberLessThanEqual(int lotNumber);

}
