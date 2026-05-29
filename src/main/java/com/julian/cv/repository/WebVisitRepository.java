package com.julian.cv.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.julian.cv.entity.WebVisit;

@Repository
public interface WebVisitRepository extends JpaRepository<WebVisit, Long> {
    List<WebVisit> findAllByOrderByIdDesc(Pageable pageable);
}