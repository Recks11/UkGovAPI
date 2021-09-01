package dev.rexijie.ukgovapi.constants;

public enum Route {
    SKILLED_WORKER("Skilled Worker"),
    TIER_2("Tier 2 General"),
    INTRA_COMPANY_ROUTE("Intra-company Routes"),
    T2_RELIGION("T2 Minister of Religion"),
    VOLUNTARY_WORKER("Voluntary Workers"),
    RELIGIOUS_WORKER("Religious Workers"),
    T2_SPORTSPERSON("T2 Sportsperson"),
    INTERNATIONAL_AGREEMENTS("International Agreements"),
    GOVERNMENT_AUTHORISED_EXCHANGE("Government Authorised Exchange"),
    CREATIVE_AND_SPORTING("Creative and Sporting");

    private final String route;

    Route(String rating) {
        this.route = rating;
    }

    public String getRoute() {
        return route;
    }

    @Override
    public String toString() {
        return route.toUpperCase();
    }
}
