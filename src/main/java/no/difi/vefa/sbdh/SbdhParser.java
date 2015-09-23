package no.difi.vefa.sbdh;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.InputStream;

/**
 * Implementations of this interface will parse the SBDH from the supplied input stream.
 *
 * Created by soc on 16.09.2015.
 */
@SuppressWarnings("DefaultFileTemplate")
public interface SbdhParser {
    StandardBusinessDocumentHeader parse(InputStream inputStream);
}
