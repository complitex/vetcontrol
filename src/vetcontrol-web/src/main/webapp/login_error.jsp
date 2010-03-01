<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    try {
        javax.naming.InitialContext context = new javax.naming.InitialContext();
        org.vetcontrol.service.ClientBean cb = (org.vetcontrol.service.ClientBean) context.lookup("java:module/ClientBean");
        try {
            cb.getCurrentClient();
        } catch (Exception e) {
            if (e.getCause() instanceof org.vetcontrol.sync.NotRegisteredException){
                response.sendRedirect("?wicket:bookmarkablePage=:org.vetcontrol.sync.client.web.pages.RegistrationPage");
            }
        }
    } catch (javax.naming.NamingException e) {
        e.printStackTrace();
    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" />

    <%@ taglib uri='http://java.sun.com/jstl/fmt' prefix='fmt'%>
    <fmt:setBundle basename='login_messages'/>

    <title><fmt:message key='login.title'/></title>
</head>
<body>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td id="Header">
        </td>
    </tr>
    <tr>
        <td id="TopPanel">
        </td>
    </tr>
    <tr>
        <td>
            <div align="center">
                <table border="0" cellspacing="0" cellpadding="0" class="formFrame"
                       style="width:300px;">
                    <tr valign="top">
                        <td class="ff_l"><img src="images/formFrame-tl.gif" width="4" height="180" alt=""/></td>
                        <td class="ff_t">
                            <h2 style="text-align:center;"><fmt:message key='login.errorTitle'/></h2>
                            <ul>
                                <li>
                                    <span style="color:orangered;"><fmt:message key='login.errorLabel'/></span>
                                </li>
                            </ul>
                            <div align="center">
                                <form  method="POST" action="j_security_check">
                                    <table style="margin-bottom:20px">
                                        <tr>
                                            <th align="left"><fmt:message key='login.loginLabel'/>:</th>
                                            <td>
                                                <input name="j_username" type="text" size="30"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th align="left"><fmt:message key='login.passwordLabel'/>:</th>
                                            <td>
                                                <input name="j_password" type="password" size="30"/>
                                            </td>
                                        </tr>
                                    </table>
                                    <input type="submit" name="submit" value="<fmt:message key='login.submit'/>" class="btnBig"/>
                                </form>
                            </div>
                        </td>
                        <td class="ff_r"><img src="images/formFrame-tr.gif" width="4" height="180" alt=""/></td>
                    </tr>
                    <tr>
                        <td class="ff_bl"><img src="images/formFrame-bl.gif" width="4" height="4" alt=""/></td>
                        <td class="ff_b"><img src="images/spacer.gif" width="4" height="4" alt=""/></td>
                        <td class="ff_br"><img src="images/formFrame-br.gif" width="4" height="4" alt=""/></td>
                    </tr>
                </table>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <div><img src="images/spacer.gif" width="800" height="1" alt=""/></div>
        </td>
    </tr>
</table>
</body>
</html>
