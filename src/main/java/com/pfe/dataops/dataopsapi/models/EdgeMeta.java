package com.pfe.dataops.dataopsapi.models;



public class EdgeMeta {

    private String from;
    private String to;

    public EdgeMeta() {}

    public EdgeMeta(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }

    public void setFrom(String from) { this.from = from; }
    public void setTo(String to) { this.to = to; }
}

