package no.difi.vefa.sbdh;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

import java.io.Serializable;
import java.util.Date;

/**
 * Immutable object.
 */
public class Header implements Serializable {

    public static Header newInstance() {
        return new Header();
    }

    private ParticipantIdentifier senderIdentifier;
    private ParticipantIdentifier receiverIdentifier;
    private ProcessIdentifier processIdentifier;
    private DocumentTypeIdentifier documentTypeIdentifier;
    private InstanceIdentifier instanceIdentifier;
    private Date creationTimestamp;

    private Header() {
        // No action.
    }

    public Header(ParticipantIdentifier senderIdentifier, ParticipantIdentifier receiverIdentifier, ProcessIdentifier processIdentifier, DocumentTypeIdentifier documentTypeIdentifier, InstanceIdentifier instanceIdentifier, Date creationTimestamp) {
        this.senderIdentifier = senderIdentifier;
        this.receiverIdentifier = receiverIdentifier;
        this.processIdentifier = processIdentifier;
        this.documentTypeIdentifier = documentTypeIdentifier;
        this.instanceIdentifier = instanceIdentifier;
        this.creationTimestamp = creationTimestamp;
    }

    public ParticipantIdentifier getSenderIdentifier() {
        return senderIdentifier;
    }

    public Header setSenderIdentifier(ParticipantIdentifier senderIdentifier) {
        return new Header(senderIdentifier, receiverIdentifier, processIdentifier, documentTypeIdentifier, instanceIdentifier, creationTimestamp);
    }

    public ParticipantIdentifier getReceiverIdentifier() {
        return receiverIdentifier;
    }

    public Header setReceiverIdentifier(ParticipantIdentifier receiverIdentifier) {
        return new Header(senderIdentifier, receiverIdentifier, processIdentifier, documentTypeIdentifier, instanceIdentifier, creationTimestamp);
    }

    public ProcessIdentifier getProcessIdentifier() {
        return processIdentifier;
    }

    public Header setProcessIdentifier(ProcessIdentifier processIdentifier) {
        return new Header(senderIdentifier, receiverIdentifier, processIdentifier, documentTypeIdentifier, instanceIdentifier, creationTimestamp);
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public Header setDocumentTypeIdentifier(DocumentTypeIdentifier documentTypeIdentifier) {
        return new Header(senderIdentifier, receiverIdentifier, processIdentifier, documentTypeIdentifier, instanceIdentifier, creationTimestamp);
    }

    public InstanceIdentifier getInstanceIdentifier() {
        return instanceIdentifier;
    }

    public Header setInstanceIdentifier(InstanceIdentifier instanceIdentifier) {
        return new Header(senderIdentifier, receiverIdentifier, processIdentifier, documentTypeIdentifier, instanceIdentifier, creationTimestamp);
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Header setCreationTimestamp(Date creationTimestamp) {
        return new Header(senderIdentifier, receiverIdentifier, processIdentifier, documentTypeIdentifier, instanceIdentifier, creationTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (!senderIdentifier.equals(header.senderIdentifier)) return false;
        if (!receiverIdentifier.equals(header.receiverIdentifier)) return false;
        if (!processIdentifier.equals(header.processIdentifier)) return false;
        if (!documentTypeIdentifier.equals(header.documentTypeIdentifier)) return false;
        if (instanceIdentifier != null ? !instanceIdentifier.equals(header.instanceIdentifier) : header.instanceIdentifier != null)
            return false;
        return !(creationTimestamp != null ? !creationTimestamp.equals(header.creationTimestamp) : header.creationTimestamp != null);
    }

    @Override
    public int hashCode() {
        int result = senderIdentifier.hashCode();
        result = 31 * result + receiverIdentifier.hashCode();
        result = 31 * result + processIdentifier.hashCode();
        result = 31 * result + documentTypeIdentifier.hashCode();
        result = 31 * result + (instanceIdentifier != null ? instanceIdentifier.hashCode() : 0);
        result = 31 * result + (creationTimestamp != null ? creationTimestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Header{" +
                "senderIdentifier=" + senderIdentifier +
                ", receiverIdentifier=" + receiverIdentifier +
                ", processIdentifier=" + processIdentifier +
                ", documentTypeIdentifier=" + documentTypeIdentifier +
                ", instanceIdentifier=" + instanceIdentifier +
                ", creationTimestamp=" + creationTimestamp +
                '}';
    }
}
