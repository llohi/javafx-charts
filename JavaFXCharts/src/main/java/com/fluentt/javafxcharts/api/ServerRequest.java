package com.fluentt.javafxcharts.api;

import com.fluentt.javafxcharts.data.BsWfsElement;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;


/**
 * Class used to connect to the Finnish Meteorological Institute (FMI) API with a specific url
 * and extract an XML data to Java objects.
 * @author J.L.
 */
public class ServerRequest {

    // Query used to fetch only relevant data (BsWfsElements) from the API response
    final static String DATA_QUERY = "/wfs:FeatureCollection/wfs:member/BsWfs:BsWfsElement";

    // NamespaceContext used to inform the XPath of the relevant namespaces
    final static NamespaceContext NAMESPACE_CONTEXT = new NamespaceContext() {
        @Override
        public String getNamespaceURI(String prefix) {
            return prefix.equals("wfs") ? "http://www.opengis.net/wfs/2.0" :
                    prefix.equals("BsWfs") ? "http://xml.fmi.fi/schema/wfs/2.0" : null;
        }

        @Override
        public String getPrefix(String namespaceURI) { return null; }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) { return null; }
    };


    /**
     * Get data from the url with a xpath expression (query) and return all matches
     * as an array list of BsWfsElement objects.
     * @param url the formatted url
     * @return an array list of BsWfsElement objects
     */
    public static ArrayList<BsWfsElement> getData(String url)
            throws IOException, XPathExpressionException,
            ParserConfigurationException, SAXException {

        ArrayList<BsWfsElement> elements = new ArrayList<>();

        // Create Document of XML from the url
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(getRawData(url).getBytes()));

        // Create an XPath to filter through the XML
        XPath xpath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(NAMESPACE_CONTEXT);

        // Set query to observed data
        XPathExpression xpe = xpath.compile(DATA_QUERY);

        // Get data as a NodeList
        Object result = xpe.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;

        // Loop over all nodes and create a BsWfsElement of each
        for (int i = 0; i < nodes.getLength(); i++) {

            NodeList attributes = nodes.item(i).getChildNodes();

            // Parse location string into array of doubles
            // "1.234 5.678 "  =>  [1.234, 5.678]
            String[] points = attributes.item(1).getTextContent().trim().split(" ");
            double[] pos = new double[2];
            pos[0] = Double.parseDouble(points[0]);
            pos[1] = Double.parseDouble(points[1]);

            // Add new BsWfsElement to elements
            elements.add(new BsWfsElement(
                    pos,                                                              // Coordinates
                    attributes.item(3).getTextContent(),                        // Time
                    attributes.item(5).getTextContent(),                        // Parameter name
                    Double.parseDouble(attributes.item(7).getTextContent())));  // Parameter value
        }

        return elements;
    }

    /**
     * Connect to a URL and return its raw data as a String.
     * @param url The web address to the raw data
     * @return Raw data as a string
     */
    public static String getRawData(String url) throws IOException {

        // Configure url connection
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept-Encoding", "gzip");

        // Get gzip stream
        InputStream stream = conn.getInputStream();
        InputStream bodyStream = new GZIPInputStream(stream);

        // Decompress gzip to byte array
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = bodyStream.read(buffer)) > 0)
            outStream.write(buffer, 0, length);

        return outStream.toString(StandardCharsets.UTF_8);
    }
}