package dev.cyberjar.neurowatch.repository.implantmonitoring;

import dev.cyberjar.neurowatch.entity.ImplantMonitoringLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ImplantMonitoringLogRepository extends MongoRepository<ImplantMonitoringLog, String>, ImplantMonitoringLogRepositoryCustom {

    List<ImplantMonitoringLog> findByImplantSerialNumber(String implantSerialNumber);

    List<ImplantMonitoringLog> findByImplantSerialNumberAndTimestampAfter(String implantSerialNumber,
                                                                          LocalDateTime timestamp);

    List<ImplantMonitoringLog> findByImplantSerialNumberAndTimestampBetween(String implantSerialNumber,
                                                                            LocalDateTime timestampFrom,
                                                                            LocalDateTime timestampTo);

}
