package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ItemService {
    Page<Item> findAll(Pageable pageable);
    int getItemQuantityInWarehouse(Item item);
    Map<Store, Integer> getMapWithStoresAndQuantitiesForItem(Item item);
    Optional<Item> findItemByPublicId(UUID publicId);
    long pageCount(int itemsPerPage);
    List<Item> getItemsForRelatedProducts(int howMany);
    Store getWarehouse();
    boolean isItemExistsByPublicId(UUID itemPublicId);
}
