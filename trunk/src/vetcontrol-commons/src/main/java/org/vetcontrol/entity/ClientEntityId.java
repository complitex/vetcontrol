package org.vetcontrol.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.03.2010 0:02:06
 *
 * Класс определяет составной ключ для уникальности объектов от разных клиентов
 */
public class ClientEntityId implements Serializable{
    private Long id;

    private Client client;

    private Department department;

    public ClientEntityId() {
    }

    public ClientEntityId(Long id, Client client, Department department) {
        this.id = id;
        this.client = client;
        this.department = department;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientEntityId that = (ClientEntityId) o;

        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return (department != null ? department.getId() : "0") + "." +
                (client != null ? client.getId() : "0")  + "." + id;
    }
}
