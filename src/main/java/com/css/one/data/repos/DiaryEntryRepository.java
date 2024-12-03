package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.DiaryEntry;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long>, JpaSpecificationExecutor<DiaryEntry>  {

}
