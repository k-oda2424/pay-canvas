package com.paycanvas.api.model;

import java.time.Instant;

public record LoginResponse(String accessToken, String refreshToken, Instant expiresAt, UserSummary user) {}
