package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration;

import java.util.List;
import java.util.Set;

public class BDStewardshipRoleRes {

    private String uuid;
    private String name;
    private String description;
    private Integer order;
    private String roleCategory;
    private List<String> permissions;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Set<BDStewardshipRoleResourceRes> getResources() {
        return resources;
    }

    public void setResources(Set<BDStewardshipRoleResourceRes> resources) {
        this.resources = resources;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDStewardshipRoleRes)) return false;
        final BDStewardshipRoleRes other = (BDStewardshipRoleRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$uuid = this.getUuid();
        final Object other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$order = this.getOrder();
        final Object other$order = other.getOrder();
        if (this$order == null ? other$order != null : !this$order.equals(other$order)) return false;
        final Object this$roleCategory = this.getRoleCategory();
        final Object other$roleCategory = other.getRoleCategory();
        if (this$roleCategory == null ? other$roleCategory != null : !this$roleCategory.equals(other$roleCategory))
            return false;
        final Object this$permissions = this.getPermissions();
        final Object other$permissions = other.getPermissions();
        if (this$permissions == null ? other$permissions != null : !this$permissions.equals(other$permissions))
            return false;
        final Object this$resources = this.getResources();
        final Object other$resources = other.getResources();
        if (this$resources == null ? other$resources != null : !this$resources.equals(other$resources)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDStewardshipRoleRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $uuid = this.getUuid();
        result = result * PRIME + ($uuid == null ? 43 : $uuid.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $order = this.getOrder();
        result = result * PRIME + ($order == null ? 43 : $order.hashCode());
        final Object $roleCategory = this.getRoleCategory();
        result = result * PRIME + ($roleCategory == null ? 43 : $roleCategory.hashCode());
        final Object $permissions = this.getPermissions();
        result = result * PRIME + ($permissions == null ? 43 : $permissions.hashCode());
        final Object $resources = this.getResources();
        result = result * PRIME + ($resources == null ? 43 : $resources.hashCode());
        return result;
    }
}
