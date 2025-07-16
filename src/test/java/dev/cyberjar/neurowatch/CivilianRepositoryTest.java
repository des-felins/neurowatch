package dev.cyberjar.neurowatch;

import dev.cyberjar.neurowatch.entity.Civilian;
import dev.cyberjar.neurowatch.entity.Implant;
import dev.cyberjar.neurowatch.repository.civilian.CivilianRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataMongoTest
class CivilianRepositoryTest {


    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    @Autowired
    private CivilianRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void populateWithData() {

        mongoTemplate.createCollection("civilians");

        List<Implant> implants = new ArrayList<>();

        implants.add(new Implant("limb", "Model-Dvb688", "2.2", "MechaMed", 536, "742669", "2025-03-21"));
        implants.add(new Implant("ocular", "Model-SiT679", "1.5", "MechaMed", 434, "306310", "2025-06-08"));
        implants.add(new Implant("limb", "Model-Jtv413", "1.3", "MechaMed", 536, "470917", "2025-04-03"));

        List<Civilian> civilians = new ArrayList<>();
        civilians.add(new Civilian(null, "Rin Morse", "fI-88901036-kD", "1985-08-01", true, false, List.of(implants.get(0))));
        civilians.add(new Civilian(null, "Heather Huang", "YD-99086969-CP", "1994-04-16", false, true, List.of(implants.get(1))));
        civilians.add(new Civilian(null, "Amir Morgan", "MP-66879496-vg", "1975-06-26", false, true, List.of(implants.get(2))));

        mongoTemplate.insert(civilians, Civilian.class);

    }

    @AfterEach
    void cleanUp() {
        mongoTemplate.dropCollection("civilians");
    }

    @Test
    void shouldFindCivilianByNationalId() {
        List<Civilian> civilian = repository.findByNationalId("fI-88901036-kD");
        String name = "Rin Morse";
        assertEquals(name, civilian.getFirst().getLegalName());
    }

    @Test
    void shouldFindCiviliansByLotNumber() {
        List<Civilian> civilians = repository.findAllByImplantLotNumber(536);
        int expected = 2;
        assertEquals(expected, civilians.size());
    }

}