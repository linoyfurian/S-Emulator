package servlets;

import com.google.gson.Gson;
import dto.ProgramInfo;
import dto.UserInfo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import semulator.core.v3.SEmulatorEngineV3;
import utils.users.UserManager;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@WebServlet("/programs")
public class ProgramsServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ProgramInfo> proggrams;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ServletContext ctx = getServletContext();
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute("engine");

        proggrams = engine.getPrograms();

        String jsonResponse = gson.toJson(proggrams);
        resp.getWriter().write(jsonResponse);
    }
}
