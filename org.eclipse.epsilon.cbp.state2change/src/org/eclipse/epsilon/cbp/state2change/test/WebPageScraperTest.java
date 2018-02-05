package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.epsilon.cbp.state2change.WikipediaPageScraper;
import org.junit.Test;

public class WebPageScraperTest {

	String mainUrl = "https://en.wikipedia.org";
//	String startUrl = "https://en.wikipedia.org/w/index.php?title=United_Kingdom&direction=next&oldid=291606";
//	String startUrl = "https://en.wikipedia.org/w/index.php?title=United_States&direction=next&oldid=291561"; //original from the first version
	String startUrl = "https://en.wikipedia.org/w/index.php?title=United_States&direction=next&oldid=39507593";
//	String startUrl = "https://en.wikipedia.org/w/index.php?title=George_W._Bush&direction=next&oldid=331658894";
	
	@Test
	public void testScrapPage() {
		WikipediaPageScraper scraper = new WikipediaPageScraper(mainUrl, startUrl);
		String webPage = scraper.getWebPage(startUrl);
		assertEquals(webPage.length() > 0, true);
	}

	@Test
	public void testGetNextURL() {
		WikipediaPageScraper scraper = new WikipediaPageScraper(mainUrl, startUrl);
		String webPage = scraper.getWebPage(startUrl);
		String nextUrl = scraper.getNextWebPageUrl(webPage);

		assertEquals(nextUrl,
				"https://en.wikipedia.org/w/index.php?title=United_Kingdom&direction=next&oldid=291606");
	}
	
	@Test
	public void testGenerateXmlDocuments() throws IOException {
		File outputDir = new File("D:\\TEMP\\WIKIPEDIA\\xml");
		
		WikipediaPageScraper scraper = new WikipediaPageScraper(mainUrl, startUrl);
		scraper.generateXmlDocuments(mainUrl, startUrl, outputDir);
	}

}
