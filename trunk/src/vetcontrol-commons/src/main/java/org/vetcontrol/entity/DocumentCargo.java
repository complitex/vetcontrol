package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
    @GeneratedValue(generator = "EnhancedTableGenerator")
    @GenericGenerator(name = "EnhancedTableGenerator", strategy = "org.vetcontrol.hibernate.id.TableGenerator",
    parameters = {
        @Parameter(name = "segment_value", value = "document_cargo"),
        @Parameter(name = "table_name", value = "generator"),
        @Parameter(name = "segment_column_name", value = "generatorName"),
        @Parameter(name = "value_column_name", value = "generatorValue"),
        @Parameter(name = "initial_value", value = "100"),
        @Parameter(name = "increment_size", value = "1"),
        @Parameter(name = "property", value = "id")})
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

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @ManyToOne
    @JoinColumn(name = "cargo_sender_country_id", nullable = false)
    private CountryBook senderCountry;

    @Column(name = "cargo_sender_name", nullable = false)
    private String senderName;

    @Column(name = "cargo_receiver_address", nullable = false)
    private String receiverAddress;

    @Column(name = "cargo_receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "details", length = 1024)
    private String details;

    @ManyToOne
    @JoinColumn(name = "passing_border_point_id")
    @XmlIDREF
    private PassingBorderPoint passingBorderPoint;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @XmlIDREF
    private User creator;

    @OneToMany(mappedBy = "documentCargo")
    @XmlTransient
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    @ManyToOne
    @JoinColumn(name = "cargo_mode_id", nullable = false)
    private CargoMode cargoMode;

    @OneToMany(mappedBy = "documentCargo")
    @OrderBy("id")
    @XmlTransient
    private List<Cargo> cargos = new ArrayList<Cargo>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    // AUTO GENERATED Getter and Setter

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

    public CountryBook getSenderCountry() {
        return senderCountry;
    }

    public void setSenderCountry(CountryBook senderCountry) {
        this.senderCountry = senderCountry;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public PassingBorderPoint getPassingBorderPoint() {
        return passingBorderPoint;
    }

    public void setPassingBorderPoint(PassingBorderPoint passingBorderPoint) {
        this.passingBorderPoint = passingBorderPoint;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(CargoMode cargoMode) {
        this.cargoMode = cargoMode;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
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

    public String getDisplayId() {
        return (department != null ? department.getId() : "0") + "."
                + (client != null ? client.getId() : "0") + "." + id;
    }


    // AUTO GENERATED Equal and HashCode 

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentCargo)) return false;
        if (!super.equals(o)) return false;

        DocumentCargo that = (DocumentCargo) o;

        if (cargoMode != null ? !cargoMode.equals(that.cargoMode) : that.cargoMode != null) return false;
        if (cargos != null ? !new ArrayList<Cargo>(cargos).equals(that.cargos) : that.cargos != null) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (details != null ? !details.equals(that.details) : that.details != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (movementType != that.movementType) return false;
        if (passingBorderPoint != null ? !passingBorderPoint.equals(that.passingBorderPoint) : that.passingBorderPoint != null) return false;
        if (receiverAddress != null ? !receiverAddress.equals(that.receiverAddress) : that.receiverAddress != null) return false;
        if (receiverName != null ? !receiverName.equals(that.receiverName) : that.receiverName != null) return false;
        if (senderCountry != null ? !senderCountry.equals(that.senderCountry) : that.senderCountry != null) return false;
        if (senderName != null ? !senderName.equals(that.senderName) : that.senderName != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (vehicleType != that.vehicleType) return false;
        if (vehicles != null ? !new ArrayList<Vehicle>(vehicles).equals(that.vehicles) : that.vehicles != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (movementType != null ? movementType.hashCode() : 0);
        result = 31 * result + (vehicleType != null ? vehicleType.hashCode() : 0);
        result = 31 * result + (senderCountry != null ? senderCountry.hashCode() : 0);
        result = 31 * result + (senderName != null ? senderName.hashCode() : 0);
        result = 31 * result + (receiverAddress != null ? receiverAddress.hashCode() : 0);
        result = 31 * result + (receiverName != null ? receiverName.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (passingBorderPoint != null ? passingBorderPoint.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (vehicles != null ? new ArrayList<Vehicle>(vehicles).hashCode() : 0);
        result = 31 * result + (cargoMode != null ? cargoMode.hashCode() : 0);
        result = 31 * result + (cargos != null ? new ArrayList<Cargo>(cargos).hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }
}
