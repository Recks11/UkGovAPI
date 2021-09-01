package dev.rexijie.ukgovapi.model;

import dev.rexijie.ukgovapi.constants.Rating;
import dev.rexijie.ukgovapi.constants.SponsorType;

public record Sponsor(String name,
                      String city,
                      String county,
                      SponsorType type,
                      Rating rating,
                      String Route) {}
