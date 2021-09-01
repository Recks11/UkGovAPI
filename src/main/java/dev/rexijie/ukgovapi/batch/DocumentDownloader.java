package dev.rexijie.ukgovapi.batch;

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
    private final String FILE_NAME = "downloads/sponsor_list.csv";
    @Value("${jsoup.conn.user-agent}")
    private String USER_AGENT;

    public DocumentDownloader(Connection connection) {
        this.connection = connection;
    }

    public void downloadSponsorList() {
        String csvURL = parseHTML();
        Objects.requireNonNull(csvURL);
        downloadFile(csvURL);
    }

    public String parseHTML() {
        try {
            Document document = connection.get();
            Elements els = document.select("span.download > a");
            Element downloadAnchor = els.first();
            if (downloadAnchor == null) throw new RuntimeException();
            String href = downloadAnchor.attributes().get("href");
            LOG.info("Extracted Url: {}", href);
            return href;
        } catch (Exception e) {
            LOG.error("could not establish connection to website");
            return null;
        }
    }

    private void downloadFile(@NonNull String link) {
        try {
            File fl = new File(FILE_NAME);
            if (fl.exists()) fl.delete();
            fl.getParentFile().mkdirs();

            URL urlToFile = new URL(link);
            URLConnection urlConnection = urlToFile.openConnection();
            urlConnection.setRequestProperty("user-agent", USER_AGENT);
            String contentType = urlConnection.getContentType();

            if (!contentType.equals("text/csv")) throw new RuntimeException("Downloadable file not a CSV");

            int contentLength = urlConnection.getContentLength();
            LOG.info("File content length is {} bytes", contentLength);

            InputStream fileStream = urlConnection.getInputStream();
            OutputStream out = new FileOutputStream(FILE_NAME);

            byte[] buffer = new byte[4096];
            int downloaded = 0;
            int length;
            while ((length = fileStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                downloaded += length;
                LOG.debug("Download Status: " + (downloaded * 100) / (contentLength * 1.0) + "%");
            }
        } catch (RuntimeException ex) {
            LOG.warn(ex.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
