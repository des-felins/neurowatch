package dev.cyberjar.neurowatch;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@Theme(value = "cyber", variant = Lumo.DARK)
@PWA(name = "NeuroWatch", shortName = "NW")
public class NeuroWatchApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(NeuroWatchApplication.class, args);
    }

}
