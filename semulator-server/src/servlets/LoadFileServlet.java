package servlets;

import com.google.gson.Gson;
import dto.LoadReport;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import semulator.core.v3.SEmulatorEngineV3;
import utils.SessionUtils;

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
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute("engine");

        try {
            Part filePart = req.getPart("file");
          //TODO GET USERNAME FROM SESSION:  String owner   = req.getParameter("user");
            String usernameFromSession = SessionUtils.getUsername(req);
            if (filePart == null || filePart.getSize() == 0) {
                write(resp, new LoadReport(false, "No file uploaded"));
                return;
            }

            try (InputStream in = filePart.getInputStream()) {
                LoadReport loadReport = engine.loadProgramDetails(in, usernameFromSession);
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
