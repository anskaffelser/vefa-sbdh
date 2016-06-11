package no.difi.vefa.sbdh;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Parses an XML input stream holding a &lt;StandardBusinessDocumentHeader&gt; <em>only</em>.
 * This parser will be faster than the SbdhFastParser when the input only
 * contains a single &lt;StandardBusinessDocumentHeader&gt;
 *
 * @see SbdhFastParser for a more lax parser
 *
 * Created by soc on 16.09.2015.
 */
 class SbdhOnlyParser extends SbdhContext implements SbdhParser {

    /**
     * Parses the <code>&lt;StandardBusinessDocumentHeader&gt;</code> XML fragment
     * held in the supplied input stream.
     *
     * @param inputStream positioned at the start of the first XML element.
     * @return parse StandardBusinessDocumentHeader instance
     */
    @Override
    public StandardBusinessDocumentHeader parse(InputStream inputStream) {
        Unmarshaller unmarshaller;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e1) {
            throw new IllegalStateException("Unable to create JAXB unmarshaller " + e1.getMessage(), e1);
        }

        JAXBElement element = null;
        try {
            element = (JAXBElement) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to parse input data into StandardBusinessDocumentHeader", e);

        }
        return (StandardBusinessDocumentHeader) element.getValue();
    }


}
