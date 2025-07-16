package dev.cyberjar.neurowatch.service;

import dev.cyberjar.neurowatch.entity.Civilian;
import dev.cyberjar.neurowatch.entity.Implant;
import dev.cyberjar.neurowatch.repository.civilian.CivilianRepository;
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

    public List<Civilian> getCivilianByNationalId(String nationalId) {
        return civilianRepository.findByNationalId(nationalId);
    }

    public List<Civilian> getAllCivilians() {
        return civilianRepository.findAll();
    }

    public List<Civilian> getCiviliansByLotNumber(int lotNumber) {
        return civilianRepository.findAllByImplantLotNumber(lotNumber);
    }

    public List<Civilian> getCiviliansByLotNumberGreaterOrEqual(int lotNumber) {
        return civilianRepository.findAllByImplantLotNumberGreaterThanEqual(lotNumber);
    }

    public List<Civilian> getCiviliansByLotNumberLessOrEqual(int lotNumber) {
        return civilianRepository.findAllByImplantLotNumberLessThanEqual(lotNumber);
    }

    public void deleteCivilian(Civilian civilian) {
        civilianRepository.delete(civilian);
    }

    public void deleteAllCivilians() {
        civilianRepository.deleteAll();
    }


}



