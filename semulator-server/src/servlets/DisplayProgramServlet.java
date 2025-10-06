package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.ProgramFunctionDto;
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

@WebServlet("/display-program")
public class DisplayProgramServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String programName = req.getParameter(Constants.PROGRAM_NAME);
        String isProgram = req.getParameter(Constants.IS_PROGRAM);
        boolean isProgramBool = false;
        if(isProgram.equals("true")) {
            isProgramBool = true;
        }
        ServletContext ctx = getServletContext();
        SEmulatorEngineV3 engine = (SEmulatorEngineV3) ctx.getAttribute(Constants.ENGINE);

        ProgramFunctionDto programDetails = engine.displayProgram(programName, isProgramBool);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String jsonResponse = gson.toJson(programDetails);
        resp.getWriter().write(jsonResponse);
    }
}
