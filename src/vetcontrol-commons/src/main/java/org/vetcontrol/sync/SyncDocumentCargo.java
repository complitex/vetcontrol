package org.vetcontrol.sync;

import org.vetcontrol.entity.DocumentCargo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.03.2010 13:00:53
 */
@XmlRootElement
public class SyncDocumentCargo extends SyncRequestEntity{
    private List<DocumentCargo> documentCargos;

    public SyncDocumentCargo() {
    }

    public SyncDocumentCargo(String secureKey, Date updated, List<DocumentCargo> documentCargos) {
        super(secureKey, updated);
        this.documentCargos = documentCargos;
    }

    public List<DocumentCargo> getDocumentCargos() {
        return documentCargos;
    }

    public void setDocumentCargos(List<DocumentCargo> documentCargos) {
        this.documentCargos = documentCargos;
    }
}
