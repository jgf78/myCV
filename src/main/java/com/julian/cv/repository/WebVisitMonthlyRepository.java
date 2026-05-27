package com.julian.cv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.julian.cv.entity.WebVisitMonthly;

public interface WebVisitMonthlyRepository extends JpaRepository<WebVisitMonthly, Long> {

    Optional<WebVisitMonthly> findByYearAndMonth(int year, int month);
}
