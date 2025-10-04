package context;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import semulator.core.v3.SEmulatorEngineV3;
import semulator.core.v3.SEmulatorEngineV3Impl;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        SEmulatorEngineV3 engine = new SEmulatorEngineV3Impl();

        ctx.setAttribute("engine", engine);
        System.out.println("Server initialized and Engine stored in context.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Shutting down...");
    }
}
