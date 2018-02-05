package org.eclipse.epsilon.cbp.state2change;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaPageScraper {

	String mainUrl;
	String startUrl;
	int startFromRevision = 8954;
	private String pattern = "\\/w\\/index\\.php\\?title\\=([A-Za-z0-9])\\w+.\\&amp\\;direction=next\\&amp;oldid\\=([0-9])\\w+";;
	// private String pattern =
	// "\\/w\\/index\\.php\\?title\\=George_W\\._Bush\\&amp\\;diff\\=next\\&amp\\;oldid\\=([0-9])\\w+";

	public WikipediaPageScraper(String mainUrl, String startUrl) {
		this.mainUrl = mainUrl;
		this.startUrl = startUrl;
	}

	public String getWebPage(String webAddress) {
		URL url;
		String result = null;
		boolean success = false;
		while (success == false) {
			try {
				url = new URL(webAddress);
				URLConnection con = url.openConnection();
				InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[8192];
				int len = 0;
				while ((len = in.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				result = new String(baos.toByteArray(), encoding);
				baos.reset();
				baos.close();
				in.close();
				success = true;
			} catch (IOException e) {
				success = false;
				e.printStackTrace();
			}
		}
		return result;
	}

	public String getNextWebPageUrl(String webPage) {
		String nextUrl = null;
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(webPage);
		if (m.find()) {
			nextUrl = m.group(0);
		}
		nextUrl = nextUrl.replaceAll("&amp;", "&");
		return mainUrl + nextUrl;
	}

	public void generateXmlDocuments(String mainUrl, String startUrl, File outputDir) throws IOException {
		int counter = startFromRevision;

		String nextUrl = startUrl;

		while (nextUrl != null && nextUrl.length() > 0 && counter <= 37995) {
			System.out.print("Downloading " + nextUrl + " ... ");

			counter += 1;
			String indexString = String.valueOf(counter);
			while (indexString.length() < 6) {
				indexString = "0" + indexString;
			}

			String title = getTitle(nextUrl);
			String id = getId(nextUrl);
			String fileName = title + "-" + indexString + "-" + id + ".xml";

			String webPage = getWebPage(nextUrl);

			File xmlFile = new File(outputDir.getAbsolutePath() + File.separator + fileName);
			if (outputDir.exists() == false) {
				outputDir.mkdir();
			}
			if (xmlFile.exists() == true) {
				xmlFile.delete();
			}
			if (xmlFile.createNewFile()) {
				PrintWriter out = new PrintWriter(xmlFile);
				out.print(webPage);
				out.flush();
				out.close();
			}

			nextUrl = getNextWebPageUrl(webPage);

			System.out.println("Done");
		}
	}

	private String getId(String startUrl) {
		String startWith = "oldid=";
		int start = startUrl.indexOf(startWith) + startWith.length();
		String id = startUrl.substring(start, startUrl.length());
		return id;
	}

	private String getTitle(String startUrl) {
		String startWith = "title=";
		String endWith = "&";
		int start = startUrl.indexOf(startWith) + startWith.length();
		int end = startUrl.indexOf(endWith);
		String title = startUrl.substring(start, end);
		return title;
	}
}