package com.paycanvas.api.model;

import java.time.LocalDate;
import java.util.Optional;

public record PendingTask(String id, String title, String description, Optional<LocalDate> dueDate) {}
