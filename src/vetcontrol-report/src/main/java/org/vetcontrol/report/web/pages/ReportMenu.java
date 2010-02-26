/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.web.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ResourceTemplateMenu;
import org.vetcontrol.web.template.TemplateWebApplication;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation({SecurityRoles.LOCAL_AND_REGIONAL_REPORT})
public class ReportMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString(ReportMenu.class, locale, "title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(ReportMenu.class, locale, "movement_types_report");
            }

            @Override
            public Class<? extends Page> getPage() {
                return MovementTypesReportForm.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "movement_types_report";
            }
        });

        TemplateWebApplication application = (TemplateWebApplication)Application.get();
        if(application.hasAnyRole(SecurityRoles.LOCAL_REPORT)){
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return getString(ReportMenu.class, locale, "cargos_in_day_report");
                }

                @Override
                public Class<? extends Page> getPage() {
                    return CargosInDayReportForm.class;
                }

                @Override
                public PageParameters getParameters() {
                    return PageParameters.NULL;
                }

                @Override
                public String getTagId() {
                    return "cargos_in_day_report";
                }
            });
        }
        if(application.hasAnyRole(SecurityRoles.REGIONAL_REPORT)){
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return getString(ReportMenu.class, locale, "regional_control_report");
                }

                @Override
                public Class<? extends Page> getPage() {
                    return RegionalControlReportForm.class;
                }

                @Override
                public PageParameters getParameters() {
                    return PageParameters.NULL;
                }

                @Override
                public String getTagId() {
                    return "regional_control_report";
                }
            });
        }

        return links;
    }

    @Override
    public String getTagId() {
        return "reports_menu";
    }

}
