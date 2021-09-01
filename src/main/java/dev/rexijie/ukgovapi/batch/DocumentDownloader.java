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
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

@Component
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
        return sponsorProperties.getDownloadPath()
                .concat("/")
                .concat(name);
    }

    public String getPathToFile() {
        if (lastDownloadLink == null) downloadSponsorList();
        return fileName;
    }

    public boolean downloadSponsorList() {
        String csvURL = parseHTML();
        Objects.requireNonNull(csvURL);
        return downloadFile(csvURL);
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

    private boolean downloadFile(@NonNull String link) {
        LOG.debug("downloading new file");
        try {
            fileName = generateFileName(link);
            File fl = new File(fileName);
            if (fl.exists()) {
                LOG.debug("File already exists, deleting files");
                if (fl.delete()) LOG.debug("File deleted");
            }
            if (fl.getParentFile().mkdirs()) LOG.debug("directories created");

            URL urlToFile = new URL(link);
            URLConnection urlConnection = urlToFile.openConnection();
            urlConnection.setRequestProperty("user-agent", USER_AGENT);
            String contentType = urlConnection.getContentType();

            if (!contentType.equals("text/csv")) throw new RuntimeException("Downloadable file not a CSV");

            int contentLength = urlConnection.getContentLength();
            LOG.info("File content length is {} bytes", contentLength);

            OutputStream out = new FileOutputStream(fileName);

            byte[] buffer = new byte[4096];
            int downloaded = 0;
            int length;
            while ((length = urlConnection.getInputStream().read(buffer)) != -1) {
                out.write(buffer, 0, length);
                downloaded += length;
                LOG.debug("Download Status: " + (downloaded * 100) / (contentLength * 1.0) + "%");
            }
            return true;
        } catch (RuntimeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }
}
