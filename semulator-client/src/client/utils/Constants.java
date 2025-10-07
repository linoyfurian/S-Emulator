package client.utils;

import com.google.gson.Gson;

public class Constants {

    // fxml locations
    public final static String DASHBOARD_FXML_RESOURCE_LOCATION = "/client/component/dashboard/dashboard.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";
    public final static String EXECUTION_FXML_RESOURCE_LOCATION = "/client/component/execution/execution.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/semulator";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOAD_FILE_PAGE = FULL_SERVER_PATH + "/load-file";
    public final static String USERS_PAGE = FULL_SERVER_PATH + "/users";
    public final static String PROGRAMS_PAGE = FULL_SERVER_PATH + "/programs";
    public final static String FUNCTIONS_PAGE = FULL_SERVER_PATH + "/functions";
    public final static String DISPLAY_SERVLET = FULL_SERVER_PATH + "/display-program";
    public final static String EXPAND_SERVLET = FULL_SERVER_PATH + "/expand";


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}

