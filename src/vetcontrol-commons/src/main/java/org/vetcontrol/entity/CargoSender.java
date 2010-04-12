package org.vetcontrol.entity;

import javax.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    private String name;
    private CountryBook country;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "country_id", nullable = false)
    public CountryBook getCountry() {
        return country;
    }

    public void setCountry(CountryBook country) {
        this.country = country;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
