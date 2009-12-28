/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.web;

import java.io.Serializable;
import org.vetcontrol.information.model.CountryBook;

/**
 *
 * @author Artem
 */
public class Property implements Serializable, Cloneable {

    protected String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }
    protected boolean readable;

    /**
     * Get the value of readable
     *
     * @return the value of readable
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * Set the value of readable
     *
     * @param readable new value of readable
     */
    public void setReadable(boolean readable) {
        this.readable = readable;
    }
    protected boolean writable;

    /**
     * Get the value of writeable
     *
     * @return the value of writeable
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * Set the value of writeable
     *
     * @param writeable new value of writeable
     */
    public void setWritable(boolean writeable) {
        this.writable = writeable;
    }
    protected boolean nullable;

    /**
     * Get the value of nullable
     *
     * @return the value of nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Set the value of nullable
     *
     * @param nullable new value of nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    protected int length;

    /**
     * Get the value of length
     *
     * @return the value of length
     */
    public int getLength() {
        return length;
    }

    /**
     * Set the value of length
     *
     * @param length new value of length
     */
    public void setLength(int length) {
        this.length = length;
    }
    protected Class type;

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public Class getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(Class type) {
        this.type = type;
    }
    protected boolean localizable = false;

    /**
     * Get the value of localizable
     *
     * @return the value of localizable
     */
    public boolean isLocalizable() {
        return localizable;
    }

    /**
     * Set the value of localizable
     *
     * @param localizable new value of localizable
     */
    public void setLocalizable(boolean localizable) {
        this.localizable = localizable;
    }
    protected Class surroundingClass;

    /**
     * Get the value of surroundingClass
     *
     * @return the value of surroundingClass
     */
    public Class getSurroundingClass() {
        return surroundingClass;
    }

    /**
     * Set the value of surroundingClass
     *
     * @param surroundingClass new value of surroundingClass
     */
    public void setSurroundingClass(Class surroundingClass) {
        this.surroundingClass = surroundingClass;
    }
    protected boolean beanReference = false;

    /**
     * Get the value of beanReference
     *
     * @return the value of beanReference
     */
    public boolean isBeanReference() {
        return beanReference;
    }

    /**
     * Set the value of beanReference
     *
     * @param beanReference new value of beanReference
     */
    public void setBeanReference(boolean beanReference) {
        this.beanReference = beanReference;
    }
    protected String referencedField;

    /**
     * Get the value of referencedField
     *
     * @return the value of referencedField
     */
    public String getReferencedField() {
        return referencedField;
    }

    /**
     * Set the value of referencedField
     *
     * @param referencedField new value of referencedField
     */
    public void setReferencedField(String referencedField) {
        this.referencedField = referencedField;
    }



    @Override
    public Property clone() {
        try {
            return (Property)super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }
}
