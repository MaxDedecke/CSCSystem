package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.PropertyAnnouncement;

public interface PropertyAnnouncementRepository extends JpaRepository<PropertyAnnouncement, Long>, JpaSpecificationExecutor<PropertyAnnouncement>  {

}
