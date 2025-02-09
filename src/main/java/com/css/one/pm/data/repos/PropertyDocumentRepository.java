package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.PropertyDocument;

public interface PropertyDocumentRepository extends JpaRepository<PropertyDocument, Long>, JpaSpecificationExecutor<PropertyDocument> {

}
