package dev.rexijie.ukgovapi.batch;

import dev.rexijie.ukgovapi.config.SponsorProperties;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;


public class DocumentDownloader {

    private final Logger LOG = LoggerFactory.getLogger(DocumentDownloader.class);
    private final Connection connection;
    private final SponsorProperties sponsorProperties;
    @Value("${jsoup.conn.user-agent}")
    private String USER_AGENT;
    private String lastDownloadLink;
    private String fileName;

    public DocumentDownloader(Connection connection,
                              SponsorProperties sponsorProperties) {
        this.connection = connection;
        this.sponsorProperties = sponsorProperties;
    }

    private String generateFileName(String link) {
        String[] spl = link.split("/");
        String name = spl[spl.length - 1];
        return sponsorProperties.getDownloadLocation()
                .concat("/")
                .concat(name);
    }

    public String getPathToFile() {
        if (lastDownloadLink == null) downloadSponsorList();
        return fileName;
    }

    public boolean downloadSponsorList() {
        String downloadUrl = sponsorProperties.hasDirectLink() ?
                sponsorProperties.getDirectLink() : parseHTML();
        Objects.requireNonNull(downloadUrl);

        return downloadNio(downloadUrl);
    }

    public String parseHTML() {
        try {
            Document document = connection.get();
            Elements els = document.select("span.download > a");
            Element downloadAnchor = els.first();
            if (downloadAnchor == null) throw new RuntimeException();
            String href = downloadAnchor.attributes().get("href");
            LOG.debug("Extracted Url: {}", href);
            lastDownloadLink = href;
            return href;
        } catch (Exception e) {
            LOG.error("could not establish connection to website");
            return null;
        }
    }

    private boolean downloadNio(@NonNull String link) {
        try {
            LOG.debug("Downloading Sponsor list");
            fileName = prepareDownload(link);
            URL urlToFile = new URL(link);
            URLConnection urlConnection = urlToFile.openConnection();
            urlConnection.setRequestProperty("user-agent", USER_AGENT);
            ReadableByteChannel readableByteChannel = Channels.newChannel(urlConnection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            long total = fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, urlConnection.getContentLengthLong());
            LOG.debug("File content length is {} MB", (total / 1E6));
            return true;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private String prepareDownload(String link) {
        String filename = generateFileName(link);
        File fl = new File(filename);
        if (fl.exists()) {
            LOG.debug("File already exists, deleting files");
            if (fl.delete()) LOG.debug("File deleted");
        }
        if (fl.getParentFile().mkdirs()) LOG.debug("directories created");
        return filename;
    }
}
