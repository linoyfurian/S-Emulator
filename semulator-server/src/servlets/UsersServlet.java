package servlets;

import com.google.gson.Gson;
import dto.UserInfo;
import utils.users.UserManager;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Collection;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext ctx = getServletContext();
        UserManager userManager = (UserManager) ctx.getAttribute("userManager");

        Collection<UserInfo> users = userManager.getUsers().values();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String jsonResponse = gson.toJson(users);
        resp.getWriter().write(jsonResponse);
    }
}


