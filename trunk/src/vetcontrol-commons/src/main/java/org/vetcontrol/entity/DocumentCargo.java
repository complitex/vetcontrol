package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 13:52:01
 *
 * Сущность объектно-реляционного отображения карточки на груз
 */
@Entity
@Table(name = "document_cargo")
@IdClass(ClientEntityId.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentCargo extends Synchronized implements IUpdated{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    @ManyToOne
    @JoinColumn(name = "client_id")
    @XmlIDREF
    private Client client;

    @Id
    @ManyToOne
    @JoinColumn(name = "department_id")
    @XmlIDREF
    private Department department;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    @XmlIDREF
    private User creator;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movement_type_id")
    @XmlIDREF
    private MovementType movementType;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(name = "vehicle_details", length = 255)
    private String vehicleDetails;

    @OneToMany(mappedBy = "documentCargo")
    @XmlTransient
    private List<Cargo> cargos = new ArrayList<Cargo>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_sender_id")
    @XmlIDREF
    private CargoSender cargoSender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_receiver_id")
    @XmlIDREF
    private CargoReceiver cargoReceiver;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_producer_id")
    @XmlIDREF
    private CargoProducer cargoProducer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passing_border_point_id")
    @XmlIDREF
    private PassingBorderPoint passingBorderPoint;

    @Column(name = "detention_details", length = 255)
    private String detentionDetails;

    @Column(name = "details", length = 255)
    private String details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public CargoSender getCargoSender() {
        return cargoSender;
    }

    public void setCargoSender(CargoSender cargoSender) {
        this.cargoSender = cargoSender;
    }

    public CargoReceiver getCargoReceiver() {
        return cargoReceiver;
    }

    public void setCargoReceiver(CargoReceiver cargoReceiver) {
        this.cargoReceiver = cargoReceiver;
    }

    public CargoProducer getCargoProducer() {
        return cargoProducer;
    }

    public void setCargoProducer(CargoProducer cargoProducer) {
        this.cargoProducer = cargoProducer;
    }

    public PassingBorderPoint getPassingBorderPoint() {
        return passingBorderPoint;
    }

    public void setPassingBorderPoint(PassingBorderPoint passingBorderPoint) {
        this.passingBorderPoint = passingBorderPoint;
    }

    public String getDetentionDetails() {
        return detentionDetails;
    }

    public void setDetentionDetails(String detentionDetails) {
        this.detentionDetails = detentionDetails;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDisplayId(){
        return (department != null ? department.getId() : "0") + "." +
               (client != null ? client.getId() : "0")  + "." + id;        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentCargo)) return false;
        if (!super.equals(o)) return false;

        DocumentCargo that = (DocumentCargo) o;

        if (cargoProducer != null ? !cargoProducer.equals(that.cargoProducer) : that.cargoProducer != null)
            return false;
        if (cargoReceiver != null ? !cargoReceiver.equals(that.cargoReceiver) : that.cargoReceiver != null)
            return false;
        if (cargoSender != null ? !cargoSender.equals(that.cargoSender) : that.cargoSender != null) return false;
        if (cargos != null ? !cargos.equals(that.cargos) : that.cargos != null) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (details != null ? !details.equals(that.details) : that.details != null) return false;
        if (detentionDetails != null ? !detentionDetails.equals(that.detentionDetails) : that.detentionDetails != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (movementType != null ? !movementType.equals(that.movementType) : that.movementType != null) return false;
        if (passingBorderPoint != null ? !passingBorderPoint.equals(that.passingBorderPoint) : that.passingBorderPoint != null)
            return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (vehicleDetails != null ? !vehicleDetails.equals(that.vehicleDetails) : that.vehicleDetails != null)
            return false;
        if (vehicleType != null ? !vehicleType.equals(that.vehicleType) : that.vehicleType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (movementType != null ? movementType.hashCode() : 0);
        result = 31 * result + (vehicleType != null ? vehicleType.hashCode() : 0);
        result = 31 * result + (vehicleDetails != null ? vehicleDetails.hashCode() : 0);
        result = 31 * result + (cargos != null ? cargos.hashCode() : 0);
        result = 31 * result + (cargoSender != null ? cargoSender.hashCode() : 0);
        result = 31 * result + (cargoReceiver != null ? cargoReceiver.hashCode() : 0);
        result = 31 * result + (cargoProducer != null ? cargoProducer.hashCode() : 0);
        result = 31 * result + (passingBorderPoint != null ? passingBorderPoint.hashCode() : 0);
        result = 31 * result + (detentionDetails != null ? detentionDetails.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }
}
