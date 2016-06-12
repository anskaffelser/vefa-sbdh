package no.difi.vefa.sbdh;

import no.difi.vefa.sbdh.lang.SbdhException;
import no.difi.vefa.sbdh.util.AsicAsXmlInputStream;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wraps supplied ASiC archive InputStream in Base64 encoding, inside a <code>StandardBusinessDocument</code>.
 * The payload is wrapped in an &lt;asic:asic&gt; element.
 * 
 * Created by soc on 16.09.2015.
 */
@Deprecated
public class SbdWrapper extends SbdhContext {

    public void wrapInputStream(StandardBusinessDocumentHeader sbdh, InputStream inputStream, OutputStream outputStream) {
        try {
            StaxWrapper.wrap(sbdh, new AsicAsXmlInputStream(inputStream), outputStream);
        } catch (SbdhException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
