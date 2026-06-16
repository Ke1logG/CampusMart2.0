package com.example.campusmart.common.page;

import java.util.List;

public interface IPage<T> {
    List<T> getRecords();
    long getPages();
    long getTotal();
}