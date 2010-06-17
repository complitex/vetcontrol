package org.vetcontrol.document.test;

import org.vetcontrol.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.06.2010 11:38:26
 */
public class DocumentCargoFactory {
    private static final String PERSISTENCE_UNIT_NAME = "vetcontroldb";
    private static final String SERVER_SECURE_KEY = "b2627dab45b9455e947f9755861aea10";

    private static Random random = new Random();
    private static EntityManager entityManager;

    public static DocumentCargo createRandomDocument(String login){
        User user = getUser(login);
        Client client = getClient(SERVER_SECURE_KEY);
        Department department = user.getDepartment();
        PassingBorderPoint passingBorderPoint = user.getPassingBorderPoint();

        DocumentCargo dc = new DocumentCargo();

        dc.setCreator(user);
        dc.setDepartment(department);
        dc.setClient(client);
        dc.setPassingBorderPoint(passingBorderPoint);

        dc.setMovementType(random.nextBoolean() ? MovementType.IMPORT : MovementType.TRANSIT);
        dc.setVehicleType(VehicleType.values()[random.nextInt(VehicleType.values().length)]);
                
        dc.setSenderCountry(getRandomElement(getCountryBooks()));
        dc.setSenderName(getRandomString("cargo_sender_name"));
        dc.setReceiverAddress(getRandomString("cargo_receiver_address"));
        dc.setReceiverName(getRandomString("cargo_receiver_name"));
        dc.setDetails(getRandomString("details"));

        int vehicleCount = dc.getVehicleType().isCompound() ? random.nextInt(10)+1 : 1;
        for (int i = 0; i < vehicleCount; ++i){
            Vehicle vehicle = new Vehicle();

            vehicle.setDepartment(department);
            vehicle.setClient(client);

            vehicle.setDocumentCargo(dc);
            vehicle.setVehicleType(dc.getVehicleType());
            //Контейнер            
            if (dc.getVehicleType().equals(VehicleType.CONTAINER)){
                String details = getRandomContainerPrefix(); //XXXX999999-9
                for (int k=0; k < 6; ++k) details += random.nextInt(10);
                details += "-" + random.nextInt(10);

                vehicle.setVehicleDetails(details);
            }else{
                vehicle.setVehicleDetails(getRandomString("vehicle_details"));
            }

            dc.getVehicles().add(vehicle);
        }

        dc.setCargoMode(getRandomCargoMode());

        int cargoCount = random.nextInt(20) + 1;
        for (int i=0; i < cargoCount; ++i){
            Cargo cargo = new Cargo();

            cargo.setDepartment(department);
            cargo.setClient(client);
            cargo.setDocumentCargo(dc);
            cargo.setCargoType(getRandomCargoType(dc.getCargoMode()));
            cargo.setUnitType(getRandomUnitType(dc.getCargoMode()));
            cargo.setCount(((double)random.nextInt(100000))/100);
            cargo.setVehicle(getRandomElement(dc.getVehicles()));
            cargo.setCargoProducer(getRandomCargoProducer());
            cargo.setCertificateDetails(getRandomString("certificate_details"));
            cargo.setCertificateDate(new Date((long) (new Date().getTime()-random.nextDouble()*31536000000L)));

            dc.getCargos().add(cargo);
        }

        return dc;
    }

    public static EntityManager getEntityManager() {
        if (entityManager == null){
            entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
        }

        return entityManager;
    }

    public static List<CountryBook> getCountryBooks(){
        return getEntityManager().createQuery("select c from CountryBook c where c.disabled = false", CountryBook.class).getResultList();
    }

    public static <T> T getRandomElement(List<? extends T> list){
        return list.get(random.nextInt(list.size()));
    }

    public static String getRandomString(String prefix){
        return prefix + ": "+ UUID.randomUUID().toString();
    }

    public static CargoMode getRandomCargoMode(){
        return getRandomElement(getEntityManager().createQuery("select cm from CargoMode  cm " +
                "where cm.cargoModeCargoTypes is not empty and cm.cargoModeUnitTypes is not empty and cm.disabled = false", CargoMode.class)
                .setMaxResults(500)
                .getResultList());
    }

    public static CargoType getRandomCargoType(CargoMode cargoMode){
        return getRandomElement(getEntityManager().createQuery("select t.cargoType from CargoModeCargoType t " +
                "where t.cargoMode = :cargoMode and t.cargoType.disabled = false", CargoType.class)
                .setParameter("cargoMode", cargoMode)
                .getResultList());
    }

    public static UnitType getRandomUnitType(CargoMode cargoMode){
        return getRandomElement(getEntityManager().createQuery("select t.unitType from CargoModeUnitType t " +
                "where t.cargoMode = :cargoMode and t.unitType.disabled = false", UnitType.class)
                .setParameter("cargoMode", cargoMode)
                .getResultList());
    }

    public static CargoProducer getRandomCargoProducer(){
        return getRandomElement(getEntityManager().createQuery("select cp from CargoProducer cp where cp.disabled = false", CargoProducer.class)
                .getResultList());
    }

    public static DocumentCargo getDocumentCargo(String details){
        return getEntityManager().createQuery("select dc from DocumentCargo dc where dc.details = :details", DocumentCargo.class)
                .setParameter("details", details)
                .getSingleResult();
    }

    public static User getUser(String login){
        return getEntityManager().createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public static Client getClient(String secureKey){
        return getEntityManager().createQuery("select c from Client c where c.secureKey = :secureKey", Client.class)
                .setParameter("secureKey", secureKey)
                .getSingleResult();
    }
    public static String getRandomContainerPrefix(){
        return getRandomElement(getEntityManager().createQuery("select cv.prefix from ContainerValidator cv where cv.disabled =false", String.class)
                .setMaxResults(500)
                .getResultList());
    }
}
