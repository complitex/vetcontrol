package org.vetcontrol.sync.server.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.util.FileUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.03.2010 7:24:12
 */
@WebServlet
public class DownloadServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DownloadServlet.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        process(httpServletRequest, httpServletResponse);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Параметры запроса на загрузку файла
        String secureKey = request.getParameter("secureKey");
        String version = request.getParameter("version");
        String name = request.getParameter("name");           

        //Проверка параметров
        if (name == null || version == null){
            response.setContentType("html/plain; charset=UTF-8");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Не указан парамерт имени файла или версии");
            return;
        }

        //Проверка клиента
        if (secureKey == null){
            try {
                clientBean.getClient(secureKey);
            } catch (Exception e) {
                response.setContentType("html/plain; charset=UTF-8");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Клиент не зарегистрирован");
                return;
            }
        }

        //Открытие файла
        File file = FileUtil.getUpdateFile(version, name);

        if (!file.exists()){
            response.setContentType("html/plain; charset=UTF-8");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Файл не найден");
            return;
        }

        //Тип файла
        String contentType = getServletContext().getMimeType(file.getName());

        //Обработка запроса
        new ResumeDownloadServer().processRequest(request, response, file, contentType, true);
    }
}
