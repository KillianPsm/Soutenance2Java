module fr.cda.immobilier {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires htmlunit;
    requires java.sql;
    requires sib.api.v3.sdk;

    opens fr.cda.immobilier to javafx.fxml;
    exports fr.cda.immobilier;
    exports fr.cda.immobilier.controller;
    opens fr.cda.immobilier.controller to javafx.fxml;
}