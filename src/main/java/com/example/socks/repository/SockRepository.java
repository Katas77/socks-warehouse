package com.example.socks.repository;


import com.example.socks.model.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SockRepository extends JpaRepository<Sock, Long> {

    int countByColorAndCottonPart(String color, int cottonPart);

   Optional<Sock> findByColorAndCottonPart(String color, int cottonPart);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE "
            + "(:color IS NULL OR s.color = :color) AND "
            + "(:cottonPart IS NULL OR CASE "
            + "WHEN :comparison = 'moreThan' THEN s.cottonPart > :cottonPart "
            + "WHEN :comparison = 'lessThan' THEN s.cottonPart < :cottonPart "
            + "WHEN :comparison = 'equal' THEN s.cottonPart = :cottonPart "
            + "END)")
    Integer countSocksByFilter(
            @Param("color") String color,
            @Param("comparison") String comparison,
            @Param("cottonPart") Integer cottonPart);
}


