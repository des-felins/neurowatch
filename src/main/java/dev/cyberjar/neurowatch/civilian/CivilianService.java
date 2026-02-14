package dev.cyberjar.neurowatch.civilian;

import dev.cyberjar.neurowatch.civilian.repository.CivilianRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CivilianService {

    private final CivilianRepository civilianRepository;

    public CivilianService(CivilianRepository civilianRepository) {
        this.civilianRepository = civilianRepository;
    }

    public Civilian saveCivilian(Civilian civilian) {
        return civilianRepository.save(civilian);
    }

    public Civilian updateCivilian(Civilian civilian) {
        Civilian civ = civilianRepository.findById(civilian.getId()).orElseThrow();
        return civilianRepository.save(civilian);

    }

    public Civilian addImplantToCivilian(Civilian civilian, Implant implant) {
        Civilian civ = civilianRepository.findById(civilian.getId()).orElseThrow();
        civ.getImplants().add(implant);
        return civilianRepository.save(civ);

    }

    public Civilian getCivilianById(String id) {
        return civilianRepository.findById(id).orElseThrow();
    }

    public void deleteCivilian(Civilian civilian) {
        civilianRepository.delete(civilian);
    }

    public void deleteAllCivilians() {
        civilianRepository.deleteAll();
    }

    public List<Civilian> getCiviliansByNationalId(int offset, int limit, String nationalId) {
        Pageable pageable = getPageable(offset, limit);
        return civilianRepository.findByNationalId(nationalId, pageable).getContent();
    }

    public List<Civilian> getCivilians(int offset, int limit) {
        Pageable pageable = getPageable(offset, limit);
        return civilianRepository.findAll(pageable).getContent();
    }

    public List<Civilian> getCiviliansByLotNumber(int offset, int limit, int lotNumber) {
        Pageable pageable = getPageable(offset, limit);
        return civilianRepository.findAllByImplantLotNumber(lotNumber, pageable);
    }

    public List<Civilian> getCiviliansByLotNumberGreaterOrEqual(int offset, int limit, int lotNumber) {
        Pageable pageable = getPageable(offset, limit);
        return civilianRepository.findAllByImplantLotNumberGreaterThanEqual(lotNumber, pageable);
    }

    public List<Civilian> getCiviliansByLotNumberLessOrEqual(int offset, int limit, int lotNumber) {
        Pageable pageable = getPageable(offset, limit);
        return civilianRepository.findAllByImplantLotNumberLessThanEqual(lotNumber, pageable);
    }

    public long countByLotNumber(Integer lotNumber) {
        return civilianRepository.countByLotNumber(lotNumber);
    }

    public long countByLotNumberGreaterOrEqual(Integer lotNumber) {
        return civilianRepository.countByLotNumberGreaterThanEqual(lotNumber);
    }

    public long countByLotNumberLessOrEqual(Integer lotNumber) {
        return civilianRepository.countByLotNumberLessThanEqual(lotNumber);
    }

    public long countByNationalId(String nationalId) {
        return civilianRepository.countByNationalId(nationalId);
    }

    public long countCivilians() {
        return civilianRepository.count();
    }

    private Pageable getPageable(int offset, int limit) {
        int page = offset / limit;
        return PageRequest.of(page, limit, Sort.by("nationalId").ascending());
    }

}



