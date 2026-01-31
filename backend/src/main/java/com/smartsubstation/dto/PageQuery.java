package com.smartsubstation.dto;

import lombok.Data;

/**
 * 分页查询请求
 */
@Data
public class PageQuery {

    /**
     * 当前页
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式
     */
    private String sortOrder = "desc";

    /**
     * 搜索关键词
     */
    private String keyword;
}
