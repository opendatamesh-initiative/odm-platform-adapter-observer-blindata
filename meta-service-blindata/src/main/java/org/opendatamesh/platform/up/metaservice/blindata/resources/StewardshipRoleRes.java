package org.opendatamesh.platform.up.metaservice.blindata.resources;

import java.util.List;
import java.util.Set;

public class StewardshipRoleRes {

    private String uuid;
    private String name;
    private String description;
    private Integer order;
    private String roleCategory;
    private List<PermissionRes> permissions;
    private Set<StewardshipRoleResourceRes> resources;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(String roleCategory) {
        this.roleCategory = roleCategory;
    }

    public List<PermissionRes> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionRes> permissions) {
        this.permissions = permissions;
    }

    public Set<StewardshipRoleResourceRes> getResources() {
        return resources;
    }

    public void setResources(Set<StewardshipRoleResourceRes> resources) {
        this.resources = resources;
    }
}
