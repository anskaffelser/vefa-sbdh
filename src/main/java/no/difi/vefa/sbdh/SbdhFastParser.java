

package no.difi.vefa.sbdh;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * An implementation of SBDH parser, which is optimized for speed on large files.
 *
 * It will first use a SAX parser to extract the <code>StandardBusinessDocumentHeader</code> only and
 * create a W3C DOM object.
 *
 * The W3C Document is then fed into JAXB, which saves us all the hassle of using Xpath to extract the data.
 *
 * @author steinar
 *         Date: 24.06.15
 *         Time: 15.58
 */
class SbdhFastParser extends SbdhContext implements SbdhParser {

    /**
     * Parses the inputstream from first occurence of &lt;StandardBusinessDocumentHeader&gt; to
     * the corresponding &lt;/StandardBusinessDocumentHeader&gt; into a W3C DOM object, after which the DOM
     * is unmarshalled into an Object graph using JaxB.
     *
     * Not very pretty, but it improves speed a lot when you have large XML documents.
     *
     * @param inputStream the inputstream containing the XML
     * @return an instance of StandardBusinessDocumentHeader if found, otherwise null.
     */
    public StandardBusinessDocumentHeader parse(InputStream inputStream) {

        StandardBusinessDocumentHeader standardBusinessDocumentHeader;

        if (inputStream.markSupported()) {
            // Indicates number of bytes to be read before the mark position is invalidated
            inputStream.mark(1024*32); // 32K should be sufficient to read the SBDH
        }

        // Parses and creates the W3C DOM
        Document document = parseSbdhIntoW3CDocument(inputStream);

        // If input stream contained an SBDH, unmarshal it to an Object graph using JaxB
        if (sbdhFoundInDocument(document)) {

            Unmarshaller unmarshaller = createUnmarshaller();

            // Let JAXB unmarshal into a Java Object graph from the W3C DOM Document object.
            JAXBElement root = null;
            try {
                root = (JAXBElement) unmarshaller.unmarshal(document);
            } catch (JAXBException e) {
                throw new IllegalStateException("Unable to unmarshal :" + e, e);
            }

            // Tada!
            standardBusinessDocumentHeader = (StandardBusinessDocumentHeader) root.getValue();
        } else {
            throw new IllegalArgumentException("No <StandardBusinessDocumentHeader> element found");
        }

        if (inputStream.markSupported()) {
            try {
                inputStream.reset();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to reset intput stream" + e,e);
            }
        }
        return standardBusinessDocumentHeader;
    }


    /**
     * If the supplied W3C Document contains data, we assume an SBDH was detected.
     *
     * @param document W3C Document holding the SBDH
     * @return true if Document object contains child nodes.
     */
    protected boolean sbdhFoundInDocument(Document document) {
        return document.getChildNodes().getLength() > 0;
    }

    protected Document parseSbdhIntoW3CDocument(InputStream inputStream) {
        XML2DOMReader xml2DOMReader = new XML2DOMReader();
        return xml2DOMReader.parse(inputStream, "StandardBusinessDocumentHeader");
    }

    private Unmarshaller createUnmarshaller() {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXB unmarshaller: " + e.getMessage(), e);
        }
    }

    /**
     * Transforms the W3C DOM object into a pretty printed string.
     *
     * @param document W3C document to be pretty printed.
     * @return pretty printed document
     */
    private String prettyPrint(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }
}
