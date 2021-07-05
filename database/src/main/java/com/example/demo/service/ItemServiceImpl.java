package com.example.demo.service;

import com.example.demo.dao.ItemRepository;
import com.example.demo.dao.StoreItemRepository;
import com.example.demo.dao.StoreRepository;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import com.example.demo.entity.StoreItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j

@Service
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final StoreItemRepository storeItemRepository;

    public ItemServiceImpl(ItemRepository itemRepository, StoreRepository storeRepository, StoreItemRepository storeItemRepository) {
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
        this.storeItemRepository = storeItemRepository;
    }

    @Override
    public Page<Item> findAll(Pageable pageable){
        return itemRepository.findAll(pageable);
    }

    @Override
    public int getItemQuantityInWarehouse(Item item){
        int leftInWarehouse=0;
        if(storeRepository.getStoreByStoreTitle("Warehouse").isPresent()){
            Store warehouse= storeRepository.getStoreByStoreTitle("Warehouse").get();
            if(storeItemRepository.findStoreItemByItemAndStore(item, warehouse).isPresent()){
                StoreItem warehouseStoreItem= storeItemRepository.findStoreItemByItemAndStore(item, warehouse).get();
                leftInWarehouse=warehouseStoreItem.getQuantity();
            }
        }
        return leftInWarehouse;
    }


    @Override
    public Map<Store, Integer> getMapWithStoresAndQuantitiesForItem(Item item) {
        Map<Store, Integer> storeWithQuantity=new HashMap<>();

        Iterable<StoreItem> storeWithItemQuantity= storeItemRepository.findStoreItemsByItem_Id(item.getId());
        storeWithItemQuantity.forEach(storeItem -> storeWithQuantity.put(storeItem.getStore(), storeItem.getQuantity()));
        return storeWithQuantity;
    }

    @Override
    public Optional<Item> findItemByPublicId(UUID publicId){
        return itemRepository.findItemByPublicId(publicId);
    }

    @Override
    public long pageCount(int itemsPerPage){
        // return how many pages there is at all
        long countElements= itemRepository.count();
        long fullPages = countElements/itemsPerPage;
        long notFullPages= countElements%itemsPerPage;
        return notFullPages==0 ? fullPages : fullPages+1;
    }

    @Override
    public List<Item> getItemsForRelatedProducts(int howMany) {
        //randomize items:
        List<Item> itemsRandomized=new ArrayList<>();
        if(itemRepository.findAll() instanceof List<?>){
            List<Item> items= (List<Item>) itemRepository.findAll();
            int count= (int) itemRepository.count();
            for(int i=0;i<howMany; i++){
                itemsRandomized.add(items.get(new Random().nextInt(count)));
            }
        }
        return itemsRandomized;
    }


    @Override
    public Store getWarehouse(){
        if(storeRepository.getStoreByStoreTitle("Warehouse").isPresent()) {
            return storeRepository.getStoreByStoreTitle("Warehouse").get();
        }
        log.error("Warehouse not found!");
        return null;
    }

    @Override
    public boolean isItemExistsByPublicId(UUID itemPublicId){
        if(itemRepository.findItemByPublicId(itemPublicId).isPresent()){
            return true;
        }
        return false;
    }


    @Override
    public void deleteFromWarehouse(CartItem cartItem) {
        if(isItemExistsByPublicId(cartItem.getItem().getPublicId())){
            if(storeItemRepository.findStoreItemByItemAndStore(cartItem.getItem(), getWarehouse()).isPresent()){
                StoreItem storeItem= storeItemRepository.findStoreItemByItemAndStore(cartItem.getItem(), getWarehouse()).get();
                int quantity= storeItem.getQuantity()-cartItem.getQuantity();
                storeItem.setQuantity(quantity);
                storeItem=storeItemRepository.save(storeItem);
            }
        }
    }
}
