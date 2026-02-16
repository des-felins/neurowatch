package dev.cyberjar.neurowatch.implantmonitoringlog.repository;

import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


public interface ImplantMonitoringLogRepository extends MongoRepository<ImplantMonitoringLog, String>, ImplantMonitoringLogRepositoryCustom {

    List<ImplantMonitoringLog> findByImplantSerialNumber(String implantSerialNumber);

    List<ImplantMonitoringLog> findByImplantSerialNumberAndTimestampAfter(String implantSerialNumber,
                                                                          Instant timestamp);

    List<ImplantMonitoringLog> findByImplantSerialNumberAndTimestampBetween(String implantSerialNumber,
                                                                            Instant timestampFrom,
                                                                            Instant timestampTo);

}
