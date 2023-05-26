module com.slimanice.distributedgeneticalgorthm {
    requires javafx.controls;
    requires javafx.fxml;
    requires jade;

    opens com.slimanice.distributedgeneticalgorthm to javafx.fxml;
    opens com.slimanice.distributedgeneticalgorthm.controllers to javafx.fxml;
    opens com.slimanice.distributedgeneticalgorthm.utils to javafx.base;
    exports com.slimanice.distributedgeneticalgorthm;
    exports com.slimanice.distributedgeneticalgorthm.agents;
    exports com.slimanice.distributedgeneticalgorthm.controllers;
}