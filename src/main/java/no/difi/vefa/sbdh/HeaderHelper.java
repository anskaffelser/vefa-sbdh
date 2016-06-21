package no.difi.vefa.sbdh;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.sbdh.lang.EnvelopeException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;

class HeaderHelper extends RawSbdhContext {

    public static StandardBusinessDocumentHeader toSbdh(Header header) throws EnvelopeException {
        if (header.getSenderIdentifier() == null)
            throw new EnvelopeException("Sender is missing.");
        if (header.getReceiverIdentifier() == null)
            throw new EnvelopeException("Receiver is missing.");
        if (header.getProcessIdentifier() == null)
            throw new EnvelopeException("Process identifier is missing.");
        if (header.getDocumentTypeIdentifier() == null)
            throw new EnvelopeException("Document type identifier is missing.");

        if (header.getInstanceIdentifier() == null)
            header = header.setInstanceIdentifier(InstanceIdentifier.generateUUID());
        if (header.getCreationTimestamp() == null)
            header = header.setCreationTimestamp(new Date());

        return createObject(header);
    }

    private static StandardBusinessDocumentHeader createObject(final Header header) throws EnvelopeException {
        try {
            return new StandardBusinessDocumentHeader() {{
                // Header version
                setHeaderVersion("1.0");

                // Sender
                getSender().add(new Partner() {{
                    setIdentifier(new PartnerIdentification() {{
                        setAuthority(header.getSenderIdentifier().getScheme().getValue());
                        setValue(header.getSenderIdentifier().toString());
                    }});
                }});

                // Receiver
                getReceiver().add(new Partner() {{
                    setIdentifier(new PartnerIdentification() {{
                        setAuthority(header.getReceiverIdentifier().getScheme().getValue());
                        setValue(header.getReceiverIdentifier().toString());
                    }});
                }});

                // DocumentIdentification
                setDocumentIdentification(new DocumentIdentification() {{
                    setStandard(header.getDocumentTypeIdentifier().getXmlNamespace());
                    setTypeVersion(header.getDocumentTypeIdentifier().getXmlVersion());
                    setInstanceIdentifier(header.getInstanceIdentifier().getValue());
                    setType(header.getDocumentTypeIdentifier().getXmlRootElement());

                    // Creation timestamp
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.setTime(header.getCreationTimestamp());
                    setCreationDateAndTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
                }});

                setBusinessScope(new BusinessScope() {{
                    // DocumentID
                    getScope().add(new Scope() {{
                        setType("DOCUMENTID");
                        setInstanceIdentifier(header.getDocumentTypeIdentifier().getIdentifier());
                    }});
                    // ProcessID
                    getScope().add(new Scope() {{
                        setType("PROCESSID");
                        setInstanceIdentifier(header.getProcessIdentifier().getIdentifier());
                    }});
                }});
            }};
        } catch (DatatypeConfigurationException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    public static void toSbdh(Header header, OutputStream outputStream) throws EnvelopeException {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(objectFactory.createStandardBusinessDocumentHeader(toSbdh(header)), outputStream);
        } catch (JAXBException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }

    public static Header fromSbdh(StandardBusinessDocumentHeader sbdh) throws EnvelopeException {
        Header header = Header.newInstance();

        // Sender
        if (sbdh.getSender().size() == 0)
            throw new EnvelopeException("Sender not available.");
        header = header.setSenderIdentifier(new ParticipantIdentifier(sbdh.getSender().get(0).getIdentifier().getValue()));

        // Receiver
        if (sbdh.getReceiver().size() == 0)
            throw new EnvelopeException("Receiver not available.");
        header = header.setReceiverIdentifier(new ParticipantIdentifier(sbdh.getReceiver().get(0).getIdentifier().getValue()));

        // Identifier
        header = header.setInstanceIdentifier(new InstanceIdentifier(sbdh.getDocumentIdentification().getInstanceIdentifier()));

        // Creattion timestamp
        header = header.setCreationTimestamp(sbdh.getDocumentIdentification().getCreationDateAndTime().toGregorianCalendar().getTime());

        for (Scope scope : sbdh.getBusinessScope().getScope()) {
            // DocumentID
            if ("DOCUMENTID".equals(scope.getType()))
                header = header.setDocumentTypeIdentifier(new DocumentTypeIdentifier(scope.getInstanceIdentifier()));
            // ProcessID
            if ("PROCESSID".equals(scope.getType()))
                header = header.setProcessIdentifier(new ProcessIdentifier(scope.getInstanceIdentifier()));
        }

        return header;
    }

    public static Header fromSbdh(InputStream inputStream) throws EnvelopeException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return fromSbdh(unmarshaller.unmarshal(new StreamSource(inputStream), StandardBusinessDocumentHeader.class).getValue());
        } catch (JAXBException e) {
            throw new EnvelopeException(e.getMessage(), e);
        }
    }
}
