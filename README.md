# vefa-sbdh - manipulates StandardBusinessDocument and StandardBusinessDocumentHeader 

Use this component to:

* wrap an ASiC archive as base64 encoded payload within a StandardBusinessDocument (SBD) for use with PEPPOL, eSENS etc. 
* parse (extract) base64 encoded ASiC archive from SBD payload
* parse (extract) SBDH from really large XML documents very fast.

## Wrapping ASiC archive as base64 encoded payload within StandardBusinessDocument

In order to transport an ASiC archive as payload within a StandardBusinessDocument (SBD), the payload must 
be base64 encoded.

```java
/** Wraps the ASiC archive supplied in the "inputStream" into a SBD, with the supplied SBDH */
public void wrapSampleData(InputStream inputStream, StandardBusinessDocumentHeader standardBusinessDocumentHeader) throws Exception {
    SbdWrapper sbdWrapper = new SbdWrapper();

    File outputFile = File.createTempFile("vefa-sbdh", ".xml");
    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

    sbdWrapper.wrapInputStream(standardBusinessDocumentHeader, inputStream, fileOutputStream);
    log.debug("Wrote sample StandardBusinessDocument into " + outputFile.toString());
}
```
  

## Extracting base64 encoded ASiC archive from SBD payload


## Extracting the SBDH only from SBD with large payload



