package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

class DataStoreApiInfo {
    private String databaseName;
    private String nameSpace;
    private String title;
    private String summary;
    private String version;
    private String datastoreName;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDatastoreName() {
        return datastoreName;
    }

    public void setDatastoreName(String datastoreName) {
        this.datastoreName = datastoreName;
    }
}
