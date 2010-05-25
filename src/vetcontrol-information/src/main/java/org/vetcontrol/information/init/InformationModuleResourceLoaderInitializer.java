/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.init;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.vetcontrol.information.web.util.InformationModuleResourceLoader;

/**
 *
 * @author Artem
 */
public class InformationModuleResourceLoaderInitializer implements IInitializer {

    @Override
    public void init(Application application) {
        application.getResourceSettings().addStringResourceLoader(new InformationModuleResourceLoader());
    }
}
