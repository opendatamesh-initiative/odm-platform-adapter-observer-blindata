package org.opendatamesh.platform.up.metaservice.server.database.repositories;

import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;
import org.opendatamesh.platform.up.metaservice.server.database.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByEventEntityId(String dataproduct);
    List<Notification> findByEventEntityIdOrderByIdDesc(String dataproduct);
    List<Notification> findByStatus(NotificationStatus status);
}
