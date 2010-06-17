/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change;

import java.util.Locale;
import java.util.Set;
import org.vetcontrol.document.util.change.comparator.DocumentComparator;
import org.vetcontrol.document.util.change.comparator.documentcargo.DocumentCargoComparator;
import org.vetcontrol.entity.Change;
import org.vetcontrol.entity.DocumentCargo;

/**
 *
 * @author Artem
 */
public final class DocumentChangeManager {

    private DocumentChangeManager() {
    }

    public static Set<Change> getChanges(DocumentCargo oldDocumentCargo, DocumentCargo newDocumentCargo, Locale systemLocale) {
        DocumentComparator<DocumentCargo> documentComparator = new DocumentCargoComparator(systemLocale);
        return documentComparator.compare(oldDocumentCargo, newDocumentCargo);
    }
}
