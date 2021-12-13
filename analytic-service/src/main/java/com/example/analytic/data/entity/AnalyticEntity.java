package com.example.analytic.data.entity;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "ANALYTIC_TABLE")
public class AnalyticEntity {

    @Id
    @NotNull
    @Column(name = "ID")
    @NotNull
    private UUID id;

    @NotNull
    @Column(name = "WORD")
    private String word;

    @NotNull
    @Column(name = "COUNT")
    private Long count;

    @Column(name = "RECORD_DATA")
    private LocalDateTime createdAt;

}
