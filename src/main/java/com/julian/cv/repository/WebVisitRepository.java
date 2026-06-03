package com.julian.cv.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.julian.cv.entity.WebVisit;

@Repository
public interface WebVisitRepository extends JpaRepository<WebVisit, Long> {

    List<WebVisit> findAllByOrderByIdDesc(Pageable pageable);

    @Query("""
        SELECT
            w.country AS country,
            COUNT(w) AS visits
        FROM WebVisit w
        WHERE w.country IS NOT NULL
        GROUP BY w.country
        ORDER BY COUNT(w) DESC
    """)
    List<CountryVisitProjection> findVisitsByCountry();

    @Query("""
        SELECT
            w.country AS country,
            COUNT(w) AS visits
        FROM WebVisit w
        WHERE w.country IS NOT NULL
          AND YEAR(w.visitTime) = :year
          AND MONTH(w.visitTime) = :month
        GROUP BY w.country
        ORDER BY COUNT(w) DESC
    """)
    List<CountryVisitProjection> findMonthlyVisitsByCountry(
            int year,
            int month);
    
    @Query("""
            SELECT
                DAY(w.visitTime) AS day,
                COUNT(w) AS visits
            FROM WebVisit w
            WHERE YEAR(w.visitTime) = :year
              AND MONTH(w.visitTime) = :month
            GROUP BY DAY(w.visitTime)
            ORDER BY DAY(w.visitTime)
        """)
        List<DailyVisitProjection> findDailyVisits(
                int year,
                int month);
    
    List<WebVisit> findByVisitTimeBetween(LocalDateTime start, LocalDateTime end);
}