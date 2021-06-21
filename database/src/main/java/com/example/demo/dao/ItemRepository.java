package com.example.demo.dao;

import com.example.demo.entity.Item;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
    Optional<Item> findItemByPublicId(UUID publicID);
}
