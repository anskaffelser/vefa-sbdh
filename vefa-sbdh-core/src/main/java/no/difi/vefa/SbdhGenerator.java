package no.difi.vefa;


import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 12.31
 */
public interface SbdhGenerator {

    void generate(InputStream inputStream, OutputStream outputStream);
}
