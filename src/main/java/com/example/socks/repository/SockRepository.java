package com.example.socks.repository;

import com.example.socks.model.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SockRepository extends JpaRepository<Sock, Long> {
    List<Sock> findByCottonPartBetween(int minCottonPart, int maxCottonPart);
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

/*@Query("SELECT SUM(s.quantity) FROM Sock s WHERE "
@Query(...): Это аннотация, используемая в Spring Data JPA для указания пользовательского запроса.
SELECT SUM(s.quantity): Мы выбираем сумму (SUM) количества (quantity) носок (s). Это означает, что мы будем получать общее количество носок, которое соответствует определенным условиям.
FROM Sock s: Запрос идет из таблицы Sock, где s — это алиас, который используется для ссылки на объект Sock в запросе.
        + "(:color IS NULL OR s.color = :color) AND "
        (:color IS NULL OR s.color = :color): Это условие проверяет, является ли передаваемый параметр color равным NULL или совпадает с цветом носка (s.color). Таким образом, если color не задан (т.е. равен NULL), это условие считается истинным, и все носки будут включены в сумму. Если color задан, то выбираются только те носки, которые соответствуют указанному цвету.
        + "(:cottonPart IS NULL OR CASE "
        (:cottonPart IS NULL OR CASE): Здесь начинается новое условие: если cottonPart равен NULL, оно также считается истинным, и будет выбрано все. В противном случае будет проверяться следующее условие.
 + "WHEN :comparison = 'moreThan' THEN s.cottonPart > :cottonPart "
WHEN :comparison = 'moreThan': Если параметр comparison равен moreThan, следующая часть условия (THEN) будет применена.
THEN s.cottonPart > :cottonPart: Соответствующие носки будут выбраны, если содержание хлопка (cottonPart) в них больше, чем указанное значение cottonPart.
        + "WHEN :comparison = 'lessThan' THEN s.cottonPart < :cottonPart "
WHEN :comparison = 'lessThan': Если параметр comparison равен lessThan, то следующее условие будет применено.
THEN s.cottonPart < :cottonPart: Носки будут выбраны, если содержание хлопка в них меньше, чем указанное значение.
        + "WHEN :comparison = 'equal' THEN s.cottonPart = :cottonPart "
WHEN :comparison = 'equal': Если параметр comparison равен equal, это условие будет применено.
THEN s.cottonPart = :cottonPart: Носки будут выбраны, если содержание хлопка в них равно указанному значению.
        + "END)"
END): Завершает конструкцию CASE. Этот оператор используется для проверки значений, основанных на переданном параметре comparison.*/
