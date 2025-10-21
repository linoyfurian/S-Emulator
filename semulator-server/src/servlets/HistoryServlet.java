package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import semulator.core.v3.SEmulatorEngineV3;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "servlets.HistoryServlet", urlPatterns = "/history")
public class HistoryServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<RunResultDto> runHistory;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ServletContext ctx = getServletContext();
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute("engine");

        String user = req.getParameter("username");
        runHistory = engine.getUserRunHistory(user);

        String jsonResponse = gson.toJson(runHistory);
        resp.getWriter().write(jsonResponse);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Gson gson = new Gson();
        // Parse request body
        HistoryRequestDto requestObj = gson.fromJson(reader, HistoryRequestDto.class);

        String programName = requestObj.getProgramInContext();
        boolean isProgram = requestObj.isProgram();
        int degreeOfRun = requestObj.getDegreeOfRun();
        String architecture = requestObj.getArchitecture();
        DebugContextDto debugContext = requestObj.getDebugContext();

        SEmulatorEngineV3 engine = (SEmulatorEngineV3) getServletContext().getAttribute(Constants.ENGINE);

        engine.addCurrentRunToHistory(debugContext, degreeOfRun,programName,isProgram,architecture);
        resp.setCharacterEncoding("UTF-8");
    }
}
