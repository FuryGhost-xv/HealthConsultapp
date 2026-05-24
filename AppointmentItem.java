package com.example.healthconsultapp;

public class AppointmentItem {
    private int id;
    private String doctorName;
    private String date;
    private String time;
    private String room;
    private String status;

    public AppointmentItem(int id, String doctorName, String date, String time, String room, String status) {
        this.id = id;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.room = room;
        this.status = status;
    }

    public int getId() { return id; }
    public String getDoctorName() { return doctorName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getRoom() { return room; }
    public String getStatus() { return status; }
}