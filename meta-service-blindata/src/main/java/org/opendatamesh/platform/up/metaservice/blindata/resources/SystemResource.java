package org.opendatamesh.platform.up.metaservice.blindata.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SystemResource {

    private String uuid;
    private String name;
    private String description;
    private SystemSubtype subtype;
    private List<AdditionalPropertyResource> additionalProperties =  new ArrayList<>(0);

    public SystemResource(){

    }
    public SystemResource(String name) {
        this.name = name;
    }

    public SystemResource(String name, String description, SystemSubtype subtype) {
        this.name=name;
        this.description=description;
        this.subtype=subtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SystemSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(SystemSubtype subtype) {
        this.subtype = subtype;
    }

    public List<AdditionalPropertyResource> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<AdditionalPropertyResource> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SystemResource)) {
            return false;
        }
        SystemResource blindataSystem = (SystemResource) o;
        return Objects.equals(uuid, blindataSystem.uuid) && Objects.equals(name, blindataSystem.name) && Objects.equals(description, blindataSystem.description) && Objects.equals(subtype, blindataSystem.subtype) && Objects.equals(additionalProperties, blindataSystem.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, description, subtype, additionalProperties);
    }


    @Override
    public String toString() {
        return "{" +
            " uuid='" + getUuid() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", subtype='" + getSubtype() + "'" +
            ", additionalProperties='" + getAdditionalProperties() + "'" +
            "}";
    }


}