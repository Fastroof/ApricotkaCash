package ua.com.apricortka.apricotkacash.parser;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DomParser {

    private static final Logger log = Logger.getLogger(DomParser.class);

    public String parseRate(String url) throws ParserConfigurationException, IOException, SAXException {
        log.info("Try parse EUR");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        URLConnection urlConnection = new URL(url).openConnection();
        Document doc = builder.parse(urlConnection.getInputStream());
        return doc.getDocumentElement().getElementsByTagName("rate").item(0).getTextContent();
    }
}
