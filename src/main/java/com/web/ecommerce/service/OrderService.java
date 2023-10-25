package com.web.ecommerce.service;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.order.OrderDTO;
import com.web.ecommerce.dto.order.OrderDetailDTO;
import com.web.ecommerce.enumeration.OrderStatusEnum;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.MonthlySaleData;
import com.web.ecommerce.model.OrderAnalytic;
import com.web.ecommerce.model.SaleAnalytic;
import com.web.ecommerce.model.WeeklySaleData;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.model.order.OrderHistory;
import com.web.ecommerce.model.order.OrderStatus;
import com.web.ecommerce.repository.OrderRepository;
import com.web.ecommerce.repository.OrderStatusRepository;
import com.web.ecommerce.specification.order.OrderFilter;
import com.web.ecommerce.specification.order.OrderSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    public PaginationResponse<OrderDetailDTO> getOrders(OrderFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage() - 1, filter.getItemperpage(), Sort.by("orderDate").descending());
        OrderSpecificationBuilder builder = new OrderSpecificationBuilder();
        builder.withFilter(filter);
        Specification<Order> spec = builder.build();
        Page<Order> orderLists = orderRepository.findAll(spec, pageable);
        List<OrderDetailDTO> orders = OrderDetailDTO.toOrderDetailDTOs(orderLists.stream().toList());
        return PaginationResponse.<OrderDetailDTO>builder()
                .currentPage(filter.getPage())
                .totalPage(orderLists.getTotalPages())
                .totalItem(orderLists.getNumberOfElements())
                .data(orders)
                .build();
    }

    public PaginationResponse<OrderDTO> getUserOrders(int page) {
        Long userId = getUserIdFromSecurityContext();
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by("orderDate").descending());

        Page<Order> orderPage = orderRepository.findAllByUserId(userId, pageable);
        PaginationResponse<OrderDTO> orderDTOPaginationResponse = PaginationResponse.<OrderDTO>builder()
                .currentPage(page)
                .totalPage(orderPage.getTotalPages())
                .totalItem(orderPage.getTotalElements())
                .data(OrderDTO.toOrderDTOS(orderPage.toList()))
                .build();
        return orderDTOPaginationResponse;
    }

    public OrderDetailDTO getOrder(UUID orderId) {
        Order order = orderRepository.findByOrderUuid(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with Id " + orderId + " not found"));
        return OrderDetailDTO.toOrderDetailDTO(order);
    }

    @Transactional
    public String updateOrderStatus(UUID orderId, String status) {
        Order order = orderRepository.findByOrderUuid(orderId)
                .orElseThrow(() -> new InvalidContentException("Order with " + orderId + " does not exist"));
        String replacedStatus = status.toUpperCase().replaceAll("\\s", "_");
        OrderStatusEnum statusEnum = OrderStatusEnum.valueOf(replacedStatus);
        OrderStatus nextOrderStatus = orderStatusRepository.findByName(statusEnum.toString())
                .orElseThrow(() -> new InvalidContentException("Status does not exist"));
        order.setOrderStatus(nextOrderStatus);
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setStatus(nextOrderStatus);
        orderHistory.setActionTime(LocalDateTime.now());
        order.addOrderHistory(orderHistory);
        orderRepository.save(order);
        return statusEnum.toString();
    }

    public List<OrderDetailDTO> getRecentOrders() {
        List<Order> orders = orderRepository.findTop5OrderByOrderDateDesc();
        return OrderDetailDTO.toOrderDetailDTOs(orders);
    }

    public OrderAnalytic getOrderAnalytic() {
        OrderAnalytic orderAnalytic = new OrderAnalytic();
        orderAnalytic.setTotalOrders(getTotalOrders());
        orderAnalytic.setTotalSales(getTotalSales());
        orderAnalytic.setOldestOrderDate(getOldestOrderDate());
        orderAnalytic.setTodayOrders(getTodayOrders());
        orderAnalytic.setTodaySales(getTodaySales());
        return orderAnalytic;
    }

    public SaleAnalytic getSalesAnalytic() {
        List<MonthlySaleData> monthlySales = getLastTwelveMonthSalesData();
        List<WeeklySaleData> weeklySales = getLastSevenDaysSales();
        SaleAnalytic saleAnalytic = new SaleAnalytic();
        saleAnalytic.setMonthlySaleData(monthlySales);
        saleAnalytic.setWeeklySaleData(weeklySales);
        return saleAnalytic;
    }

    private Long getTotalOrders() {
        return orderRepository.count();
    }

    private String getOldestOrderDate() {
        return orderRepository.getOldestOrderDate().orElse(LocalDateTime.now()).toString();
    }

    private Long getTotalSales() {
        return orderRepository.getTotalSales().orElse(0L);
    }

    private Long getTodayOrders() {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime startOfDay = currentTimestamp.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = currentTimestamp.withHour(23).withMinute(59).withSecond(59);
        return orderRepository.getTodayOrders(startOfDay,endOfDay);
    }

    private Long getTodaySales() {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime startOfDay = currentTimestamp.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = currentTimestamp.withHour(23).withMinute(59).withSecond(59);
        return orderRepository.getTodaySales(startOfDay,endOfDay).orElse(0L);
    }

    private List<WeeklySaleData> getLastSevenDaysSales(){
        LocalDateTime lastSevenDays = LocalDateTime.now().minusDays(7);
        List<WeeklySaleData> saleData = orderRepository.getLastSevenDaysSales(lastSevenDays);
        return processWeeklySaleData(saleData);
    }

    private List<WeeklySaleData> processWeeklySaleData(List<WeeklySaleData> saleData){
        int currentDay = LocalDateTime.now().getDayOfWeek().getValue();
        int lastWeekDays = 7 - currentDay;
        boolean[] dayExists = new boolean[7];
        for (WeeklySaleData data : saleData) {
            dayExists[data.getDayOfWeek() - 1] = true;
        }
        if(isAllMonthExist(dayExists)){
            return saleData;
        }
        List<WeeklySaleData> saleDataList = new ArrayList<>();
        int saleDataCount = 0;
        for (int i = 0; i < lastWeekDays; i++) {
            WeeklySaleData data = new WeeklySaleData();
            data.setDayOfWeek(currentDay + i + 1);
            if (!dayExists[currentDay + i]){
                data.setTotalSales((double) 0);
            }else{
                data = saleData.get(saleDataCount);
                saleDataCount++;
            }
            saleDataList.add(data);
        }
        for (int i = 0; i < currentDay; i++) {
            WeeklySaleData data = new WeeklySaleData();
            if (!dayExists[i]) {
                data.setDayOfWeek(i + 1);
                data.setTotalSales((double) 0);
            } else {
                data = saleData.get(saleDataCount);
                saleDataCount++;
            }
            saleDataList.add(data);
        }
        return saleDataList;
    }

    private List<MonthlySaleData> getLastTwelveMonthSalesData() {
        LocalDateTime lastTwelveMonth = LocalDateTime.now().minusMonths(12);
        List<MonthlySaleData> saleData = orderRepository.getLastTwelveMonthSales(lastTwelveMonth);
        List<MonthlySaleData> processedSaleData = processMonthlySaleData(saleData);
        return processedSaleData;
    }

    private List<MonthlySaleData> processMonthlySaleData(List<MonthlySaleData> saleData) {
        int currentMonth = LocalDateTime.now().getMonthValue();
        int lastYearMonths = 12 - currentMonth;
        boolean[] monthExist = new boolean[12];
        for (MonthlySaleData data : saleData) {
            monthExist[data.getMonth() - 1] = true;
        }
        if(isAllMonthExist(monthExist)){
            return saleData;
        }
        List<MonthlySaleData> saleDataList = new ArrayList<>();
        int saleDataCount = 0;
        for (int i = 0; i < lastYearMonths; i++) {
            MonthlySaleData data = new MonthlySaleData();
            data.setMonth(currentMonth + i + 1);
            if(!monthExist[currentMonth + i]){
                data.setTotalSales((double) 0);
            }else{
                data = saleData.get(saleDataCount);
                saleDataCount++;
            }
            data.setYear(LocalDateTime.now().getYear() - 1);
            saleDataList.add(data);
        }
        for (int i = 0; i < currentMonth; i++) {
            MonthlySaleData data = new MonthlySaleData();
            if (!monthExist[i]) {
                data.setMonth(i + 1);
                data.setTotalSales((double) 0);
                data.setYear(LocalDateTime.now().getYear());
            } else {
                data = saleData.get(saleDataCount);
                saleDataCount++;
            }
            saleDataList.add(data);
        }
        return saleDataList;
    }

    private boolean isAllMonthExist(boolean[] months){
        for (boolean month : months) {
            if (!month) {
                return false;
            }
        }
        return true;
    }
}
