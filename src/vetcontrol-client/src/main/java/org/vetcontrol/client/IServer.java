package org.vetcontrol.client;

import java.io.File;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.04.2010 12:10:25
 */
public interface IServer {

    public File getClientDeployDir();

    public File getClientUpdateDir();

    public void deployClient();

    public void undeployClient();
}
