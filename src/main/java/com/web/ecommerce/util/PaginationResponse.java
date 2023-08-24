package com.web.ecommerce.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationResponse<T> {
    List<T> data;
    int currentPage;
    int totalItems;
    int totalPages;
}
