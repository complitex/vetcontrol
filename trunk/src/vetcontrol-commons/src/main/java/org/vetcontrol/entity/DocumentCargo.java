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
public class DocumentCargo extends Synchronized implements IUpdated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @XmlIDREF
    private Client client;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    @XmlIDREF
    private Department department;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "creator_id")
    @XmlIDREF
    private User creator;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "movement_type_id")
    @XmlIDREF
    private MovementType movementType;
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;
    @OneToMany(mappedBy = "documentCargo")
    @XmlTransient
    private List<Cargo> cargos = new ArrayList<Cargo>();
    @Embedded
    @AttributeOverride(name = "name", column =
    @Column(name = "cargo_sender_name"))
    @AssociationOverrides({
        @AssociationOverride(name = "country", joinColumns =
        @JoinColumn(name = "cargo_sender_country_id"))})
    private CargoSenderEmbeddable cargoSender;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column =
        @Column(name = "cargo_receiver_name")),
        @AttributeOverride(name = "address", column =
        @Column(name = "cargo_receiver_address"))
    })
    private CargoReceiverEmbeddable cargoReceiver;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
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

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
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

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public CargoSenderEmbeddable getCargoSender() {
        return cargoSender;
    }

    public void setCargoSender(CargoSenderEmbeddable cargoSenderEmbeddable) {
        this.cargoSender = cargoSenderEmbeddable;
    }

    public CargoReceiverEmbeddable getCargoReceiver() {
        return cargoReceiver;
    }

    public void setCargoReceiver(CargoReceiverEmbeddable cargoReceiverEmbeddable) {
        this.cargoReceiver = cargoReceiverEmbeddable;
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

    public String getDisplayId() {
        return (department != null ? department.getId() : "0") + "."
                + (client != null ? client.getId() : "0") + "." + id;
    }
}
