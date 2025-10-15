package servlets;

import dto.UserInfo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;
import utils.users.UserManager;

import java.io.IOException;
import java.util.Map;

@WebServlet("/credits")
public class CreditsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        ServletContext ctx = getServletContext();
        UserManager userManager = (UserManager) ctx.getAttribute("userManager");

        Map<String, UserInfo> users = userManager.getUsers();
        String username = SessionUtils.getUsername(req);
        UserInfo user = users.get(username);

        String operaion = req.getParameter("operation");
        String creditsStr = req.getParameter("credits");
        int credits = Integer.parseInt(creditsStr);

        if (operaion.equals("new"))
            user.setCredits(credits);
        else {
            user.updateUsedCredits(credits);
        }
    }
}
