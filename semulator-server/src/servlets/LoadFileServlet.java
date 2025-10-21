package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.LoadReport;
import dto.UserInfo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import semulator.core.v3.SEmulatorEngineV3;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.users.UserManager;

import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "servlets.LoadFileServlet", urlPatterns = "/load-file")
@MultipartConfig(fileSizeThreshold = 1024 * 32, maxFileSize = 1024L * 1024L * 20)
public class LoadFileServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        ServletContext ctx = getServletContext();
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute(Constants.ENGINE);
        Object lock = ServletUtils.getUploadLock(ctx);

        try {
            Part filePart = req.getPart("file");
            String username = SessionUtils.getUsername(req);

            if (username == null || username.isEmpty()) {
                write(resp, new LoadReport(false, "No active session"));
                return;
            }
            if (filePart == null || filePart.getSize() == 0) {
                write(resp, new LoadReport(false, "No file uploaded"));
                return;
            }

            LoadReport loadReport;
            synchronized (lock) {
                try (InputStream in = filePart.getInputStream()) {
                    loadReport = engine.loadProgramDetails(in, username);
                }

                if (loadReport.isSuccess()) {
                    UserManager userManager = ServletUtils.getUserManager(ctx);
                    UserInfo user = userManager.getUsers().get(username);
                    if (user != null) {
                        user.updateProgramsNumber(loadReport.getProgramsNumber());
                        user.updateFunctionsNumber(loadReport.getFunctionsNumber());
                    }
                }
            }

            write(resp, loadReport);

        } catch (Exception ex) {
            write(resp, new LoadReport(false, ex.getMessage() != null ? ex.getMessage() : "Upload error"));
        }
    }

    private void write(HttpServletResponse resp, LoadReport loadReport) throws IOException {
        resp.getWriter().write(gson.toJson(loadReport));
    }
}