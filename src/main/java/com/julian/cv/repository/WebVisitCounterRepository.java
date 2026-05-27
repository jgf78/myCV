package com.julian.cv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.julian.cv.entity.WebVisitCounter;

public interface WebVisitCounterRepository extends JpaRepository<WebVisitCounter, Long> {
}