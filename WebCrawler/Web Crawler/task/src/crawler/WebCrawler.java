package crawler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    private JLabel urlLabel;
    private JTextField urlTextField;
    private JToggleButton runButton;
    private JPanel urlPanel;
    private JLabel workersLabel;
    private JTextField workersField;
    private JPanel workersPanel;
    private JLabel maxDepthLabel;
    private JTextField maxDepthField;
    private JCheckBox maxDepthEnabled;
    private JPanel depthPanel;
    private JLabel timeLabel;
    private JTextField timeField;
    private JLabel secondsLabel;
    private JCheckBox timeEnabled;
    private JPanel timePanel;
    private JLabel parsedLabel;
    private JLabel parsedPages;
    private JPanel parsedPanel;
    private JLabel exportLabel;
    private JTextField exportTextField;
    private JButton exportButton;
    private JPanel exportPanel;
    private ArrayList<SwingWorker<Void, String>> workers = new ArrayList<>();
    private ConcurrentLinkedQueue<String> urlsToBeProcessedThisDepth = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> urlsToBeProcessedNextDepth = new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<String,String> urlsAndTitlesMap = new ConcurrentHashMap<>();
    private AtomicInteger parsedPagesCount = new AtomicInteger(0);
    AtomicInteger currentDepth = new AtomicInteger(0);

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        initializeComponents();
        setTitle("Web Crawler");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        System.out.println("Is event dispatch thread: " + SwingUtilities.isEventDispatchThread());
    }

    private void initializeComponents() {
        //        START URL
        urlLabel = new JLabel(String.format("%-30s", "URL:"));
        urlTextField = new JTextField(30);
        urlTextField.setName("UrlTextField");
        runButton = new JToggleButton("Run");
        runButton.setName("RunButton");
        urlPanel = new JPanel();
        urlPanel.setBorder(new EmptyBorder(5,5,5,5));
        urlPanel.add(urlLabel);
        urlPanel.add(urlTextField);
        urlPanel.add(runButton);
        add(urlPanel);


//        WORKERS
        workersLabel = new JLabel(String.format("%-30s","Workers:"));
        workersField = new JTextField(30);
        workersPanel = new JPanel();
        workersPanel.setBorder(new EmptyBorder(5,5,5,5));
        workersPanel.add(workersLabel);
        workersPanel.add(workersField);
        add(workersPanel);

//        MAXIMUM DEPTH
        maxDepthLabel = new JLabel(String.format("%-30s","Maximum depth:"));
        maxDepthField = new JTextField(30);
        maxDepthField.setName("DepthTextField");
        maxDepthEnabled = new JCheckBox("Enabled", false);
        maxDepthEnabled.setName("DepthCheckBox");
        depthPanel = new JPanel();
        depthPanel.setBorder(new EmptyBorder(5,5,5,5));
        depthPanel.add(maxDepthLabel);
        depthPanel.add(maxDepthField);
        depthPanel.add(maxDepthEnabled);
        add(depthPanel);

//        TIME LIMIT
        timeLabel = new JLabel(String.format("%-30s","Time limit:"));
        timeField = new JTextField(30);
        secondsLabel = new JLabel("seconds");
        timeEnabled = new JCheckBox("Enabled", false);
        timePanel = new JPanel();
        timePanel.setBorder(new EmptyBorder(5,5,5,5));
        timePanel.add(timeLabel);
        timePanel.add(timeField);
        timePanel.add(secondsLabel);
        timePanel.add(timeEnabled);
        add(timePanel);

//       todo ELAPSED TIME

//       PARSED PAGES
        parsedLabel = new JLabel(String.format("%-30s","Parsed pages:"));
        parsedPages = new JLabel(parsedPagesCount.toString());
        parsedPages.setName("ParsedLabel");
        parsedPanel = new JPanel();
        parsedLabel.setBorder(new EmptyBorder(5,5,5,5));
        parsedPanel.add(parsedLabel);
        parsedPanel.add(parsedPages);
        add(parsedPanel);

//        EXPORT
        exportLabel = new JLabel(String.format("%-30s","Export:"));
        exportTextField = new JTextField(30);
        exportTextField.setName("ExportUrlTextField");
        exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        exportPanel = new JPanel();
        exportPanel.setBorder(new EmptyBorder(5,5,5,5));
        exportPanel.add(exportLabel);
        exportPanel.add(exportTextField);
        exportPanel.add(exportButton);
        add(exportPanel);

        exportButton.addActionListener(actionEvent -> exportToFile());
        runButton.addActionListener( actionEvent -> {
            if (runButton.isSelected()){
                startSearching();
            } else {
                stopSearching();
            }
        });
    }

    private void stopSearching() {
        System.out.println("Shutting down searching.");
        for (SwingWorker<Void,String> worker : workers) {
            worker.cancel(true);
        }
    }

    private void exportToFile() {
        // read export file name
        String exportFileName = exportTextField.getText();
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(exportFileName))) {
            if(urlsAndTitlesMap.isEmpty()) {
                printWriter.println();
            }
            for (Map.Entry<String, String> entry : urlsAndTitlesMap.entrySet()) {
                printWriter.println(entry.getKey());
                printWriter.println(entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSearching() {
        restartEnvironment();

        // Initial user URL
        final String urlToSearch = urlTextField.getText();

        Optional<String[]> urlTitleHtml = getUrlTitleAndHtmlOfSite(urlToSearch,urlToSearch);
        if (urlTitleHtml.isPresent()) {
            String siteURL = urlTitleHtml.get()[0];
            String siteTitle = urlTitleHtml.get()[1];
            String siteHTML = urlTitleHtml.get()[2];

            addToUrlsAndTitlesMap(siteURL, siteTitle);

            //Find urls on this site that will be processed next
            findUrlsAndAddToQueue(siteHTML);


            int maxDepth;
            try {
                maxDepth = Integer.parseInt(maxDepthField.getText());
                System.out.println("max depth = " + maxDepth);
            } catch (NumberFormatException e) {
                maxDepthField.setText("0");
                maxDepth = 0;
            }

            //If max depth is not exceeded
            if(currentDepth.incrementAndGet() <= maxDepth) {

                int requiredWorkers;
                try {
                    requiredWorkers = Integer.parseInt(workersField.getText());
                } catch (NumberFormatException e) {
                    requiredWorkers = 3;
                }

                //Initialize workers
                for (int i = 0 ; i < requiredWorkers; i++) {
                    workers.add(new UrlFinder(this));
                }

                //Start workers
                for (SwingWorker<Void,String> worker : workers) {
                    worker.execute();
                }
            }

        }
        //Nothing was found at this url
        else {

            addToUrlsAndTitlesMap(urlToSearch, "");
        }

        //Increment number of parsed pages
        parsedPages.setText(String.valueOf(parsedPagesCount.incrementAndGet()));
    }

    private void restartEnvironment() {
        urlsAndTitlesMap = new ConcurrentHashMap<>();
        urlsToBeProcessedThisDepth = new ConcurrentLinkedQueue<>();
        urlsToBeProcessedNextDepth = new ConcurrentLinkedQueue<>();
        parsedPagesCount.set(0);
        parsedPages.setText(String.valueOf(parsedPagesCount.get()));
    }

    public void findUrlsAndAddToQueue(String siteHtml) {
        // pattern matches links inside a tags eg.: <a href="myLink.com">
        Pattern pattern = Pattern.compile("<a.*?href=[\"'](.*?)['\"].*?>",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(siteHtml);

        //For all links found
        while (matcher.find()) {
            //If link have not been processed yet
            if (!urlsAndTitlesMap.containsKey(matcher.group(1))) {
                //Add link to concurrent queue
                urlsToBeProcessedNextDepth.add(matcher.group(1));
            }
        }
    }

    public String getUrlToProcess() {
        return urlsToBeProcessedThisDepth.poll();
    }

    public void addToUrlsAndTitlesMap(String url, String title) {
        urlsAndTitlesMap.put(url, title);
    }

    public Optional<String[]> getUrlTitleAndHtmlOfSite(String unknownUrl, String fromUrl) {
        /*
         * Get a good URL for this site or return Optional.empty()
         */
        String siteUrl;
        System.out.println("Processing this url: " + unknownUrl);
        if (unknownUrl.startsWith("http://") || unknownUrl.startsWith("https://")) {
            // absolute URL which should be OK
            siteUrl = unknownUrl;
        }
        else if (unknownUrl.startsWith("//")) {
            // missing protocol
            try {
                // get protocol from original URL and add it to this one
                siteUrl = new URL(fromUrl).getProtocol() + ":" + unknownUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        else if (unknownUrl.contains("/")) {
            // maybe missing protocol as well ?
            try {
                // get protocol from original URL and add it to this one
                siteUrl = new URL(fromUrl).getProtocol() + "://" + unknownUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        else {
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
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            if (Objects.equals(connection.getContentType(), "text/html")) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                String siteHtml = new String(inputStream.readAllBytes(),StandardCharsets.UTF_8);
                String siteTitle = getTitleFromSiteHtml(siteHtml);
                return Optional.of(new String[] {siteUrl, siteTitle, siteHtml});
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

    public void incrementParsedPagesCount() {
        parsedPagesCount.incrementAndGet();
    }

    public void updateParsedPagesCount() {
        parsedPages.setText(String.valueOf(parsedPagesCount.get()));
    }

    public int getMaxDepth() {
        return Integer.parseInt(maxDepthField.getText());
    }

    public void goToNextDepth() {
        if (currentDepth.incrementAndGet() > getMaxDepth()) {
            runButton.setSelected(false);
        }

        else if (urlsToBeProcessedThisDepth.isEmpty()) {
            System.out.println("Going deeper to depth: " + currentDepth.incrementAndGet());
            urlsToBeProcessedThisDepth = new ConcurrentLinkedQueue<>(urlsToBeProcessedNextDepth);
            urlsToBeProcessedThisDepth.clear();
        }

        else {
            System.err.println("Go to next depth called but thisDepth queue is not empty.");
        }
    }

    public JToggleButton getRunButton() {
        return runButton;
    }
}