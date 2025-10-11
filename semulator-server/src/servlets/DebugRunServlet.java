package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.DebugContextDto;
import dto.DebugProgramRequest;
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

@WebServlet("/debug-run")
public class DebugRunServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Gson gson = new Gson();

        // Parse request body
        DebugProgramRequest requestObj = gson.fromJson(reader, DebugProgramRequest.class);

        String programName = requestObj.getProgramName();
        boolean isProgram = requestObj.isProgram();
        int degreeOfExpand = requestObj.getDegreeOfExpand();
        long[] inputs = requestObj.getInputs();
        Map<String, Long> originalInputs = requestObj.getOriginalInputs();
        DebugContextDto debugContext = requestObj.getDebugContext();

        String username = SessionUtils.getUsername(req);
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) getServletContext().getAttribute(Constants.ENGINE);

        String isInitialDebug = req.getParameter("is_initial_debug");
        DebugContextDto result;
        if(isInitialDebug!=null){
            if(isInitialDebug.equals("true")) {
                result = engine.initialStartOfDebugger(username, programName, isProgram, degreeOfExpand, debugContext, originalInputs, inputs);
            }
            else{
                result = engine.debug(username, programName, isProgram, degreeOfExpand, debugContext, originalInputs);
            }
        }
        else
            result = engine.resume(username, programName, isProgram, degreeOfExpand, debugContext, originalInputs);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(result));
    }
}
