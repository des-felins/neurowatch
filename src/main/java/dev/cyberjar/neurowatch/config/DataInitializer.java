package dev.cyberjar.neurowatch.config;

import dev.cyberjar.neurowatch.civilian.Civilian;
import dev.cyberjar.neurowatch.civilian.Implant;
import dev.cyberjar.neurowatch.civilian.repository.CivilianRepository;
import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.implantmonitoringlog.repository.ImplantMonitoringLogRepository;
import dev.cyberjar.neurowatch.security.User;
import dev.cyberjar.neurowatch.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${app.create-test-users:true}")
    private boolean createTestUsers;

    @Value("${app.seed.yaml.enabled:false}")
    private boolean yamlSeedEnabled;

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      CivilianRepository civilianRepository,
                                      ImplantMonitoringLogRepository logRepository,
                                      PasswordEncoder passwordEncoder,
                                      Clock clock,
                                      @Value("${app.seed.yaml.location:classpath:/seed-data.yaml}") Resource seedResource) {
        return args -> {
            // Create default admin and user
            createDefaultUsers(userRepository, passwordEncoder);

            // 1) Try YAML seed (only if enabled + resource exists)
            if (yamlSeedEnabled && seedFromYamlIfPresent(seedResource, civilianRepository, logRepository)) {
                log.info("Data initialization completed (seeded from YAML)");
                return;
            }

            // Insert sample data if enabled
            if (createTestUsers) {
                insertDataIntoCiviliansAndLogs(civilianRepository, logRepository, clock);
            } else {
                log.info("Sample data creation is disabled");
            }

            log.info("Data initialization completed");

        };
    }

    private boolean seedFromYamlIfPresent(Resource resource, CivilianRepository civilianRepository, ImplantMonitoringLogRepository logRepository) {
        try {

            if (!resource.exists()) {
                log.info("No YAML seed found at {} (skipping)", resource);
                return false;
            }

            log.info("Loading seed data from {}", resource);

            // Intentionally unsafe for CVE demo purposes:
            // CVE-2022-1471 is related to SnakeYAML deserialization risks with Constructor.
            Yaml yaml = new Yaml(new Constructor(SeedData.class));

            SeedData seed;
            try (InputStream in = resource.getInputStream()) {
                seed = yaml.load(in);
            }

            if (seed == null) {
                log.warn("YAML seed file was empty (skipping)");
                return false;
            }

            civilianRepository.deleteAll();
            logRepository.deleteAll();

            if (seed.getCivilians() != null) {
                List<Civilian> civilians = new ArrayList<>();
                for (SeedCivilian sc : seed.getCivilians()) {
                    List<Implant> implants = new ArrayList<>();
                    if (sc.getImplants() != null) {
                        for (SeedImplant si : sc.getImplants()) {
                            implants.add(new Implant(
                                    si.getType(),
                                    si.getModel(),
                                    si.getVersion(),
                                    si.getManufacturer(),
                                    si.getLotNumber(),
                                    si.getSerialNumber(),
                                    si.getInstalledAt()
                            ));
                        }
                    }

                    civilians.add(new Civilian(
                            null,
                            sc.getLegalName(),
                            sc.getNationalId(),
                            sc.getBirthDate(),
                            sc.isCriminalRecord(),
                            sc.isUnderSurveillance(),
                            implants
                    ));
                }
                civilianRepository.saveAll(civilians);
                log.info("Seeded {} civilians from YAML", civilians.size());
            }

            if (seed.getLogs() != null) {
                List<ImplantMonitoringLog> logs = new ArrayList<>();
                for (SeedLog sl : seed.getLogs()) {
                    Instant ts = (sl.getTimestamp() == null || sl.getTimestamp().isBlank())
                            ? Instant.now()
                            : Instant.parse(sl.getTimestamp());

                    GeoJsonPoint location = null;
                    if (sl.getLocation() != null) {
                        // GeoJsonPoint(x=lon, y=lat)
                        location = new GeoJsonPoint(sl.getLocation().getLongitude(), sl.getLocation().getLatitude());
                    }

                    logs.add(new ImplantMonitoringLog(
                            null,
                            sl.getImplantSerialNumber(),
                            sl.getCivilianNationalId(),
                            ts,
                            sl.getPowerUsageUw(),
                            sl.getCpuUsagePct(),
                            sl.getNeuralLatencyMs(),
                            location
                    ));
                }
                logRepository.saveAll(logs);
                log.info("Seeded {} implant logs from YAML", logs.size());
            }

            return true;
        } catch (Exception e) {
            log.warn("Failed to load YAML seed from {} (falling back to generated data): {}",
                    resource, e.toString());
            return false;
        }
    }

    private void createDefaultUsers(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {

        if (!userRepository.existsByUsername("admin")) {

            User admin = new User(null, "admin",
                    passwordEncoder.encode("adminpass"),
                    "alicia.wu@udm.gv",
                    Set.of("ADMIN", "USER"));

            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {

            User user = new User(null, "user",
                    passwordEncoder.encode("userpass"),
                    "ian.black@udm.gv",
                    Set.of("USER"));

            userRepository.save(user);

        }
    }

    private void insertDataIntoCiviliansAndLogs(CivilianRepository civilianRepository,
                                                ImplantMonitoringLogRepository logRepository,
                                                Clock clock) {

        civilianRepository.deleteAll();
        logRepository.deleteAll();

        List<Implant> implants = new ArrayList<>();
        List<Civilian> civilians = new ArrayList<>();
        List<ImplantMonitoringLog> logs = new ArrayList<>();

        GeoJsonPoint NYC_MIDTOWN = new GeoJsonPoint(-73.9855, 40.7580);
        GeoJsonPoint NYC_BROOKLYN = new GeoJsonPoint(-73.9780, 40.6782);
        GeoJsonPoint NYC_QUEENS = new GeoJsonPoint(-73.7949, 40.7282);

        GeoJsonPoint BOS_DOWNTOWN = new GeoJsonPoint(-71.0589, 42.3601);
        GeoJsonPoint PHL_CENTER = new GeoJsonPoint(-75.1652, 39.9526);
        GeoJsonPoint DC_DOWNTOWN = new GeoJsonPoint(-77.0369, 38.9072);

        Random r = new Random(7331);

        if (civilianRepository.findAll().isEmpty()) {


            // Recall-likely group (tight correlation): MechaMed limb lot 536
            // These will spike neural latency + CPU in a narrow time window.
            implants.addAll(makeBatch(
                    "limb",
                    "Model-Dvb688",
                    "2.2",
                    "MechaMed",
                    536,
                    "MM-536-DVB-",
                    4,
                    "2025-03-21")
            );
            implants.addAll(makeBatch(
                    "limb",
                    "Model-Jtv413",
                    "1.3",
                    "MechaMed",
                    536,
                    "MM-536-JTV-",
                    2,
                    "2025-04-03")
            );

            // “Known-bug-ish” ocular lot 746 (SynthForge)
            implants.addAll(makeBatch(
                    "ocular",
                    "Model-gOq543",
                    "3.8",
                    "SynthForge",
                    746,
                    "SF-746-OCU-",
                    4,
                    "2024-01-17")
            );

            // NeuroCore cardiac lot 289 for variety
            implants.addAll(makeBatch(
                    "cardiac",
                    "Model-Gkf965",
                    "2.3",
                    "NeuroCore",
                    289,
                    "NC-289-CAR-",
                    4,
                    "2024-05-29")
            );

            // Mixed pool (normal background + “attack-likely” victims)
            implants.add(new Implant(
                    "ocular",
                    "Model-fXX373",
                    "1.2",
                    "NeuroCore",
                    617,
                    "NC-617-OCU-447327",
                    "2023-07-03")
            );
            implants.add(new Implant(
                    "cardiac",
                    "Model-OMt936",
                    "1.1",
                    "SynthForge",
                    141,
                    "SF-141-CAR-905785",
                    "2024-09-06")
            );
            implants.add(new Implant(
                    "limb",
                    "Model-Yjx053",
                    "3.8",
                    "MechaMed",
                    490,
                    "MM-490-LIM-984050",
                    "2024-02-11")
            );
            implants.add(new Implant(
                    "cardiac",
                    "Model-mUw025",
                    "2.8",
                    "MechaMed",
                    415,
                    "MM-415-CAR-226330",
                    "2023-07-04")
            );
            implants.add(new Implant(
                    "ocular",
                    "Model-mZd159",
                    "1.9",
                    "SynthForge",
                    664,
                    "SF-664-OCU-624181",
                    "2023-10-16")
            );
            implants.add(new Implant(
                    "cardiac",
                    "Model-lkh474",
                    "3.0",
                    "SynthForge",
                    197,
                    "SF-197-CAR-941730",
                    "2024-02-14")
            );
            implants.add(new Implant(
                    "ocular",
                    "Model-zNd426",
                    "1.5",
                    "NeuroCore",
                    816,
                    "NC-816-OCU-566493",
                    "2024-05-07")
            );
            implants.add(new Implant(
                    "cardiac",
                    "Model-StO778",
                    "2.3",
                    "NeuroCore",
                    459,
                    "NC-459-CAR-107741",
                    "2024-06-21")
            );
            implants.add(new Implant(
                    "limb",
                    "Model-VVo800",
                    "3.8",
                    "NeuroCore",
                    817,
                    "NC-817-LIM-893238",
                    "2024-12-07")
            );
            implants.add(new Implant(
                    "ocular",
                    "Model-SiT679",
                    "1.5",
                    "MechaMed",
                    434,
                    "MM-434-OCU-306310",
                    "2025-06-08")
            );
            implants.add(new Implant(
                    "cardiac",
                    "Model-ooV123",
                    "1.7",
                    "MechaMed",
                    103,
                    "MM-103-CAR-283686",
                    "2023-11-24")
            );
            implants.add(new Implant(
                    "ocular",
                    "Model-BCf487",
                    "1.7",
                    "MechaMed",
                    124,
                    "MM-124-OCU-629496",
                    "2024-05-29")
            );


            // names/national IDs: semi-random but stable
            List<String> names = List.of(
                    "Aarav Das", "Paula Lin", "Aelita Fang", "Talon Minx", "Felicia Lee",
                    "Yllo Hill", "Nicholas Ramirez", "Rin Morse", "Heather Huang", "Amir Morgan",
                    "Sanae Kuroda", "Milan Varga", "Noura Haddad", "Ilya Petrov", "Jun Park",
                    "Eva Kowalski", "Noah van Dijk", "Lina Moretti", "Samira Khan", "Tomás Silva",
                    "Kei Tanaka", "Marta Nowak", "Omar Reyes", "Zoe Novak"
            );

            List<String> ids = List.of(
                    "Ni-96751543-BP", "NP-59909166-Wg", "gQ-01247486-nk", "Ww-33252326-jv", "dJ-71032254-JQ",
                    "Ew-42902984-rX", "Zy-82483905-hw", "fI-88901036-kD", "YD-99086969-CP", "MP-66879496-vg",
                    "Qm-10488329-xA", "Jp-22019411-pL", "Rt-39012004-fQ", "Vb-48100291-qS", "Ls-58291004-hK",
                    "Az-60439110-eR", "NL-77102010-vD", "It-88200194-mT", "Pk-91334022-sN", "Br-02910233-tS",
                    "Jt-14099201-kE", "Pl-23011049-mN", "Mx-39099211-oR", "Zq-40100321-zN"
            );

            // assign implants to civilians (1–2 each)
            int implantCursor = 0;

            for (int i = 0; i < 19; i++) {
                boolean criminalRecord = (i % 7 == 0);         // some have records
                boolean underSurveillance = (i % 5 == 0);      // some are watched

                List<Implant> owned = new ArrayList<>();
                owned.add(implants.get(implantCursor++));

                // every 3rd civilian gets a second implant if available
                if (i % 3 == 0 && implantCursor < implants.size()) {
                    owned.add(implants.get(implantCursor++));
                }

                civilians.add(new Civilian(
                        null,
                        names.get(i),
                        ids.get(i),
                        pickBirthDate(i),
                        criminalRecord,
                        underSurveillance,
                        owned
                ));
            }

            civilianRepository.saveAll(civilians);

        }


        if (logRepository.findAll().isEmpty()) {


            Map<String, String> implantToCivilian = civilians.stream()
                    .flatMap(c -> c.getImplants().stream().map(im -> Map.entry(im.getSerialNumber(), c.getNationalId())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Instant now = clock.instant();

            // Baseline: 3 days, every 3 hours, for all implants.
            for (Implant implant : implants) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                GeoJsonPoint city = pickHomeLocationForImplant(
                        implant,
                        NYC_MIDTOWN,
                        PHL_CENTER,
                        BOS_DOWNTOWN,
                        DC_DOWNTOWN);

                // Instant start
                Instant start = now.minus(Duration.ofDays(3));

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        start, 24, 180,
                        1.6, 18.0, 18.0,
                        0.6, 6.0, 5.0,
                        city, 0.010
                );
            }

            // Baseline+ for subset: 7 days, every 4 hours
            List<Implant> richerHistory = implants.subList(0, Math.min(10, implants.size()));
            for (Implant implant : richerHistory) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                GeoJsonPoint city = pickHomeLocationForImplant(implant,
                        NYC_MIDTOWN,
                        PHL_CENTER,
                        BOS_DOWNTOWN,
                        DC_DOWNTOWN);

                Instant start = now.minus(Duration.ofDays(7));

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        start, 42, 240,
                        1.7, 20.0, 19.0,
                        0.7, 7.0, 6.0,
                        city, 0.012
                );
            }

            //  Incident anchor: yesterday at 02:00 UTC
            Instant incidentBase = ZonedDateTime.ofInstant(now, ZoneOffset.UTC)
                    .minusDays(1)
                    .withHour(2).withMinute(0).withSecond(0).withNano(0)
                    .toInstant();

            // Recall-likely cluster: lot 536 spikes
            List<Implant> lot536 = implants.stream()
                    .filter(im -> im.getManufacturer().equals("MechaMed") && im.getLotNumber() == 536)
                    .toList();

            for (Implant implant : lot536) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        incidentBase.plus(Duration.ofMinutes(10)), 30, 2,
                        6.8, 92.0, 160.0,
                        0.8, 4.0, 12.0,
                        NYC_BROOKLYN, 0.003
                );
            }

            // Attack-likely cluster
            List<Implant> attackVictims = implants.stream()
                    .filter(im -> !(im.getManufacturer().equals("MechaMed") && im.getLotNumber() == 536))
                    .limit(12)
                    .toList();

            for (Implant implant : attackVictims) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        incidentBase.plus(Duration.ofMinutes(20)), 20, 3,
                        2.2, 96.0, 85.0,
                        0.5, 3.0, 10.0,
                        NYC_QUEENS, 0.004
                );
            }

            // Outlier: fixed-ish time in UTC (5 days ago at 23:15 UTC)
            Implant outlier = implants.getLast();
            String outlierNat = implantToCivilian.get(outlier.getSerialNumber());
            if (outlierNat != null) {
                Instant outlierStart = ZonedDateTime.ofInstant(now, ZoneOffset.UTC)
                        .minusDays(5)
                        .withHour(23).withMinute(15).withSecond(0).withNano(0)
                        .toInstant();

                addSeries(
                        logs, r,
                        outlier.getSerialNumber(), outlierNat,
                        outlierStart, 25, 4,
                        3.5, 55.0, 200.0,
                        0.7, 8.0, 18.0,
                        PHL_CENTER, 0.006
                );
            }

            logRepository.saveAll(logs);

        }
    }


    // Helper methods

    private static List<Implant> makeBatch(
            String type, String model, String version, String manufacturer,
            int lotNumber, String serialPrefix, int count, String installedAt
    ) {
        List<Implant> result = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            String serial = serialPrefix + (1000 + i);
            result.add(new Implant(type, model, version, manufacturer, lotNumber, serial, installedAt));
        }
        return result;
    }

    private static String pickBirthDate(int i) {

        int year = 1968 + (i * 2) % 32;      // 1968..1999
        int month = 1 + (i * 3) % 12;        // 1..12
        int day = 1 + (i * 7) % 27;          // 1..28
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private static GeoJsonPoint pickHomeLocationForImplant(
            Implant im,
            GeoJsonPoint nyc, GeoJsonPoint bos, GeoJsonPoint phl, GeoJsonPoint dc
    ) {
        if (im.getManufacturer().equals("MechaMed") && im.getLotNumber() == 536) return nyc;

        int bucket = Math.abs(im.getSerialNumber().hashCode()) % 4;
        return switch (bucket) {
            case 0 -> nyc;
            case 1 -> bos;
            case 2 -> phl;
            default -> dc;
        };
    }

    private static void addSeries(
            List<ImplantMonitoringLog> logs,
            Random r,
            String implantSerial,
            String civilianNationalId,
            Instant start,        // ✅ Instant
            int points,
            int stepMinutes,
            double basePowerUw,
            double baseCpuPct,
            double baseLatencyMs,
            double powerJitter,
            double cpuJitter,
            double latencyJitter,
            GeoJsonPoint center,
            double locationJitter
    ) {
        for (int i = 0; i < points; i++) {
            Instant ts = start.plus(Duration.ofMinutes((long) i * stepMinutes)); // ✅ Instant math

            double power = clampMin(basePowerUw + randSigned(r) * powerJitter, 0.0);
            double cpu = clamp(baseCpuPct + randSigned(r) * cpuJitter, 0.0, 100.0);
            double latency = clampMin(baseLatencyMs + randSigned(r) * latencyJitter, 0.0);

            GeoJsonPoint loc = jitterPoint(center, r, locationJitter);

            logs.add(new ImplantMonitoringLog(
                    null,
                    implantSerial,
                    civilianNationalId,
                    ts,
                    power,
                    cpu,
                    latency,
                    loc
            ));
        }
    }
    private static GeoJsonPoint jitterPoint(GeoJsonPoint center, Random r, double maxDelta) {
        double dx = randSigned(r) * maxDelta;
        double dy = randSigned(r) * maxDelta;
        return new GeoJsonPoint(center.getX() + dx, center.getY() + dy);
    }

    private static double randSigned(Random r) {
        return (r.nextDouble() * 2.0) - 1.0; // -1..+1
    }

    private static double clampMin(double v, double min) {
        return Math.max(min, v);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }


    // ---------------- YAML seed DTOs (SnakeYAML target types) ----------------

    public static class SeedData {
        private List<SeedCivilian> civilians;
        private List<SeedLog> logs;

        public List<SeedCivilian> getCivilians() { return civilians; }
        public void setCivilians(List<SeedCivilian> civilians) { this.civilians = civilians; }

        public List<SeedLog> getLogs() { return logs; }
        public void setLogs(List<SeedLog> logs) { this.logs = logs; }
    }

    public static class SeedCivilian {
        private String legalName;
        private String nationalId;
        private String birthDate; // yyyy-MM-dd
        private boolean criminalRecord;
        private boolean underSurveillance;
        private List<SeedImplant> implants;

        public String getLegalName() { return legalName; }
        public void setLegalName(String legalName) { this.legalName = legalName; }

        public String getNationalId() { return nationalId; }
        public void setNationalId(String nationalId) { this.nationalId = nationalId; }

        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

        public boolean isCriminalRecord() { return criminalRecord; }
        public void setCriminalRecord(boolean criminalRecord) { this.criminalRecord = criminalRecord; }

        public boolean isUnderSurveillance() { return underSurveillance; }
        public void setUnderSurveillance(boolean underSurveillance) { this.underSurveillance = underSurveillance; }

        public List<SeedImplant> getImplants() { return implants; }
        public void setImplants(List<SeedImplant> implants) { this.implants = implants; }
    }

    public static class SeedImplant {
        private String type;
        private String model;
        private String version;
        private String manufacturer;
        private int lotNumber;
        private String serialNumber;
        private String installedAt; // yyyy-MM-dd

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getManufacturer() { return manufacturer; }
        public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

        public int getLotNumber() { return lotNumber; }
        public void setLotNumber(int lotNumber) { this.lotNumber = lotNumber; }

        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

        public String getInstalledAt() { return installedAt; }
        public void setInstalledAt(String installedAt) { this.installedAt = installedAt; }
    }

    public static class SeedLog {
        private String civilianNationalId;
        private String implantSerialNumber;
        private String timestamp; // ISO-8601 instant, e.g. 2025-02-25T15:00:00Z
        private double powerUsageUw;
        private double cpuUsagePct;
        private double neuralLatencyMs;
        private SeedLocation location;

        public String getCivilianNationalId() { return civilianNationalId; }
        public void setCivilianNationalId(String civilianNationalId) { this.civilianNationalId = civilianNationalId; }

        public String getImplantSerialNumber() { return implantSerialNumber; }
        public void setImplantSerialNumber(String implantSerialNumber) { this.implantSerialNumber = implantSerialNumber; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public double getPowerUsageUw() { return powerUsageUw; }
        public void setPowerUsageUw(double powerUsageUw) { this.powerUsageUw = powerUsageUw; }

        public double getCpuUsagePct() { return cpuUsagePct; }
        public void setCpuUsagePct(double cpuUsagePct) { this.cpuUsagePct = cpuUsagePct; }

        public double getNeuralLatencyMs() { return neuralLatencyMs; }
        public void setNeuralLatencyMs(double neuralLatencyMs) { this.neuralLatencyMs = neuralLatencyMs; }

        public SeedLocation getLocation() { return location; }
        public void setLocation(SeedLocation location) { this.location = location; }
    }

    public static class SeedLocation {
        private double latitude;
        private double longitude;

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }

}
