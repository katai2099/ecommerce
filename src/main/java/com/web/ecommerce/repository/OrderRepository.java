package com.web.ecommerce.repository;

import com.web.ecommerce.model.MonthlySaleData;
import com.web.ecommerce.model.WeeklySaleData;
import com.web.ecommerce.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    Page<Order> findAllByUserId(Long userID, Pageable pageable);

    @Query("select o from Order o where o.id= ?1")
    Optional<Order> findByOrderUuid(UUID uuid);

    @Query("select o from Order o order by o.orderDate desc limit 5")
    List<Order> findTop5OrderByOrderDateDesc();

    @Query("select sum(o.TotalPrice) from Order o")
    Optional<Long> getTotalSales();

    @Query("select count(o) from Order o where o.orderDate between ?1 and ?2")
    Long getTodayOrders(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("select sum(o.TotalPrice) from Order o where o.orderDate between ?1 and ?2")
    Optional<Long> getTodaySales(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("select o.orderDate from Order o order by o.orderDate asc limit 1")
    Optional<LocalDateTime> getOldestOrderDate();

    @Query("select new com.web.ecommerce.model.MonthlySaleData(sum(o.TotalPrice), month(o.orderDate) ,year(o.orderDate)) from Order o where o.orderDate between ?1 and current date group by month (o.orderDate),year (o.orderDate)")
    List<MonthlySaleData> getLastTwelveMonthSales(LocalDateTime lastTwelveMonth);

    @Query("select new com.web.ecommerce.model.WeeklySaleData(sum(o.TotalPrice),DAYOFWEEK(o.orderDate)) from Order o where o.orderDate between ?1 and current_date group by DAYOFWEEK(o.orderDate)")
    List<WeeklySaleData> getLastSevenDaysSales(LocalDateTime lastSevenDays);
}
