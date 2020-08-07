package crawler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

//        START URL
        JLabel urlLabel = new JLabel(String.format("%-30s", "URL:"));
        JTextField urlTextField = new JTextField(30);
        urlTextField.setName("UrlTextField");
        JButton runButton = new JButton("Run");
        runButton.setName("RunButton");
        JPanel urlPanel = new JPanel();
        urlPanel.setBorder(new EmptyBorder(5,5,5,5));
        urlPanel.add(urlLabel);
        urlPanel.add(urlTextField);
        urlPanel.add(runButton);
        add(urlPanel);


//        WORKERS
        JLabel workersLabel = new JLabel(String.format("%-30s","Workers:"));
        JTextField workersField = new JTextField(30);
        JPanel workersPanel = new JPanel();
        workersPanel.setBorder(new EmptyBorder(5,5,5,5));
        workersPanel.add(workersLabel);
        workersPanel.add(workersField);
        add(workersPanel);

//        MAXIMUM DEPTH
        JLabel maxDepthLabel = new JLabel(String.format("%-30s","Maximum depth:"));
        JTextField maxDepthField = new JTextField(30);
        maxDepthField.setName("DepthTextField");
        JCheckBox maxDepthEnabled = new JCheckBox("Enabled", false);
        maxDepthEnabled.setName("DepthCheckBox");
        JPanel depthPanel = new JPanel();
        depthPanel.setBorder(new EmptyBorder(5,5,5,5));
        depthPanel.add(maxDepthLabel);
        depthPanel.add(maxDepthField);
        depthPanel.add(maxDepthEnabled);
        add(depthPanel);

//        TIME LIMIT
        JLabel timeLabel = new JLabel(String.format("%-30s","Time limit:"));
        JTextField timeField = new JTextField(30);
        JLabel secondsLabel = new JLabel("seconds");
        JCheckBox timeEnabled = new JCheckBox("Enabled", false);
        JPanel timePanel = new JPanel();
        timePanel.setBorder(new EmptyBorder(5,5,5,5));
        timePanel.add(timeLabel);
        timePanel.add(timeField);
        timePanel.add(secondsLabel);
        timePanel.add(timeEnabled);
        add(timePanel);

//       todo ELAPSED TIME

//       PARSED PAGES
        JLabel parsedLabel = new JLabel(String.format("%-30s","Parsed pages:"));
        JLabel parsedPages = new JLabel("0");
        parsedPages.setName("ParsedLabel");
        JPanel parsedPanel = new JPanel();
        parsedLabel.setBorder(new EmptyBorder(5,5,5,5));
        parsedPanel.add(parsedLabel);
        parsedPanel.add(parsedPages);
        add(parsedPanel);

//        EXPORT
        JLabel exportLabel = new JLabel(String.format("%-30s","Export:"));
        JTextField exportTextField = new JTextField(30);
        exportTextField.setName("ExportUrlTextField");
        JButton exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        JPanel exportPanel = new JPanel();
        exportPanel.setBorder(new EmptyBorder(5,5,5,5));
        exportPanel.add(exportLabel);
        exportPanel.add(exportTextField);
        exportPanel.add(exportButton);
        add(exportPanel);

        exportButton.addActionListener(actionEvent -> {
            // read export file name
            String exportFileName = exportTextField.getText();
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(exportFileName))) {
                // todo export to file
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        runButton.addActionListener( actionEvent -> {
            // clear table
//            tableModel.setRowCount(0);
            final String url = urlTextField.getText(); // initial user URL
            InputStream inputStream = null;
            try {
                URLConnection connection = new URL(url).openConnection();
                // make bot look kinda like human using win 10 and firefox browser
                connection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
                if (Objects.equals(connection.getContentType(), "text/html")) {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String siteHtml = new String(inputStream.readAllBytes(),StandardCharsets.UTF_8);
                    String siteTitle = getTitleFromSiteHtml(siteHtml);
//                    titleLabel.setText(siteTitle); // title of initial user URL
//                    tableModel.addRow(new String[]{url, siteTitle}); // add it to table also
                    // pattern matches links inside a tags eg.: <a href="myLink.com">
                    Pattern pattern = Pattern.compile("<a.*?href=[\"'](.*?)['\"].*?>",
                            Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(siteHtml);
                    while (matcher.find()) { // for all links found
                        Optional<Object[]> helper = getUrlAndTitle(matcher.group(1), url);
                        // if url is correct and title was found add it to table
//                        helper.ifPresent(tableModel::addRow);
                    }
                    pack();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        setTitle("Web Crawler");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Optional<Object[]> getUrlAndTitle(String unknownUrl, String fromUrl) {
        /*
         * Get a good URL for this site or return Optional.empty()
         */
        String siteUrl;
        if (unknownUrl.startsWith("http://") || unknownUrl.startsWith("https://")) {
            // absolute URL which should be OK
            siteUrl = unknownUrl;
        } else if (unknownUrl.startsWith("//")) {
            // missing protocol
            try {
                // get protocol from original URL and add it to this one
                siteUrl = new URL(fromUrl).getProtocol() + ":" + unknownUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else if (unknownUrl.contains("/")) {
            // maybe missing protocol as well ?
            try {
                // get protocol from original URL and add it to this one
                siteUrl = new URL(fromUrl).getProtocol() + "://" + unknownUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            // relative path
            try {
                // constructor creates working url from absolute and relative urls
                siteUrl = new URL(new URL(fromUrl), unknownUrl).toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        /*
         * Found good url proceed to find title...
         */
        InputStream inputStream = null;
        try {
            URLConnection connection = new URL(siteUrl).openConnection();
            if (Objects.equals(connection.getContentType(), "text/html")) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                String siteHtml = new String(inputStream.readAllBytes(),StandardCharsets.UTF_8);
                String siteTitle = getTitleFromSiteHtml(siteHtml);
                return Optional.of(new String[] {siteUrl, siteTitle});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Optional.empty();
    }

    private String getTitleFromSiteHtml(String siteText) {
        Pattern pattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(siteText);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }
}