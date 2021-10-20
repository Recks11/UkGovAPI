package dev.rexijie.ukgovapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "sponsors")
public class SponsorProperties {
    private String sponsorListPage;
    private String directLink;
    private String downloadLocation;

    public SponsorProperties(String sponsorListPage, String directLink, String downloadLocation) {
        this.sponsorListPage = sponsorListPage;
        this.directLink = directLink;
        this.downloadLocation = downloadLocation;
    }

    public String getSponsorListPage() {
        return sponsorListPage;
    }

    public void setSponsorListPage(String sponsorListPage) {
        this.sponsorListPage = sponsorListPage;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    public String getDirectLink() {
        return directLink;
    }

    public void setDirectLink(String directLink) {
        this.directLink = directLink;
    }

    public boolean hasDirectLink() {
        return this.directLink != null && this.directLink.matches(
                "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");
    }
}
