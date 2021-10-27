package dev.rexijie.ukgovapi.model;

import dev.rexijie.ukgovapi.constants.Rating;
import dev.rexijie.ukgovapi.constants.SponsorType;

import java.util.Set;

public record Sponsor(String name,
                      String city,
                      String county,
                      SponsorType type,
                      Rating rating,
                      Set<String> route) {
    @Override
    public String toString() {
        return "Sponsor{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", type=" + type +
                ", rating=" + rating +
                ", route=" + route +
                '}';
    }
}
