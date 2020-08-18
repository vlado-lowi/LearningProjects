package crawler;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class UrlFinder extends SwingWorker<Void, String> {

    private final WebCrawler webCrawler;

    public UrlFinder(WebCrawler webCrawler) {
        this.webCrawler = webCrawler;
    }

    @Override
    protected Void doInBackground() {

        String urlToProcess;

        while (!isCancelled()) {
            urlToProcess = webCrawler.getUrlToProcess();

            //Process URL
            if (urlToProcess != null) {
                Optional<String[]> urlTitleHtml =
                        webCrawler.getUrlTitleAndHtmlOfSite(urlToProcess, urlToProcess);

                if (urlTitleHtml.isPresent()) {
                    String siteURL = urlTitleHtml.get()[0];
                    String siteTitle = urlTitleHtml.get()[1];
                    String siteHTML = urlTitleHtml.get()[2];

                    webCrawler.addToUrlsAndTitlesMap(siteURL, siteTitle);

                    //Find urls on this site that will be processed next
                    webCrawler.findUrlsAndAddToQueue(siteHTML);
                }
                //Nothing was found at this url
                else {

                    webCrawler.addToUrlsAndTitlesMap(urlToProcess, "");
                }

                webCrawler.incrementParsedPagesCount();
                publish();
            }

            //Wait a bit for new urls to be found
            else {
                System.err.println("No url to be processed waiting for a bit and going deeper.");
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                webCrawler.goToNextDepth();
            }
        }
        webCrawler.getRunButton().setSelected(false);
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        webCrawler.updateParsedPagesCount();
    }
}
