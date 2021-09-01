package dev.rexijie.ukgovapi.constants;

public enum SponsorType {
    UNKNOWN("Not Available"),
    TEMPORARY_WORKER("Temporary Worker"),
    WORKER("Worker");
    private final String type;

    SponsorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toUpperCase();
    }
}
