package org.example.blood_help_app.controllers.controller_implementation.general;

import org.example.blood_help_app.service.ServicesImplementation;

public abstract class Controller {
    protected ServicesImplementation services;

    public void setServices(final ServicesImplementation services) {
        this.services = services;
    }
}
