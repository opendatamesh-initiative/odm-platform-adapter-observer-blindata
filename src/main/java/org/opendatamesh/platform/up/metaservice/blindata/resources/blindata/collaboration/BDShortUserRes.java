package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration;

public class BDShortUserRes {
    private String uuid;
    private String username;
    private String fullName;
    private String displayName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "BDShortUserRes{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
