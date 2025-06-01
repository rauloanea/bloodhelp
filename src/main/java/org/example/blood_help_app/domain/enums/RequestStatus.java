package org.example.blood_help_app.domain.enums;

public enum RequestStatus {
    PENDING,        // Cerere în așteptare
    APPROVED,       // Cerere aprobată
    FULFILLED,      // Cerere îndeplinită (sânge livrat)
    CANCELLED,      // Cerere anulată
    REJECTED        // Cerere respinsă
}
