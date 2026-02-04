package org.opendatamesh.platform.up.metaservice.blindata.services.v1.notificationevents;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "blindata")
public class BlindataProperties {

    private String url;
    private String user;
    private String password;
    private String tenantUUID;
    private String roleUuid;
    private String systemNameRegex;
    private String systemTechnologyRegex;
    private String dependsOnSystemNameRegex;
    private List<EventHandler> eventHandlers;

    // Getters and Setters

    public static class EventHandler {
        private String eventType;
        private String filter;
        private List<String> activeUseCases;

        // Getters and Setters

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public List<String> getActiveUseCases() {
            return activeUseCases;
        }

        public void setActiveUseCases(List<String> activeUseCases) {
            this.activeUseCases = activeUseCases;
        }

        @Override
        public String toString() {
            return "EventHandler {" + "\n" +
                    "\teventType='" + eventType + '\'' + ",\n" +
                    "\tfilter='" + filter + '\'' + ",\n" +
                    "\tactiveUseCases=" + activeUseCases + "\n" +
                    '}';
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantUUID() {
        return tenantUUID;
    }

    public void setTenantUUID(String tenantUUID) {
        this.tenantUUID = tenantUUID;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public String getSystemNameRegex() {
        return systemNameRegex;
    }

    public void setSystemNameRegex(String systemNameRegex) {
        this.systemNameRegex = systemNameRegex;
    }

    public String getSystemTechnologyRegex() {
        return systemTechnologyRegex;
    }

    public void setSystemTechnologyRegex(String systemTechnologyRegex) {
        this.systemTechnologyRegex = systemTechnologyRegex;
    }

    public List<EventHandler> getEventHandlers() {
        return eventHandlers;
    }

    public void setEventHandlers(List<EventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public String getDependsOnSystemNameRegex() {
        return dependsOnSystemNameRegex;
    }

    public void setDependsOnSystemNameRegex(String dependsOnSystemNameRegex) {
        this.dependsOnSystemNameRegex = dependsOnSystemNameRegex;
    }
}
