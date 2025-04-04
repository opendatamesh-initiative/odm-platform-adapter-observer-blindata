package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions;

//PAY ATTENTION, COPIED FROM ODM-PLATFORM!!!
public interface OdmPlatformApiStandardErrors {
    String code();

    String description();

    static OdmPlatformApiStandardErrors getNotFoundError(String className) {
        return null;
    }
}
