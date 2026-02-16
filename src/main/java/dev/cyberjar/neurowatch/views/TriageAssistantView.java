package dev.cyberjar.neurowatch.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import dev.cyberjar.neurowatch.ai.IncidentTriageService;
import dev.cyberjar.neurowatch.ai.domain.EstimatedBlastRadius;
import dev.cyberjar.neurowatch.ai.domain.IncidentCase;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@Route(value = "assistant", layout = MainLayout.class)
@PermitAll
public class TriageAssistantView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(TriageAssistantView.class);


    private final IncidentTriageService triageService;
    private final TextArea prompt = new TextArea("Triage request");
    private final Button run = new Button("Run triage");

    private final VerticalLayout output = new VerticalLayout();

    public TriageAssistantView(IncidentTriageService triageService) {
        this.triageService = triageService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Incident Assistant"));
        add(new Text("LLM-backed triage view."));

        prompt.setWidthFull();
        prompt.setMinHeight("160px");
        prompt.setPlaceholder("Example: Investigate spikes around lon=-74.0060 lat=40.7128 for the last 24 hours. Focus on latency anomalies.");

        run.addClickListener(e -> execute());

        HorizontalLayout actions = new HorizontalLayout(run);
        actions.setPadding(false);

        output.setWidthFull();
        output.setPadding(false);
        output.setSpacing(true);

        add(prompt, actions, output);
        expand(output);
    }

    private void execute() {
        output.removeAll();
        String text = prompt.getValue() != null ? prompt.getValue().trim() : "";
        if (text.isBlank()) {
            Notification.show("Give the assistant something to work with.");
            return;
        }

        try {
            IncidentCase incident = triageService.triage(text);
            logger.info("Triage request received: {}", incident.toString());

            render(incident);
        } catch (Exception ex) {
            output.add(new H3("Error"));
            output.add(new Pre(ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? "(no message)" : ex.getMessage())));
        }
    }

    private void render(IncidentCase incident) {
        output.add(section("Signal", new Pre(formatSignal(incident))));
        output.add(section("Risk Assessment", new Pre(formatAssessment(incident))));
        output.add(section("Affected Implants", new Pre(formatAffected(incident))));
        output.add(section("Root Cause Hypothesis", new Pre(formatHypothesis(incident))));
        output.add(section("Estimated Blast Radius", new Pre(formatBlastRadius(incident))));
        output.add(section("Containment Plan", new Pre(formatPlan(incident))));

        output.add(new Text("Note: This is a demo assistant. Don't take its answers for granted."));
    }

    private String formatSignal(IncidentCase incident) {
        return incident.signal().toString();
    }

    private String formatAssessment(IncidentCase incident) {
        return incident.assessment().toString();
    }

    private String formatAffected(IncidentCase incident) {

        return incident.affected()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private String formatHypothesis(IncidentCase incident) {

        StringBuilder builder = new StringBuilder();

        builder.append(incident.hypothesis().type().toString());
        builder.append("\n");
        builder.append(incident.hypothesis().confidence());
        builder.append("\n");
        builder.append(incident.hypothesis().evidence().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));

        return builder.toString();
    }

    private String formatBlastRadius(IncidentCase incident) {

        EstimatedBlastRadius blastRadius = incident.plan().estimatedBlastRadius();
        StringBuilder builder = new StringBuilder();

        builder.append(blastRadius.affectedImplantsEstimate());
        builder.append("\n");
        builder.append(String.join(", ", blastRadius.affectedLots()));
        builder.append("\n");
        builder.append(String.join(", ", blastRadius.affectedModels()));
        builder.append("\n");
        builder.append(blastRadius.geoSummary());
        builder.append("\n");
        builder.append(blastRadius.timeSummary());

        return builder.toString();
    }

    private String formatPlan(IncidentCase incident) {

        return incident.plan()
                .steps()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }


    private VerticalLayout section(String title, Pre preFormattedText) {
        VerticalLayout box = new VerticalLayout();
        box.setPadding(false);
        box.setSpacing(false);
        box.add(new H3(title));
        box.add(preFormattedText);
        return box;
    }

}
