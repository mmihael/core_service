package dev.mm.core.coreservice.constants;

public class Uris {

    public static final String PUBLIC = "/public";
    public static final String API = "/api";
    public static final String CONTENT = "/content";

    public static final String USER = "/user";
    public static final String USER_ID = "/{userId}";

    public static final String ORGANIZATION = "/organization";
    public static final String ORGANIZATION_ID = "/{organizationId}";

    public static final String ROLE = "/role";

    public static final String APP_CONFIGURATION = "/app-configuration";

    public static final String FILE = "/file";
    public static final String FILE_ID = "/{fileId}";

    public static final String CHAT = "/chat";
    public static final String CHAT_ID = "/{chatId}";

    public static final String CHAT_MESSAGE = "/chat-message";
    public static final String CHAT_UNREAD_MESSAGE = "/chat-unread-message";

    public static final String API_LOGIN = API + "/login";
    public static final String API_LOGOUT = API + "/logout";

    public static final String API_USER = API + USER;
    public static final String API_USER_ID = API_USER + USER_ID;
    public static final String API_ROLE = API + ROLE;
    public static final String API_ORGANIZATION = API + ORGANIZATION;
    public static final String API_ORGANIZATION_ID = API_ORGANIZATION + ORGANIZATION_ID;
    public static final String API_ORGANIZATION_ID_ASSIGN_USER = API_ORGANIZATION_ID + "/assign-user";
    public static final String API_APP_CONFIGURATION = API + APP_CONFIGURATION;
    public static final String API_FILE = API + FILE;
    public static final String API_FILE_ID = API_FILE + FILE_ID;
    public static final String API_FILE_ID_CONTENT = API_FILE_ID + CONTENT;
    public static final String PUBLIC_API_FILE_ID_CONTENT = PUBLIC + API_FILE_ID + CONTENT;

    public static final String API_ORGANIZATION_USER = API_ORGANIZATION_ID + USER;
    public static final String API_ORGANIZATION_USER_ID = API_ORGANIZATION_ID + USER + USER_ID;
    public static final String API_ORGANIZATION_APP_CONFIGURATION = API_ORGANIZATION_ID + APP_CONFIGURATION;
    public static final String API_ORGANIZATION_FILE = API_ORGANIZATION_ID + FILE;
    public static final String API_ORGANIZATION_FILE_ID = API_ORGANIZATION_FILE + FILE_ID;
    public static final String API_ORGANIZATION_FILE_ID_CONTENT = API_ORGANIZATION_FILE + FILE_ID + CONTENT;
    public static final String PUBLIC_API_ORGANIZATION_FILE_ID_CONTENT = PUBLIC + API_ORGANIZATION_FILE_ID + CONTENT;

    public static final String API_ORGANIZATION_CHAT = API_ORGANIZATION_ID + CHAT;
    public static final String API_ORGANIZATION_CHAT_ID = API_ORGANIZATION_ID + CHAT + CHAT_ID;
    public static final String API_ORGANIZATION_CHAT_ID_CHAT_MESSAGE = API_ORGANIZATION_CHAT_ID + CHAT_MESSAGE;

}
