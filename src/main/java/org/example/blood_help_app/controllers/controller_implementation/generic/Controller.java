package org.example.blood_help_app.controllers.controller_implementation.generic;

import org.example.blood_help_app.service.ServicesImplementation;

public abstract class Controller {
    protected ServicesImplementation services;

    public void setServices(final ServicesImplementation services) {
        this.services = services;
    }
}
