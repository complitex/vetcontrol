package org.vetcontrol.entity;

import javax.persistence.*;

/**
 *@author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 14:38:41
 *
 * Справочник отправителей грузов
 */
@Entity
@Table(name = "cargo_sender")
public class CargoSender implements ILocalBook, ILongId {

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
    private CargoSenderEmbeddable cargoSenderEmbeddable;

    @Embedded
    public CargoSenderEmbeddable getCargoSenderEmbeddable() {
        return cargoSenderEmbeddable;
    }

    public void setCargoSenderEmbeddable(CargoSenderEmbeddable cargoSenderEmbeddable) {
        this.cargoSenderEmbeddable = cargoSenderEmbeddable;
    }
}
