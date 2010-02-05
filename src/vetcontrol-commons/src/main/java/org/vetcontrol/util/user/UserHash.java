/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.user;

import java.io.Serializable;
import java.util.EnumMap;
import org.vetcontrol.entity.SecurityGroup;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;

/**
 *
 * @author Artem
 */
public class UserHash implements Serializable {

    public UserHash() {
    }

    public UserHash(User user) {
        userHash = user.hashCode();

        if (user.getUserGroups() != null) {
            for (UserGroup group : user.getUserGroups()) {
                userGroupHashes.put(group.getSecurityGroup(), group.hashCode());
            }
        }
    }
    private Integer userHash;

    public Integer getUserHash() {
        return userHash;
    }

    public void setUserHash(Integer userHash) {
        this.userHash = userHash;
    }
    private EnumMap<SecurityGroup, Integer> userGroupHashes = new EnumMap<SecurityGroup, Integer>(SecurityGroup.class);

    public EnumMap<SecurityGroup, Integer> getUserGroupHashes() {
        return userGroupHashes;
    }

    public void setUserGroupHashes(EnumMap<SecurityGroup, Integer> userGroupHashes) {
        this.userGroupHashes = userGroupHashes;
    }

    public void updateVersionIfNecessary(User newUser){
        //TODO: implement
    }
}
