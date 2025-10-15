package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import dto.ExecutionRunDto;
import dto.ProgramFunctionDto;
import dto.RunProgramRequest;
import dto.UserInfo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import semulator.core.v3.SEmulatorEngineV3;
import utils.SessionUtils;
import utils.users.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@WebServlet("/regular-run")
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
        int credits = requestObj.getCredits();

        String username = SessionUtils.getUsername(req);

        SEmulatorEngineV3 engine = (SEmulatorEngineV3) getServletContext().getAttribute(Constants.ENGINE);

        ExecutionRunDto result = engine.runProgram(credits, username, architecture, degreeOfExpand, programName, isProgram, originalInputs, inputs);

        if (result == null) {
            return;
        }
        System.out.println(result.getResult());
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(result));
    }

}
