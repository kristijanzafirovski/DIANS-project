package com.example.diansproject.model;


public enum Signal {
    BUY("BUY"),
    SELL("SELL");

    private final String label;

    Signal(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}