package dev.cyberjar.neurowatch.civilian.repository;

import dev.cyberjar.neurowatch.civilian.Civilian;
import org.springframework.data.domain.Pageable;
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
    public List<Civilian> findAllByImplantLotNumber(int lotNumber, Pageable pageable) {
        Query query = new Query(Criteria.where("implants.lotNumber").is(lotNumber)).with(pageable);
        return mongoTemplate.find(query, Civilian.class);
    }

    @Override
    public List<Civilian> findAllByImplantLotNumberGreaterThanEqual(int lotNumber, Pageable pageable) {
        Query query = new Query(Criteria.where("implants.lotNumber").gte(lotNumber)).with(pageable);
        return mongoTemplate.find(query, Civilian.class);
    }

    @Override
    public List<Civilian> findAllByImplantLotNumberLessThanEqual(int lotNumber, Pageable pageable) {
        Query query = new Query(Criteria.where("implants.lotNumber").lte(lotNumber)).with(pageable);
        return mongoTemplate.find(query, Civilian.class);
    }

    @Override
    public long countByLotNumber(int lotNumber) {
        Query query = new Query(Criteria.where("implants.lotNumber").is(lotNumber));
        return mongoTemplate.count(query, Civilian.class);
    }

    @Override
    public long countByLotNumberGreaterThanEqual(int lotNumber) {
        Query query = new Query(Criteria.where("implants.lotNumber").gte(lotNumber));
        return mongoTemplate.count(query, Civilian.class);
    }

    @Override
    public long countByLotNumberLessThanEqual(int lotNumber) {
        Query query = new Query(Criteria.where("implants.lotNumber").lte(lotNumber));
        return mongoTemplate.count(query, Civilian.class);
    }
}
