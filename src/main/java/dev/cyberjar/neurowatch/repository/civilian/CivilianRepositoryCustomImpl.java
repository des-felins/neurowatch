package dev.cyberjar.neurowatch.repository.civilian;

import dev.cyberjar.neurowatch.entity.Civilian;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CivilianRepositoryCustomImpl implements CivilianRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public CivilianRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public Optional<Civilian> findAByImplantSerialNumber(String implantSerialNumber) {
        Query query = new Query(Criteria.where("implants.serialNumber").is(implantSerialNumber));
        return Optional.ofNullable(mongoTemplate.findOne(query, Civilian.class));
    }

    @Override
    public List<Civilian> findAllByImplantLotNumber(int lotNumber) {
        Query query = new Query(Criteria.where("implants.lotNumber").is(lotNumber));
        return mongoTemplate.find(query, Civilian.class);
    }

    @Override
    public List<Civilian> findAllByImplantLotNumberGreaterThanEqual(int lotNumber) {
        Query query = new Query(Criteria.where("implants.lotNumber").gte(lotNumber));
        return mongoTemplate.find(query, Civilian.class);
    }

    @Override
    public List<Civilian> findAllByImplantLotNumberLessThanEqual(int lotNumber) {
        Query query = new Query(Criteria.where("implants.lotNumber").lte(lotNumber));
        return mongoTemplate.find(query, Civilian.class);
    }
}
