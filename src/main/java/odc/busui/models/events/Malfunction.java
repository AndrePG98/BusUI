package odc.busui.models.events;

import javafx.beans.property.*;

public class Malfunction {
    private final StringProperty type;
    private final StringProperty message;
    private final FloatProperty chance;
    private final LongProperty repairTime;

    public Malfunction(String type, float chance, long repairTime, String message) {
        this.type = new SimpleStringProperty(type);
        this.message = new SimpleStringProperty(message);
        this.chance = new SimpleFloatProperty(chance);
        this.repairTime = new SimpleLongProperty(repairTime);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public float getChance() {
        return chance.get();
    }

    public FloatProperty chanceProperty() {
        return chance;
    }

    public long getRepairTime() {
        return repairTime.get();
    }

    public LongProperty repairTimeProperty() {
        return repairTime;
    }
}

