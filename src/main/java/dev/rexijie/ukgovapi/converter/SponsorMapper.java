package dev.rexijie.ukgovapi.converter;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import dev.rexijie.ukgovapi.constants.Rating;
import dev.rexijie.ukgovapi.constants.Route;
import dev.rexijie.ukgovapi.constants.SponsorType;
import dev.rexijie.ukgovapi.model.Sponsor;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SponsorMapper {
    private static final Logger LOG = Loggers.getLogger(SponsorMapper.class);
    private static final CSVParser csvParser = new CSVParserBuilder().withSeparator(',')
            .withIgnoreLeadingWhiteSpace(true)
            .build();

    public static String[] parseCsvLine(String line) {
        try {
            return csvParser.parseLine(line);
        } catch (IOException e) {
            LOG.error("Error parsing line: {}", line);
            return new String[5];
        }
    }

    private static String clean(String word) {
        return word.replace("\"", "").trim();
    }

    public static Sponsor toSponsor(String[] line) {
        try {
            String typeAndRating = clean(line[3]);
            int splitLocation = typeAndRating.indexOf("(");
            String type = clean(typeAndRating.substring(0, splitLocation));
            String rating = clean(typeAndRating.substring(splitLocation + 1, typeAndRating.length() - 1));

            return new Sponsor(clean(line[0]), clean(line[1]), clean(line[2]),
                    getSponsorTypeEnum(type),
                    getRatingsEnum(rating), new HashSet<>(Set.of(clean(line[4]))));
        } catch (Exception ex) {
            LOG.error("Error converting line: {}", Arrays.toString(line));
            return new Sponsor(line[0], line[1], line[2], SponsorType.UNKNOWN, Rating.UNKNOWN, new HashSet<>(Set.of(line[4])));
        }
    }

    public static SponsorType getSponsorTypeEnum(String type) {
        if (type.startsWith("Worker")) return SponsorType.WORKER;
        if (type.startsWith("Temporary")) return SponsorType.TEMPORARY_WORKER;
        return SponsorType.UNKNOWN;
    }

    public static Rating getRatingsEnum(String rating) {
        return switch (rating) {
            case "A (Premium)" -> Rating.A1;
            case "A (SME+)" -> Rating.A2;
            case "A rating" -> Rating.A;
            case "B rating" -> Rating.B;
            default -> Rating.C;
        };
    }

    public static Route getRoute(String route) {
        final String voluntaryWorker = Route.VOLUNTARY_WORKER.toString();
        return switch (route.toLowerCase()) {
            case "skilled worker" -> Route.SKILLED_WORKER;
            case "tier 2 general" -> Route.TIER_2;
            case "intra-company routes" -> Route.INTRA_COMPANY_ROUTE;
            case "t2 minister of religion" -> Route.T2_RELIGION;
            case "voluntary workers" -> Route.VOLUNTARY_WORKER;
            case "religious workers" -> Route.RELIGIOUS_WORKER;
            case "t2 sportsperson" -> Route.T2_SPORTSPERSON;
            case "international agreements" -> Route.INTERNATIONAL_AGREEMENTS;
            case "government authorised exchange" -> Route.GOVERNMENT_AUTHORISED_EXCHANGE;
            case "creative and sporting" -> Route.CREATIVE_AND_SPORTING;
            default -> Route.valueOf(route);
        };

    }
}
