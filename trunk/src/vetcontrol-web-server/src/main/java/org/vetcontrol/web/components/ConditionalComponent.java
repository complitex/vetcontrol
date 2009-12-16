/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.Loop;

/**
 *
 * @author Artem
 */
public class ConditionalComponent extends Loop {

    Component[] subComponents;

    public ConditionalComponent(String id, Component subComponent) {
        super(id, 0);
        this.subComponents = new Component[]{subComponent};
    }

    public ConditionalComponent(String id, Component[] subComponents) {
        super(id, 0);
        this.subComponents = subComponents;
    }

    public void setRender(boolean render){
        setDefaultModelObject(render ? 1 : 0);
    }

    @Override
    protected void populateItem(LoopItem item) {
        if(subComponents != null){
            for (Component component : subComponents) {
                item.add(component);
            }
        }
    }
}
