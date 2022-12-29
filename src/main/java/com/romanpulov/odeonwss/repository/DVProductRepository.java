package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.DVProduct;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DVProductRepository extends PagingAndSortingRepository<DVProduct, Long> {
}
