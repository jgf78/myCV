package com.julian.cv.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "web_visit_counter")
public class WebVisitCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_visits")
    private Long totalVisits;

    @Column(name = "last_visit")
    private LocalDateTime lastVisit;
}
