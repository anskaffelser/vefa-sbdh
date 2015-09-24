package no.difi.vefa.sbdh;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Extracts base64 encoded ASiC payload from &lt;StandardBusinessDocument&gt;
 *
 * @author steinar
 *         Date: 22.09.15
 *         Time: 08.26
 */
public interface AsicExtractor {

    boolean isDecodeFromBase64();

    void setDecodeFromBase64(boolean decodeFromBase64);

    /**
     * Extracts the Base64 encoded payload from a &lt;StandardBusinessDocument&gt;, decoded it into the supplied outputstream.
     * The payload must be represented as follows:
     * <pre>
     *     &lt;asic:asic xmlns:asic="urn:etsi.org:specification:02918:v1.2.1"&gt;
     *          payload in base64 comes here
     *     &lt;/asic:asic&gt;
     * </pre>
     *
     * Note: Only the xml local name will be inspected and should equal <code >asic</code>
     *
     * @param sbdInputStream inputstream pointing to the first byte of the XML document to be parsed
     * @param outputStream output stream into which the decoded (binary) contents will be written to.
     */
    void extractAsic(InputStream sbdInputStream, OutputStream outputStream);


}
