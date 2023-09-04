package com.web.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationResponse<T> {
    int currentPage;
    int totalItem;
    int totalPage;
    List<T> data;
}
