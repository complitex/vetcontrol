/*
 * $Id: MySignInPage.java 459307 2006-02-14 09:57:21Z jonl $
 * $Revision: 459307 $ $Date: 2006-02-14 10:57:21 +0100 (Tue, 14 Feb 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vetcontrol.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.vetcontrol.web.components.authentication.LocalizableSignInPanel;

public final class ApplicationSignInPage extends WebPage {

    /**
     * Constructor
     */
    public ApplicationSignInPage() {
        this(null);
    }

    /**
     * Constructor
     *
     * @param parameters
     *            Parameters to page
     */
    public ApplicationSignInPage(final PageParameters parameters) {
        add(new LocalizableSignInPanel("signInPanel", true));
    }
}
