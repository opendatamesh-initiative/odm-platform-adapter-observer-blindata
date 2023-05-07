package org.opendatamesh.platform.up.metaservice.blindata.api;


import org.opendatamesh.platform.up.metaservice.blindata.entities.BlindataSystem;
import org.opendatamesh.platform.up.metaservice.blindata.entities.Credentials;

public interface BlindataSystemAPI {

    /**
     * Create a system on Blindata.
     *
     * @param system
     * @param credentials
     * @return null if it is discarded by a 409, the created object otherwise
     * @throws Exception when an error occurs
     */
    BlindataSystem postSystem(BlindataSystem system, Credentials credentials) throws Exception;

    /**
     * Deletes a system on Blindata.
     *
     * @param system
     * @param credentials
     * @return void
     * @throws Exception when an error occurs
     */
    void deleteSystem(BlindataSystem system, Credentials credentials) throws Exception;

    /**
     * Put a new version of the system in place of the previous one with the same uuid on Blindata.
     *
     * @param system
     * @param credentials
     * @return void
     * @throws Exception when an error occurs
     */
    BlindataSystem putSystem(BlindataSystem system, Credentials credentials) throws Exception;

    /**
     * Gets a system on Blindata from the uuid.
     *
     * @param uuid
     * @param credentials
     * @return DataCategory
     * @throws Exception when an error occurs
     */
    BlindataSystem getSystem(String uuid, Credentials credentials) throws Exception;

}
