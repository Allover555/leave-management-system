package com.leave.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;

    public static <T> PageResult<T> of(Page<?> page, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotalElements());
        result.setPage(page.getNumber() + 1);
        result.setSize(page.getSize());
        result.setTotalPages(page.getTotalPages());
        return result;
    }
}
