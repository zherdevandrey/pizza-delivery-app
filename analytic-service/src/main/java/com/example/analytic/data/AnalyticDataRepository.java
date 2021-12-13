package com.example.analytic.data;

import com.example.analytic.data.entity.AnalyticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface AnalyticDataRepository extends JpaRepository<AnalyticEntity, String> {

    @Query(value = "SELECT a FROM AnalyticEntity a WHERE a.word=:word")
    Stream<AnalyticEntity> findByWord(@Param("word") String word);

}
