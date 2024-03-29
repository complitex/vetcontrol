package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Artem
 *
 * Справочник получателей грузов
 * НЕ ИСПОЛЬЗУЕТСЯ СЕЙЧАС!
 */
@Entity
@Table(name = "cargo_receiver")
public class CargoReceiver implements ILocalBook, ILongId {

    private Long id;

    @Id
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
    @Column(name = "id", nullable = false, unique = true)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    private String name;
    private String address;

    @Column(name = "address", nullable = false)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
