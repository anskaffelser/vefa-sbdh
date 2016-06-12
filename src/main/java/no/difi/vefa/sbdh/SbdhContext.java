package no.difi.vefa.sbdh;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

abstract class SbdhContext {

    protected static final String NS_SBDH = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

    protected static JAXBContext jaxbContext;

    protected static ObjectFactory objectFactory = new ObjectFactory();

    static {
        try {
            jaxbContext = JAXBContext.newInstance(StandardBusinessDocument.class, StandardBusinessDocumentHeader.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
