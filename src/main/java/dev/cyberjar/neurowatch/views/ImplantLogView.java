package dev.cyberjar.neurowatch.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import dev.cyberjar.neurowatch.entity.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.entity.MonitoringStats;
import dev.cyberjar.neurowatch.service.ImplantMonitoringLogService;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Route(value = "logs", layout = MainLayout.class)
@PermitAll
public class ImplantLogView extends VerticalLayout {

    private final ImplantMonitoringLogService logService;

    private final TextField serialN = new TextField("Serial #");
    private final DatePicker fromPicker = new DatePicker("From date");
    private final Button searchBtn = new Button("Search");

    private final Grid<ImplantMonitoringLog> grid = new Grid<>(ImplantMonitoringLog.class, false);

    private final Span stats = new Span();

    public ImplantLogView(ImplantMonitoringLogService logService) {
        this.logService = logService;

        buildFilterBar();
        buildGrid();

        add(new HorizontalLayout(serialN, fromPicker, searchBtn),
                grid,
                new Hr(),
                stats);

        setSizeFull();
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

        LocalDateTime from = fromPicker.getValue() == null
                ? LocalDate.MIN.atStartOfDay()
                : fromPicker.getValue().atStartOfDay();


        List<ImplantMonitoringLog> logs =
                logService.findByImplantSerialNumberAndAfter(serial, from);
        grid.setItems(logs);


        MonitoringStats s =
                logService.aggregateStatsForImplantForPeriod(serial, from,
                        LocalDateTime.now());

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
}
