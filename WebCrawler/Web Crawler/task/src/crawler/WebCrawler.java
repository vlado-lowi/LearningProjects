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
        setLayout(new BorderLayout(5,5));

        JTextField urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");
        urlTextField.setColumns(30);

        JButton runButton = new JButton();
        runButton.setName("RunButton");
        runButton.setText("Get text!");

        JLabel urlLabel = new JLabel("URL:");

        JPanel upperControlPanel = new JPanel();
        upperControlPanel.setBorder(new EmptyBorder(5,5,5,5));
        upperControlPanel.add(urlLabel);
        upperControlPanel.add(urlTextField);
        upperControlPanel.add(runButton);
        add(upperControlPanel, BorderLayout.NORTH);


        JLabel titleLabel = new JLabel();
        titleLabel.setName("TitleLabel");

        JPanel middleAreaPanel = new JPanel();
        middleAreaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        middleAreaPanel.setLayout(new BorderLayout(5,5));
        middleAreaPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"URL", "Title"};
        int numRows = 0 ;
        // create table model
        DefaultTableModel tableModel = new DefaultTableModel(numRows, columnNames.length) ;
        tableModel.setColumnIdentifiers(columnNames);
        // create table based on the model
        JTable titlesTable = new JTable(tableModel);
        titlesTable.setName("TitlesTable");
        // user cannot edit table
        titlesTable.setEnabled(false);
        // add table to scroll pane so you can scroll it and see column names at all times
        JScrollPane scrollPane = new JScrollPane(titlesTable);
        titlesTable.setFillsViewportHeight(true);
        middleAreaPanel.add(scrollPane, BorderLayout.CENTER);

        add(middleAreaPanel, BorderLayout.CENTER);

        JLabel exportLabel = new JLabel("Export:");
        JTextField exportTextField = new JTextField(30);
        exportTextField.setName("ExportUrlTextField");
        JButton exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(exportLabel);
        bottomPanel.add(exportTextField);
        bottomPanel.add(exportButton);

        add(bottomPanel, BorderLayout.SOUTH);

        exportButton.addActionListener(actionEvent -> {
            // read export file name
            String exportFileName = exportTextField.getText();
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(exportFileName))) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    printWriter.println(tableModel.getValueAt(i, 0));
                    printWriter.println(tableModel.getValueAt(i, 1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        runButton.addActionListener( actionEvent -> {
            // clear table
            tableModel.setRowCount(0);
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
                    titleLabel.setText(siteTitle); // title of initial user URL
                    tableModel.addRow(new String[]{url, siteTitle}); // add it to table also
                    // pattern matches links inside a tags eg.: <a href="myLink.com">
                    Pattern pattern = Pattern.compile("<a.*?href=[\"'](.*?)['\"].*?>",
                            Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(siteHtml);
                    while (matcher.find()) { // for all links found
                        Optional<Object[]> helper = getUrlAndTitle(matcher.group(1), url);
                        // if url is correct and title was found add it to table
                        helper.ifPresent(tableModel::addRow);
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