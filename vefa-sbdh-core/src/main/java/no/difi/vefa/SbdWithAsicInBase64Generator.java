package no.difi.vefa;

import no.difi.asic.AsicUtils;
import org.apache.commons.io.IOUtils;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ManifestItem;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.*;
import java.math.BigInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 12.44
 */
public class SbdWithAsicInBase64Generator implements SbdhGenerator {

    private final JAXBContext jaxbContext;

    public SbdWithAsicInBase64Generator() {
        try {
            jaxbContext = JAXBContext.newInstance(StandardBusinessDocument.class, StandardBusinessDocumentHeader.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create JAXBContext " + e.getMessage(), e);
        }
    }

    @Override
    public void generate(InputStream inputStream, OutputStream outputStream) {

        if (inputStream.markSupported()){
            inputStream.mark(Integer.MAX_VALUE);
        } else {
            throw new IllegalStateException("No support for non-rewindable inputstreams in this version");
        }

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        StandardBusinessDocumentHeader sbdh = extractSbdh(zipInputStream);

        modifyOriginalSbdh(sbdh);

        StandardBusinessDocument sbd = new StandardBusinessDocument();
        sbd.setStandardBusinessDocumentHeader(sbdh);

        try {
            inputStream.reset();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, baos);
            byte[] bytes = baos.toByteArray();
            QName qNameForPayload = new QName("urn:etsi.org:specification:02918:v1.2.1","asic","asic");
            JAXBElement<byte[]> jaxbElement = new JAXBElement<>(qNameForPayload, byte[].class, bytes);
            sbd.setAny(jaxbElement);


        } catch (IOException e) {
            throw new IllegalStateException("Unable to reset inputstream");
        }

        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<StandardBusinessDocument> standardBusinessDocument = new ObjectFactory().createStandardBusinessDocument(sbd);

            marshaller.marshal(standardBusinessDocument, outputStream);

        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to create unmarshaller " + e.getMessage(), e);
        }
    }


    void modifyOriginalSbdh(StandardBusinessDocumentHeader sbdh) {
        ManifestItem manifestItem = new ManifestItem();
        manifestItem.setMimeTypeQualifierCode(AsicUtils.MIMETYPE_ASICE);
        manifestItem.setUniformResourceIdentifier("#asic");
        manifestItem.setDescription("References the embedded ASiC archive in Base64 encoding");
        sbdh.getManifest().getManifestItem().clear();
        sbdh.getManifest().getManifestItem().add(manifestItem);
        sbdh.getManifest().setNumberOfItems(BigInteger.valueOf(1L));
    }

    /**
     * Extracts the StandardBusinessHeader object from the ASiC archive supplied in the ZipInputStream.
     *
     * @param zipInputStream
     * @return
     */
    StandardBusinessDocumentHeader extractSbdh(ZipInputStream zipInputStream) {
        StandardBusinessDocumentHeader sbdh = null;

        InputStream sbdhInputStream = sbdhInputStream(zipInputStream);
        sbdh = parseSbdh(sbdhInputStream);

        return sbdh;
    }

    StandardBusinessDocumentHeader parseSbdh(InputStream sbdhInputStream) {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e1) {
            throw new IllegalStateException("Unable to create JAXB unmarshaller " + e1.getMessage(), e1);
        }

        JAXBElement element = null;
        try {
            element = (JAXBElement) unmarshaller.unmarshal(sbdhInputStream);
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to parse input data into StandardBusinessDocumentHeader", e);

        }
        StandardBusinessDocumentHeader sbdh = (StandardBusinessDocumentHeader) element.getValue();
        return sbdh;
    }

    InputStream sbdhInputStream(ZipInputStream zipInputStream) {
        ZipEntry zipEntry = null;
        ByteArrayInputStream byteArrayInputStream = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equalsIgnoreCase("sbdh.xml")) {
                    IOUtils.copy(zipInputStream, baos);
                    byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading input stream: " + e.getMessage(), e);
        }

        return byteArrayInputStream;

    }
}
