package org.vetcontrol.sync;

import org.vetcontrol.entity.ArrestDocument;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.04.2010 19:17:32
 */
@XmlRootElement
public class SyncArrestDocument extends SyncRequestEntity{
    private List<ArrestDocument> arrestDocuments;

    public SyncArrestDocument() {
    }

    public SyncArrestDocument(String secureKey, Date updated, List<ArrestDocument> arrestDocuments) {
        super(secureKey, updated);
        this.arrestDocuments = arrestDocuments;
    }

    public List<ArrestDocument> getArrestDocuments() {
        return arrestDocuments;
    }

    public void setArrestDocuments(List<ArrestDocument> arrestDocuments) {
        this.arrestDocuments = arrestDocuments;
    }
}
