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

@WebServlet("/load-file")
@MultipartConfig(fileSizeThreshold = 1024 * 32, maxFileSize = 1024L * 1024L * 20)
public class LoadFileServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        ServletContext ctx = getServletContext();
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute(Constants.ENGINE);

        try {
            Part filePart = req.getPart("file");
            String usernameFromSession = SessionUtils.getUsername(req);


            System.out.println("usernameFromSession: " + usernameFromSession);
            if (filePart == null || filePart.getSize() == 0) {
                write(resp, new LoadReport(false, "No file uploaded"));
                return;
            }

            try (InputStream in = filePart.getInputStream()) {
                LoadReport loadReport = engine.loadProgramDetails(in, usernameFromSession);
                UserManager userManager = ServletUtils.getUserManager(ctx);
                UserInfo user = userManager.getUsers().get(usernameFromSession);
                user.updateProgramsNumber(loadReport.getProgramsNumber());
                user.updateFunctionsNumber(loadReport.getFunctionsNumber());
                write(resp, loadReport);
            }

        } catch (Exception ex) {
            write(resp, new LoadReport(false, ex.getMessage() != null ? ex.getMessage() : "Upload error"));
        }
    }

    private void write(HttpServletResponse resp, LoadReport loadReport) throws IOException {
        resp.getWriter().write(gson.toJson(loadReport));
    }
}
