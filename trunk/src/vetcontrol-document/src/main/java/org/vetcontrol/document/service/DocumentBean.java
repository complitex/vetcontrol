package org.vetcontrol.document.service;

import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 18:22:06
 */
@Stateless
public class DocumentBean {
    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    UserProfileBean userProfileBean;

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    public DocumentCargo getDocumentCargo(Long id){
        return entityManager.find(DocumentCargo.class, id);
    }

    public DocumentCargo loadDocumentCargo(Long id){
        DocumentCargo documentCargo = entityManager.find(DocumentCargo.class, id);        
        documentCargo.getCargos().size();
        return documentCargo;
    }

    public List<Cargo> getCargos(DocumentCargo documentCargo){
        return entityManager.createQuery("from Cargo dc where dc.documentCargo = :documentCargo", Cargo.class)
                .setParameter("documentCargo", documentCargo)
                .getResultList();
    }

    public void save(DocumentCargo documentCargo){
        if (documentCargo.getId() != null){
            List<Cargo> dbCargos = getCargos(documentCargo);

            for (Cargo db : dbCargos){
                boolean remove = true;

                for (Cargo ui : documentCargo.getCargos()){
                    if (db.getId().equals(ui.getId())){
                        remove = false;
                        break;
                    }
                }

                if (remove){
                    entityManager.remove(db);
                }
            }
            documentCargo.setUpdated(DateUtil.getCurrentDate());            
            entityManager.merge(documentCargo);
        }else{
            documentCargo.setCreator(userProfileBean.getCurrentUser());
            documentCargo.setCreated(DateUtil.getCurrentDate());
            entityManager.persist(documentCargo);
        }
    }

    public List<CargoMode> getCargoModes(CargoType cargoType){
        return entityManager.createQuery("from CargoMode cm where cm.cargoType = :cargoType", CargoMode.class)

                .setParameter("cargoType", cargoType)
                .getResultList();
    }

    public List<DocumentCargo> getDocumentCargos(){
        List<DocumentCargo> documentCargos =  entityManager.createQuery("from DocumentCargo", DocumentCargo.class)                
                .getResultList();
        for (DocumentCargo dc : documentCargos){
            bookViewDAO.addLocalizationSupport(dc);
            for (Cargo c : dc.getCargos()){
                bookViewDAO.addLocalizationSupport(c);
            }
        }

        return documentCargos;
    }

    public Long getDocumentCargosSize(){
        return (Long) entityManager.createQuery("select count(dc) from DocumentCargo dc").getSingleResult();        
    }


        

}
