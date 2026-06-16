package com.example.campusmart.common.page;

import java.util.List;

public class PageImpl<T> implements IPage<T> {
    private List<T> records;
    private long pages;
    private long total;

    public PageImpl() {}

    public PageImpl(List<T> records, long pages, long total) {
        this.records = records;
        this.pages = pages;
        this.total = total;
    }

    @Override
    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    @Override
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}