package org.vetcontrol.entity;

import javax.persistence.*;

/**
 * @author Artem
 *
 * Справочник получателей грузов
 */
@Entity
@Table(name = "cargo_receiver")
public class CargoReceiver implements ILocalBook, ILongId {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    private CargoReceiverEmbeddable cargoReceiverEmbeddable;

    @Embedded
    public CargoReceiverEmbeddable getCargoReceiverEmbeddable() {
        return cargoReceiverEmbeddable;
    }

    public void setCargoReceiverEmbeddable(CargoReceiverEmbeddable cargoReceiverEmbeddable) {
        this.cargoReceiverEmbeddable = cargoReceiverEmbeddable;
    }
}
