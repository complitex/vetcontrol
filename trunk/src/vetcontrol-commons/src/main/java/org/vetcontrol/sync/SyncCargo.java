package org.vetcontrol.sync;

import org.vetcontrol.entity.Cargo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.03.2010 13:39:09
 */
@XmlRootElement
public class SyncCargo extends SyncRequestEntity{
    private List<Cargo> cargos;

    public SyncCargo() {
    }

    public SyncCargo(String secureKey, Date updated, List<Cargo> cargos) {
        super(secureKey, updated);
        this.cargos = cargos;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }
}
