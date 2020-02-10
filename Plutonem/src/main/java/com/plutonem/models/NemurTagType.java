package com.plutonem.models;

public enum NemurTagType {
    DEFAULT;

    private static final int INT_DEFAULT = 0;

    public static NemurTagType fromInt(int value) {
        switch (value) {
            default:
                return DEFAULT;
        }
    }

    public int toInt() {
        switch (this) {
            case DEFAULT:
            default:
                return INT_DEFAULT;
        }
    }
}
