package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v2;

class AsyncApiInfo {
    private String title;
    private String version;
    private String description;
    private String termsOfService;
    /*
    private Contact contact;
    private License license;
    */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }
}
