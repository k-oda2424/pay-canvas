package com.paycanvas.api.model;

public record DailyAttendance(
    String id,
    String date,
    String staffName,
    String storeName,
    String checkIn,
    String checkOut,
    int workHours,
    int tardyMinutes,
    String status) {}
