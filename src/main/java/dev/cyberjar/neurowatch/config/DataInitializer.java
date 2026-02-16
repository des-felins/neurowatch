package dev.cyberjar.neurowatch.config;

import dev.cyberjar.neurowatch.civilian.Civilian;
import dev.cyberjar.neurowatch.civilian.Implant;
import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.security.User;
import dev.cyberjar.neurowatch.civilian.repository.CivilianRepository;
import dev.cyberjar.neurowatch.implantmonitoringlog.repository.ImplantMonitoringLogRepository;
import dev.cyberjar.neurowatch.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("true")
    private boolean createTestUsers;


    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      CivilianRepository civilianRepository,
                                      ImplantMonitoringLogRepository logRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Create default admin and user
            createDefaultUsers(userRepository, passwordEncoder);

            // Insert sample data if enabled
            if (createTestUsers) {
                insertDataIntoCiviliansAndLogs(civilianRepository, logRepository);
            } else {
                log.info("Sample data creation is disabled");
            }

            log.info("Data initialization completed");

        };
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
                                                ImplantMonitoringLogRepository logRepository) {

        civilianRepository.deleteAll();
        logRepository.deleteAll();

        List<Implant> implants = new ArrayList<>();
        List<Civilian> civilians = new ArrayList<>();
        List<ImplantMonitoringLog> logs = new ArrayList<>();

        Point NYC_MIDTOWN = new Point(-73.9855, 40.7580);
        Point NYC_BROOKLYN = new Point(-73.9780, 40.6782);
        Point NYC_QUEENS = new Point(-73.7949, 40.7282);

        Point BOS_DOWNTOWN = new Point(-71.0589, 42.3601);
        Point PHL_CENTER = new Point(-75.1652, 39.9526);
        Point DC_DOWNTOWN = new Point(-77.0369, 38.9072);

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

            // map implantSerial -> civilianNationalId (for log generation)
            Map<String, String> implantToCivilian = civilians.stream()
                    .flatMap(c -> c.getImplants().stream().map(im -> Map.entry(im.getSerialNumber(), c.getNationalId())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // --- Logs ---
            // Create three “story arcs”:
            // 1) baseline (normal telemetry spread across cities and time)
            // 2) recall-likely (MechaMed limb lot 536 spikes)
            // 3) attack-likely (multivendor CPU spikes in same geo/time window)

            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

            // Baseline: 3 days, every 3 hours, for all implants.
            for (Implant implant : implants) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                Point city = pickHomeLocationForImplant(
                        implant,
                        NYC_MIDTOWN,
                        PHL_CENTER,
                        BOS_DOWNTOWN,
                        DC_DOWNTOWN);
                LocalDateTime start = now.minusDays(3);

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        start, 24, 180, // 24 points, 180 min step = every 3 hours
                        1.6, 18.0, 18.0, // power uW, cpu %, latency ms (baseline-ish)
                        0.6, 6.0, 5.0,   // jitter
                        city, 0.010       // location jitter
                );
            }

            // Baseline+ for a subset (more “rich” history): 7 days, every 4 hours
            List<Implant> richerHistory = implants.subList(0, Math.min(10, implants.size()));
            for (Implant implant : richerHistory) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                Point city = pickHomeLocationForImplant(implant,
                        NYC_MIDTOWN,
                        PHL_CENTER,
                        BOS_DOWNTOWN,
                        DC_DOWNTOWN);
                LocalDateTime start = now.minusDays(7);

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        start, 42, 240, // 42 points, 240 min step = every 4 hours for ~7 days
                        1.7, 20.0, 19.0,
                        0.7, 7.0, 6.0,
                        city, 0.012
                );
            }

            // Incident anchor time window for demos
            LocalDateTime incidentBase = now.minusDays(1).withHour(2).withMinute(0);

            // Recall-likely cluster: lot 536 (MechaMed limb) spikes hard.
            // Neural latency + CPU both high, tightly clustered.
            List<Implant> lot536 = implants.stream()
                    .filter(im -> im.getManufacturer().equals("MechaMed") && im.getLotNumber() == 536)
                    .toList();

            for (Implant implant : lot536) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        incidentBase.plusMinutes(10), 30, 2, // 30 points, every 2 min
                        6.8, 92.0, 160.0,  // strong anomaly
                        0.8, 4.0, 12.0,
                        NYC_BROOKLYN, 0.003
                );
            }

            // Attack-likely cluster: many different implants spike CPU in same place/time.
            // CPU high, latency elevated but not insane, power mostly normal (looks like external load/interference).
            List<Implant> attackVictims = implants.stream()
                    .filter(im -> !(im.getManufacturer().equals("MechaMed") && im.getLotNumber() == 536)) // exclude recall group
                    .limit(12)
                    .toList();

            for (Implant implant : attackVictims) {
                String natId = implantToCivilian.get(implant.getSerialNumber());
                if (natId == null) continue;

                addSeries(
                        logs, r,
                        implant.getSerialNumber(), natId,
                        incidentBase.plusMinutes(20), 20, 3, // 20 points, every 3 min
                        2.2, 96.0, 85.0,
                        0.5, 3.0, 10.0,
                        NYC_QUEENS, 0.004
                );
            }

            // A single “distant weirdness” outlier to test false positive handling
            Implant outlier = implants.getLast();
            String outlierNat = implantToCivilian.get(outlier.getSerialNumber());
            if (outlierNat != null) {
                addSeries(
                        logs, r,
                        outlier.getSerialNumber(), outlierNat,
                        now.minusDays(5).withHour(23).withMinute(15), 25, 4,
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

    private static Point pickHomeLocationForImplant(
            Implant im,
            Point nyc, Point bos, Point phl, Point dc
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
            LocalDateTime start,
            int points,
            int stepMinutes,
            double basePowerUw,
            double baseCpuPct,
            double baseLatencyMs,
            double powerJitter,
            double cpuJitter,
            double latencyJitter,
            Point center,
            double locationJitter
    ) {
        for (int i = 0; i < points; i++) {
            LocalDateTime ts = start.plusMinutes((long) i * stepMinutes);

            double power = clampMin(basePowerUw + randSigned(r) * powerJitter, 0.0);
            double cpu = clamp(baseCpuPct + randSigned(r) * cpuJitter, 0.0, 100.0);
            double latency = clampMin(baseLatencyMs + randSigned(r) * latencyJitter, 0.0);

            Point loc = jitterPoint(center, r, locationJitter);

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

    private static Point jitterPoint(Point center, Random r, double maxDelta) {
        double dx = randSigned(r) * maxDelta;
        double dy = randSigned(r) * maxDelta;
        return new Point(center.getX() + dx, center.getY() + dy);
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

}
