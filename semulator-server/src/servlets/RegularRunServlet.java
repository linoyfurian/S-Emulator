package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.ExecutionRunDto;
import dto.RunProgramRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import semulator.core.v3.SEmulatorEngineV3;
import utils.SessionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "servlets.RegularRunServlet", urlPatterns = "/regular-run")
public class RegularRunServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        // Parse request body
        RunProgramRequest requestObj = gson.fromJson(reader, RunProgramRequest.class);

        String programName = requestObj.getProgramName();
        boolean isProgram = requestObj.isProgram();
        int degreeOfExpand = requestObj.getDegreeOfExpand();
        long[] inputs = requestObj.getInputs();
        Map<String, Long> originalInputs = requestObj.getOriginalInputs();
        String architecture = requestObj.getArchitecture();
        long credits = requestObj.getCredits();

        String username = SessionUtils.getUsername(req);

        SEmulatorEngineV3 engine = (SEmulatorEngineV3) getServletContext().getAttribute(Constants.ENGINE);

        ExecutionRunDto result = engine.runProgram(credits, username, architecture, degreeOfExpand, programName, isProgram, originalInputs, inputs);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(result));
    }

}

