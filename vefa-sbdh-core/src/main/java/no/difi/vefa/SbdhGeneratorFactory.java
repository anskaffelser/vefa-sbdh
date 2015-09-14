package no.difi.vefa;

/**
 * @author steinar
 *         Date: 10.09.15
 *         Time: 12.32
 */
public class SbdhGeneratorFactory {

    public static SbdhGenerator messageLevelGenerator() {
        return new SbdWithAsicInBase64Generator();
    }
}
