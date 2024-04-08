package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode
public class BDStewardshipRoleRes {

    private String uuid;
    private String name;
    private String description;
    private Integer order;
    private String roleCategory;
    private List<BDPermissionRes> permissions;
    private Set<BDStewardshipRoleResourceRes> resources;

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

    public List<BDPermissionRes> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<BDPermissionRes> permissions) {
        this.permissions = permissions;
    }

    public Set<BDStewardshipRoleResourceRes> getResources() {
        return resources;
    }

    public void setResources(Set<BDStewardshipRoleResourceRes> resources) {
        this.resources = resources;
    }
}
