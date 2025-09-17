package com.paycanvas.api.model;

import java.time.LocalDate;

public record Announcement(String id, String message, LocalDate date) {}
