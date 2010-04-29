package org.vetcontrol.entity;

import org.vetcontrol.sync.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 16:13:49
 *
 * Модель пользователя
 */
@Entity
@Table(name = "user")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements ILongId, IUpdated, IQuery{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlID @XmlJavaTypeAdapter(LongAdapter.class)
    private Long id;

    @Column(name = "login", length = 32, unique = true, nullable = false)
    private String login;

    @Column(name = "_password", length = 32, nullable = false)
    private String password;

    @Transient
    @XmlTransient
    private String changePassword;

    @Column(name = "first_name", length = 45)
    private String firstName;

    @Column(name = "middle_name", length = 45)
    private String middleName;

    @Column(name = "last_name", length = 45)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @XmlIDREF
    private Job job;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "login", referencedColumnName = "login")
    @XmlTransient
    private List<UserGroup> userGroups = new ArrayList<UserGroup>();

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @XmlIDREF
    private Department department;

    @ManyToOne
    @JoinColumn(name = "passing_border_point_id")
    @XmlIDREF
    private PassingBorderPoint passingBorderPoint;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Column(name = "locale", length = 2)
    private String locale;

    @Column(name = "page_size")
    private Integer pageSize;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public PassingBorderPoint getPassingBorderPoint() {
        return passingBorderPoint;
    }

    public void setPassingBorderPoint(PassingBorderPoint passingBorderPoint) {
        this.passingBorderPoint = passingBorderPoint;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Query getInsertQuery(EntityManager em){
        return em.createNativeQuery("insert into user " +
                "(id, login, _password, first_name, middle_name, last_name, job_id, department_id, passing_border_point_id, updated) " +
                " value (:id, :login, :_password, :first_name, :middle_name, :last_name, :job_id, :department_id, " +
                ":passing_border_point_id,  :updated)")
                .setParameter("id", id)
                .setParameter("login", login)
                .setParameter("_password", password)
                .setParameter("first_name", firstName)
                .setParameter("middle_name", middleName)
                .setParameter("last_name", lastName)
                .setParameter("job_id", job != null ? job.getId() : null)
                .setParameter("department_id", department.getId())
                .setParameter("passing_border_point_id", passingBorderPoint)
                .setParameter("updated", updated);
    }

    @Override
    public Query getUpdateQuery(EntityManager em){
        return em.createNativeQuery("update user set login = :login, _password = :_password, first_name = :first_name, " +
                "middle_name = :middle_name, last_name = :last_name, job_id = :job_id, department_id = :department_id, " +
                "passing_border_point_id = :passing_border_point_id, updated = :updated where id = :id")
                .setParameter("id", id)
                .setParameter("login", login)
                .setParameter("_password", password)
                .setParameter("first_name", firstName)
                .setParameter("middle_name", middleName)
                .setParameter("last_name", lastName)
                .setParameter("job_id", job != null ? job.getId() : null)
                .setParameter("department_id", department.getId())
                .setParameter("passing_border_point_id", passingBorderPoint)
                .setParameter("updated", updated);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[hash: ").append(Integer.toHexString(hashCode()))
                .append(", id: ").append(id)
                .append(", login: ").append(login)
                .append(", firstName: ").append(firstName)
                .append(", lastName: ").append(lastName)
                .append(", middleName: ").append(middleName)
                .append(", job: ").append(job)
                .append(", userGroups: ").append(userGroups)
                .append(", department: ").append(department)
                .append(", passingBorderPoint: ").append(passingBorderPoint)
                .append("]").toString();
    }

    @Transient
    public String getFullName() {
        return ((lastName != null) ? lastName + " " : "")
                + ((firstName != null) ? firstName + " " : "")
                + ((middleName != null) ? middleName : "");
    }

    @Transient
    public String getShortName() {
        return ((lastName != null) ? lastName + " " : "")
                + ((firstName != null) ? firstName.substring(0, 1) + ". " : "")
                + ((middleName != null) ? middleName.substring(0, 1) : ".");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (changePassword != null ? !changePassword.equals(user.changePassword) : user.changePassword != null)
            return false;
        if (department != null ? !department.equals(user.department) : user.department != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (job != null ? !job.equals(user.job) : user.job != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (locale != null ? !locale.equals(user.locale) : user.locale != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (middleName != null ? !middleName.equals(user.middleName) : user.middleName != null) return false;
        if (pageSize != null ? !pageSize.equals(user.pageSize) : user.pageSize != null) return false;
        if (passingBorderPoint != null ? !passingBorderPoint.equals(user.passingBorderPoint) : user.passingBorderPoint != null)
            return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (updated != null ? !updated.equals(user.updated) : user.updated != null) return false;
        if (userGroups != null ? !userGroups.equals(user.userGroups) : user.userGroups != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (changePassword != null ? changePassword.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (job != null ? job.hashCode() : 0);
        result = 31 * result + (userGroups != null ? userGroups.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (passingBorderPoint != null ? passingBorderPoint.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (pageSize != null ? pageSize.hashCode() : 0);
        return result;
    }
}
