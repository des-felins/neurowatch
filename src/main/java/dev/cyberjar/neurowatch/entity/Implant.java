package dev.cyberjar.neurowatch.entity;


import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.util.Objects;


public class Implant {

    private String type;
    private String model;
    private String version;
    private String manufacturer;
    @Indexed(unique = true)
    private String serialNumber;
    @Indexed
    private int lotNumber;
    private LocalDate installedAt;

    public Implant() {

    }

    public Implant(String type,
                   String model, String version,
                   String manufacturer, int lotNumber,
                   String serialNumber,
                   String installedAt) {
        this.type = type;
        this.model = model;
        this.version = version;
        this.manufacturer = manufacturer;
        this.lotNumber = lotNumber;
        this.serialNumber = serialNumber;
        this.installedAt = LocalDate.parse(installedAt);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(LocalDate installedAt) {
        this.installedAt = installedAt;
    }

    public int getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(int lotNumber) {
        this.lotNumber = lotNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Implant implant = (Implant) o;
        return Objects.equals(serialNumber, implant.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serialNumber);
    }
}
