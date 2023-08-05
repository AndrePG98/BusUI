package odc.busui.models.events;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Maintenance {
    private final StringProperty type;
    private final StringProperty message;
    private final LongProperty repairTime;


    public Maintenance(String type, String message, Long repairTime) {
        this.type =  new SimpleStringProperty(type);
        this.message = new SimpleStringProperty(message);
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

    public long getRepairTime() {
        return repairTime.get();
    }

    public LongProperty repairTimeProperty() {
        return repairTime;
    }
}
