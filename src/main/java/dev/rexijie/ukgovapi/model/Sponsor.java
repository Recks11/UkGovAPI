package dev.rexijie.ukgovapi.model;

import dev.rexijie.ukgovapi.constants.Rating;
import dev.rexijie.ukgovapi.constants.SponsorType;

import java.util.Set;

public record Sponsor(String name,
                      String city,
                      String county,
                      SponsorType type,
                      Rating rating,
                      Set<String> Route) {}
