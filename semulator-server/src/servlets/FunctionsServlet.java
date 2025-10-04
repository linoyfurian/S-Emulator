package servlets;

import com.google.gson.Gson;
import dto.FunctionInfo;
import dto.ProgramInfo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import semulator.core.v3.SEmulatorEngineV3;

import java.io.IOException;
import java.util.List;

@WebServlet("/functions")
public class FunctionsServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<FunctionInfo> functions;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ServletContext ctx = getServletContext();
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute("engine");

        functions = engine.getFunctions();

        String jsonResponse = gson.toJson(functions);
        resp.getWriter().write(jsonResponse);
    }
}
