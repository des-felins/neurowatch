package dev.cyberjar.neurowatch.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;
import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.implantmonitoringlog.ImplantMonitoringLogService;
import dev.cyberjar.neurowatch.implantmonitoringlog.dto.MonitoringStats;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Route(value = "logs", layout = MainLayout.class)
@PermitAll
public class ImplantLogView extends VerticalLayout {

    private final ImplantMonitoringLogService logService;

    private static final ZoneId USER_ZONE = ZoneId.of("Europe/Amsterdam");

    private final TextField serialN = new TextField("Serial #");
    private final DatePicker fromPicker = new DatePicker("From date");
    private final Button searchBtn = new Button("Search");

    private final Grid<ImplantMonitoringLog> grid = new Grid<>(ImplantMonitoringLog.class, false);

    private final Span stats = new Span();

    public ImplantLogView(ImplantMonitoringLogService logService) {
        this.logService = logService;

        buildFilterBar();
        buildGrid();

        HorizontalLayout topBar = new HorizontalLayout(serialN, fromPicker, searchBtn);
        topBar.setDefaultVerticalComponentAlignment(Alignment.END);


        if (hasRole("ROLE_ADMIN")) {
            topBar.add(buildAdminImportControls());
        }


        add(topBar,
                grid,
                new Hr(),
                stats);

        setSizeFull();
    }


    private Component buildAdminImportControls() {
        UI ui = UI.getCurrent();

        // Capture serial safely (upload handler runs outside UI lock).
        AtomicReference<String> currentSerial = new AtomicReference<>("");
        currentSerial.set(serialN.getValue() == null ? "" : serialN.getValue().trim());
        serialN.addValueChangeListener(
                e -> currentSerial.set(e.getValue() == null ? "" : e.getValue().trim()));

        Upload upload = new Upload(
                UploadHandler.inMemory((_, data) -> {
                    String serial = currentSerial.get();
                    if (serial.isBlank()) {
                        throw new IllegalStateException("Enter an implant Serial # first.");
                    }

                    try {
                        var result = logService.importLogsForCivilian(serial, new ByteArrayInputStream(data));

                        ui.access(() -> {
                            Notification.show(
                                    "Imported " + result.logsImported() + " logs for serial " + serial,
                                    4000,
                                    Notification.Position.MIDDLE
                            );
                            refresh();
                        });
                    } catch (Exception ex) {
                        ui.access(() -> Notification.show(
                                "Import failed: " + ex.getMessage(),
                                6000,
                                Notification.Position.MIDDLE
                        ));
                        throw ex;
                    }
                })
        );

        upload.addClassName("inline-upload");
        upload.setAcceptedFileTypes(".yaml", ".yml");
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);
        upload.setUploadButton(new Button("Import Logs (YAML)"));
        upload.setWidth("24rem");

        upload.addFileRejectedListener(ev ->
                Notification.show(ev.getErrorMessage(), 5000, Notification.Position.MIDDLE)
        );

        return upload;
    }

    private void buildGrid() {
        grid.addColumn(ImplantMonitoringLog::getTimestamp).setHeader("Timestamp")
                .setAutoWidth(true).setSortable(true);
        grid.addColumn(ImplantMonitoringLog::getPowerUsageUw).setHeader("Pwr µW");
        grid.addColumn(ImplantMonitoringLog::getCpuUsagePct).setHeader("CPU %");
        grid.addColumn(ImplantMonitoringLog::getNeuralLatencyMs).setHeader("Latency ms");
        grid.setHeight("300px");
        grid.addClassName("wrap-grid");
    }

    private void buildFilterBar() {
        fromPicker.setValue(LocalDate.now().minusDays(7));
        searchBtn.addClickListener(e -> refresh());
    }

    private void refresh() {
        String serial = serialN.getValue().trim();
        if (serial.isBlank()) {
            grid.setItems(List.of());
            stats.setText("Enter a serial number.");
            return;
        }

        Instant from = (fromPicker.getValue() == null
                ? LocalDate.now(USER_ZONE).minusYears(100).atStartOfDay(USER_ZONE)
                : fromPicker.getValue().atStartOfDay(USER_ZONE)
        ).toInstant();


        List<ImplantMonitoringLog> logs =
                logService.findByImplantSerialNumberAndAfter(serial, from);
        grid.setItems(logs);


        MonitoringStats s =
                logService.aggregateStatsForImplantForPeriod(serial, from,
                        Instant.now());

        if (s != null) {
            stats.setText(String.format(
                    "Avg power: %.2f µW   •   Avg CPU: %.2f %%   •   Avg latency: %.2f ms",
                    s.avgPowerUsageUw(),
                    s.avgCpuUsagePct(),
                    s.avgNeuralLatencyMs()));
        } else {
            stats.setText("No stats for the selected period.");
        }
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }


}
