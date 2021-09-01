package dev.rexijie.ukgovapi.constants;

public enum Rating {
    A1("A (Premium)"),
    A2("A (SME+)"),
    A("A Rating"),
    B("B Rating"),
    C("Probationary Sponsor"),
    UNKNOWN("Unknown");
    private final String rating;

    Rating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return rating.toUpperCase();
    }
}
