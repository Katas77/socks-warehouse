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


/*
SELECT SUM(s.quantity):
Этот фрагмент запроса определяет, что мы хотим получить сумму значений столбца quantity из сущности Sock.
SUM — это агрегирующая функция, которая суммирует количество носков.
FROM Sock s:
Указывает, что мы выбираем данные из таблицы, связанной с сущностью Sock, и используем псевдоним s для удобства обращения к полям класса Sock.
        WHERE:
        Условие фильтрации, которое определяет, какие записи будут участвовать в суммировании.
        (:color IS NULL OR s.color = :color):
Это условие позволяет фильтровать носки по цвету.
Если переменная color равна NULL, условие разрешает включение всех цветов (т.е. не применяет фильтр по цвету).
В противном случае, оно сравнивает цвет носков с указанным цветом (s.color = :color).
        (:cottonPart IS NULL OR CASE ... END):
Это условие проверяет процентное содержание хлопка, используя оператор CASE.
Если cottonPart равен NULL, условие также разрешает включение всех значений хлопка.
В противном случае, идет проверка на основе значения переменной comparison:
WHEN :comparison = 'moreThan' THEN s.cottonPart > :cottonPart:
Если comparison равно 'moreThan', выполняется проверка, чтобы процент хлопка был больше заданного (s.cottonPart > :cottonPart).
WHEN :comparison = 'lessThan' THEN s.cottonPart < :cottonPart:
Если comparison равно 'lessThan', выполняется проверка, чтобы процент хлопка был меньше заданного (s.cottonPart < :cottonPart).
WHEN :comparison = 'equal' THEN s.cottonPart = :cottonPart:
Если comparison равно 'equal', выполняется проверка на равенство (s.cottonPart = :cottonPart).
Параметры метода
@Param("color") String color:
Параметр, который передает цвет для фильтрации носков. Может быть NULL.
@Param("comparison") String comparison:
Параметр, который определяет тип сравнения ('moreThan', 'lessThan', или 'equal').
@Param("cottonPart") Integer cottonPart:
Параметр, который передает процент хлопка для фильтрации. Может быть NULL.
        Итог
Метод countSocksByFilter позволяет гибко считать общее количество носков на основе заданных критериев. Он может работать с различными условиями фильтрации, которые задаются через параметры, предоставляя пользователю возможность получать необходимую информацию без изменения запроса.*/
