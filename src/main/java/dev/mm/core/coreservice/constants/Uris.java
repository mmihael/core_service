package dev.mm.core.coreservice.constants;

public class Uris {

    public static final String API = "/api";
    public static final String USER = "/user";
    public static final String ORGANIZATION = "/organization";
    public static final String APP_CONFIGURATION = "/app-configuration";

    public static final String API_LOGIN = API + "/login";
    public static final String API_LOGOUT = API + "/logout";

    public static final String API_USER = API + USER;
    public static final String API_USER_ID = API_USER + "/{userId}";

    public static final String API_ORGANIZATION = API + ORGANIZATION;
    public static final String API_ORGANIZATION_ID = API_ORGANIZATION + "/{organizationId}";
    public static final String API_ORGANIZATION_ID_ASSIGN_USER = API_ORGANIZATION_ID + "/assign-user";

    public static final String API_APP_CONFIGURATION = API + APP_CONFIGURATION;

    public static final String API_ORGANIZATION_USER = API_ORGANIZATION_ID + USER;
    public static final String API_ORGANIZATION_USER_ID = API_ORGANIZATION_ID + USER + "/{userId}";

    public static final String API_ORGANIZATION_APP_CONFIGURATION = API_ORGANIZATION_ID + APP_CONFIGURATION;

}
