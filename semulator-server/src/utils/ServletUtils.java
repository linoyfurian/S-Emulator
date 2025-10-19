package utils;

import jakarta.servlet.ServletContext;
import utils.users.UserManager;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";

    private static final Object userManagerLock = new Object();

    private static final String UPLOAD_LOCK = "UPLOAD_LOCK";

    public static Object getUploadLock(ServletContext ctx) {
        Object lock = ctx.getAttribute(UPLOAD_LOCK);
        if (lock == null) {
            synchronized (ServletUtils.class) {
                lock = ctx.getAttribute(UPLOAD_LOCK);
                if (lock == null) {
                    lock = new Object();
                    ctx.setAttribute(UPLOAD_LOCK, lock);
                }
            }
        }
        return lock;
    }

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }
}