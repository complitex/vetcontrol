package org.vetcontrol.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 13:52:01
 */
@Entity
@Table(name = "document_cargo")
public class DocumentCargo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;       

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movement_type_id")
    private MovementType movementType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(name = "vehicle_details", length = 255)
    private String vehicleDetails;

    @OneToMany(mappedBy = "documentCargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cargo> cargos = new ArrayList<Cargo>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_sender_id")
    private CargoSender cargoSender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_receiver_id")
    private CargoReceiver cargoReceiver;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_producer_id")
    private CargoProducer cargoProducer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passing_border_point_id")
    private PassingBorderPoint passingBorderPoint;

    @Column(name = "detention_details", length = 255)
    private String detentionDetails;

    @Column(name = "details", length = 255)
    private String details;

    @Version
    @Column(name = "version")
    private Date version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }
}
