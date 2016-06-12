package no.difi.vefa.sbdh.util;

import no.difi.vefa.sbdh.api.ContentWithManifest;
import org.apache.commons.codec.binary.Base64InputStream;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Manifest;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ManifestItem;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class AsicAsXmlInputStream extends FilterInputStream implements ContentWithManifest {

    private final static byte[] preContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<asic:asic xmlns:asic=\"urn:etsi.org:specification:02918:v1.2.1\" id=\"asic\">\n".getBytes();
    private final static byte[] postContent = "</asic:asic>".getBytes();

    private final static Manifest manifest = new Manifest() {{
        setNumberOfItems(BigInteger.valueOf(1));
        getManifestItem().add(new ManifestItem() {{
            setMimeTypeQualifierCode("application/vnd.etsi.asic-e+zip");
            setUniformResourceIdentifier("#asic");
            setDescription("ASiC archive containing the business documents.");
        }});
    }};

    public AsicAsXmlInputStream(InputStream inputStream) {
        super(new SequenceInputStream(Collections.enumeration(Arrays.asList(
                new ByteArrayInputStream(preContent),
                new Base64InputStream(inputStream, true, 75, "\n".getBytes()),
                new ByteArrayInputStream(postContent)
        ))));
    }

    @Override
    public Manifest getManifest() {
        return manifest;
    }
}
