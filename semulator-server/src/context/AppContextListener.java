package context;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        SEmulatorEngine engine = new SEmulatorEngineImpl();

        ctx.setAttribute("engine", engine);
        System.out.println("Server initialized and Engine stored in context.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Shutting down...");
    }
}
