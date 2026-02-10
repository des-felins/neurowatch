package dev.cyberjar.neurowatch.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import dev.cyberjar.neurowatch.entity.ImplantMonitoringLog;
import dev.cyberjar.neurowatch.service.LiveLogBus;
import jakarta.annotation.security.PermitAll;
import reactor.core.Disposable;

import java.util.Collections;
import java.util.LinkedList;

@Route(value = "live-logs", layout = MainLayout.class)
@PermitAll
public class LiveLogsView extends VerticalLayout implements AfterNavigationObserver {

    private final LiveLogBus bus;

    private final LinkedList<ImplantMonitoringLog> buffer = new LinkedList<>();
    private final ListDataProvider<ImplantMonitoringLog> data =
            new ListDataProvider<>(Collections.synchronizedList(buffer));

    private final Grid<ImplantMonitoringLog> grid = new Grid<>(ImplantMonitoringLog.class, false);
    private final Button clearBtn = new Button("Clear");
    private final Checkbox autoScroll = new Checkbox("Auto-scroll", true);

    private volatile boolean paused = false;
    private Disposable subscription;

    public LiveLogsView(LiveLogBus bus) {
        this.bus = bus;
        setSizeFull();
        configureGrid();
        add(buildToolbar(), grid);
        expand(grid);
    }

    private Component buildToolbar() {

        clearBtn.addClickListener(e -> {
            buffer.clear();
            data.refreshAll();
        });
        HorizontalLayout bar = new HorizontalLayout(clearBtn, autoScroll);
        bar.setAlignItems(Alignment.CENTER);
        bar.setWidthFull();
        bar.setJustifyContentMode(JustifyContentMode.START);
        return bar;
    }

    private void configureGrid() {

        grid.setDataProvider(data);
        grid.addColumn(ImplantMonitoringLog::getTimestamp).setHeader("Time").setAutoWidth(true);
        grid.addColumn(ImplantMonitoringLog::getImplantSerialNumber).setHeader("Serial").setAutoWidth(true);
        grid.addColumn(ImplantMonitoringLog::getCivilianNationalId).setHeader("National ID").setAutoWidth(true);
        grid.addColumn(l -> String.format("%.1f µW", l.getPowerUsageUw())).setHeader("Power").setAutoWidth(true);
        grid.addColumn(l -> String.format("%.1f %%", l.getCpuUsagePct())).setHeader("CPU").setAutoWidth(true);
        grid.addColumn(l -> String.format("%.2f ms", l.getNeuralLatencyMs())).setHeader("Latency").setAutoWidth(true);
        grid.addColumn(l -> l.getLocation() != null ? (l.getLocation().getY() + ", " + l.getLocation().getX()) : "")
                .setHeader("Lat, Lon").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setHeightFull();

    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

        // subscribe when the view becomes active
        subscription = bus.stream().subscribe(log ->
                getUI().ifPresent(ui -> ui.access(() -> {
                        buffer.addFirst(log);              // newest first
                        // trim to avoid unbounded growth (keep last 5000 rows)
                        if (buffer.size() > 5000) buffer.removeLast();
                        data.refreshAll();
                        if (autoScroll.getValue() && !buffer.isEmpty()) {
                            grid.scrollToIndex(0);
                        }
                }))
        );
    }

    @Override
    public void onDetach(DetachEvent detachEvent) {
        if (subscription != null) {
            subscription.dispose();
            subscription = null;
        }
    }

}
