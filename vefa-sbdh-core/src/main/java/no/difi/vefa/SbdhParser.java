package no.difi.vefa;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.InputStream;

/**
 * Created by soc on 16.09.2015.
 */
public interface SbdhParser {
    StandardBusinessDocumentHeader parse(InputStream inputStream);
}
