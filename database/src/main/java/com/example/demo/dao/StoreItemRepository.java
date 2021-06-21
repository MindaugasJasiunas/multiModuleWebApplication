package com.example.demo.dao;

import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import com.example.demo.entity.StoreItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreItemRepository extends CrudRepository<StoreItem, Long> {
    @Query("SELECT t from StoreItem t WHERE t.item=:item AND t.store=:store")
    Optional<StoreItem> findStoreItemByItemAndStore(@Param("item") Item item, @Param("store") Store store);

    Iterable<StoreItem> findStoreItemsByItem_Id(Long itemId);
}
