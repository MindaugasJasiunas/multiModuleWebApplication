package com.example.demo.service;

import com.example.demo.dao.ItemRepository;
import com.example.demo.dao.StoreItemRepository;
import com.example.demo.dao.StoreRepository;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Item;
import com.example.demo.entity.Store;
import com.example.demo.entity.StoreItem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)  //initializes mocks
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreItemRepository storeItemRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void findAll() {
        //given
        Pageable pageable= PageRequest.of(0, 1);
        Page<Item> page= Page.empty();

        Mockito.when(itemRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<Item> pageReturned= itemService.findAll(pageable);

        //then
        assertNotNull(pageReturned);
        assertEquals(page, pageReturned);
        Mockito.verify(itemRepository, Mockito.times(1)).findAll(any(Pageable.class));
    }

    @DisplayName("getItemQuantityInWarehouse() item doesn't exists - return 0")
    @Test
    void getItemQuantityInWarehouse_ItemDoesntExists_Return0() {
        //given
        Item item=new Item();
        Mockito.when(storeRepository.getStoreByStoreTitle(anyString())).thenReturn(Optional.empty());

        //when
        int result= itemService.getItemQuantityInWarehouse(item);

        //then
        assertEquals(0, result);

        Mockito.verify(storeRepository, Mockito.times(1)).getStoreByStoreTitle(anyString());
    }

    @DisplayName("getItemQuantityInWarehouse()")
    @Test
    void getItemQuantityInWarehouse() {
        //given
        Item item=new Item();
        Store warehouse=new Store();
        StoreItem storeItem=new StoreItem();
        storeItem.setItem(item);
        storeItem.setQuantity(5);

        Mockito.when(storeRepository.getStoreByStoreTitle(anyString())).thenReturn(Optional.of(warehouse));
        Mockito.when(storeItemRepository.findStoreItemByItemAndStore(any(Item.class), any(Store.class))).thenReturn(Optional.of(storeItem));

        //when
        int result= itemService.getItemQuantityInWarehouse(item);

        //then
        assertEquals(storeItem.getQuantity(), result);

        Mockito.verify(storeRepository, Mockito.times(2)).getStoreByStoreTitle(anyString());
        Mockito.verify(storeItemRepository, Mockito.times(2)).findStoreItemByItemAndStore(any(Item.class), any(Store.class));
    }

    @Test
    void getMapWithStoresAndQuantitiesForItem() {
        //given
        Item item=new Item();
        item.setId(1L);
        item.setTitle("temporary item");
        Store store=new Store();
        store.setId(1L);
        store.setStoreTitle("store 1");
        Store store2=new Store();
        store2.setId(2L);
        store2.setStoreTitle("store 2");
        StoreItem storeItem=new StoreItem();
        storeItem.setItem(item);
        storeItem.setStore(store);
        storeItem.setQuantity(5);
        StoreItem storeItem2=new StoreItem();
        storeItem2.setItem(item);
        storeItem2.setStore(store2);
        storeItem2.setQuantity(7);
        Iterable<StoreItem> storeItems= Arrays.asList(storeItem, storeItem2);

        Mockito.when(storeItemRepository.findStoreItemsByItem_Id(anyLong())).thenReturn(storeItems);

        //when
        Map<Store, Integer> storesWithQuantities= itemService.getMapWithStoresAndQuantitiesForItem(item);

        //then
        assertNotNull(storesWithQuantities);
        assertEquals(2, storesWithQuantities.size());
        assertTrue(storesWithQuantities.containsKey(store));
        assertTrue(storesWithQuantities.containsKey(store2));
        assertEquals(storeItem.getQuantity(), storesWithQuantities.get(store));
        assertEquals(storeItem2.getQuantity(), storesWithQuantities.get(store2));

        Mockito.verify(storeItemRepository, Mockito.times(1)).findStoreItemsByItem_Id(anyLong());
    }

    @Test
    void findItemByPublicId() {
        //given
        Item item= new Item();

        Mockito.when(itemRepository.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));

        //when
        Optional<Item> optionalItem= itemService.findItemByPublicId(UUID.randomUUID());

        //then
        assertNotNull(optionalItem);
        assertTrue(optionalItem.isPresent());
        assertEquals(item, optionalItem.get());

        Mockito.verify(itemRepository, Mockito.times(1)).findItemByPublicId(any(UUID.class));
    }

    @DisplayName("findItemByPublicId() not existing")
    @Test
    void findItemByPublicId_notExisting() {
        //given
        Mockito.when(itemRepository.findItemByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        //when
        Optional<Item> optionalItem= itemService.findItemByPublicId(UUID.randomUUID());

        //then
        assertNotNull(optionalItem);
        assertTrue(optionalItem.isEmpty());

        Mockito.verify(itemRepository, Mockito.times(1)).findItemByPublicId(any(UUID.class));
    }

    @DisplayName("pageCount() 2 pages")
    @Test
    void pageCount() {
        //given
        Mockito.when(itemRepository.count()).thenReturn(10L);

        //when
        long pages= itemService.pageCount(9);

        //then
        assertEquals(2, pages);

        Mockito.verify(itemRepository, Mockito.times(1)).count();
    }

    @DisplayName("pageCount() 0 elements - 0 pages")
    @Test
    void pageCount_ZeroElements() {
        //given
        Mockito.when(itemRepository.count()).thenReturn(0L);

        //when
        long pages= itemService.pageCount(9);

        //then
        assertEquals(0, pages);

        Mockito.verify(itemRepository, Mockito.times(1)).count();
    }

    @DisplayName("pageCount() 1 element - 1 page")
    @Test
    void pageCount_OneElement() {
        //given
        Mockito.when(itemRepository.count()).thenReturn(1L);

        //when
        long pages= itemService.pageCount(9);

        //then
        assertEquals(1, pages);

        Mockito.verify(itemRepository, Mockito.times(1)).count();
    }

    @Test
    void getItemsForRelatedProducts() {
        //given
        Item item=new Item();
        item.setId(1L);
        Item item2=new Item();
        item2.setId(2L);
        Item item3=new Item();
        item3.setId(3L);
        Item item4=new Item();
        item4.setId(4L);
        Iterable<Item> items=Arrays.asList(item, item2, item3, item4);

        Mockito.when(itemRepository.findAll()).thenReturn(items);
        Mockito.when(itemRepository.count()).thenReturn((long) ((List<Item>)items).size());

        //when
        List<Item> itemList= itemService.getItemsForRelatedProducts(2);

        //then
        assertNotNull(itemList);
        assertEquals(2, itemList.size());

        Mockito.verify(itemRepository, Mockito.times(2)).findAll();
        Mockito.verify(itemRepository, Mockito.times(1)).count();
    }

    @Test
    void getWarehouse() {
        //given
        Store warehouse=new Store();
        warehouse.setId(1L);
        warehouse.setStoreTitle("Warehouse");
        Mockito.when(storeRepository.getStoreByStoreTitle("Warehouse")).thenReturn(Optional.of(warehouse));

        //when
        Store store= itemService.getWarehouse();

        //then
        assertNotNull(store);
        assertEquals(warehouse.getId(), store.getId());
        assertEquals(warehouse.getStoreTitle(), store.getStoreTitle());

        Mockito.verify(storeRepository, Mockito.times(2)).getStoreByStoreTitle("Warehouse");
    }

    @DisplayName("getWarehouse() not exists - null")
    @Test
    void getWarehouse_NotExists() {
        //given
        Mockito.when(storeRepository.getStoreByStoreTitle("Warehouse")).thenReturn(Optional.empty());

        //when
        Store store= itemService.getWarehouse();

        //then
        assertNull(store);

        Mockito.verify(storeRepository, Mockito.times(1)).getStoreByStoreTitle("Warehouse");
    }

    @Test
    void isItemExistsByPublicId() {
        //given
        Item item=new Item();
        Mockito.when(itemRepository.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));

        //when
        boolean answer= itemService.isItemExistsByPublicId(UUID.randomUUID());

        //then
        assertTrue(answer);

        Mockito.verify(itemRepository, Mockito.times(1)).findItemByPublicId(any(UUID.class));
    }

    @DisplayName("isItemExistsByPublicId Not exist")
    @Test
    void isItemExistsByPublicId_NotExist() {
        //given
        Mockito.when(itemRepository.findItemByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        //when
        boolean answer= itemService.isItemExistsByPublicId(UUID.randomUUID());

        //then
        assertFalse(answer);

        Mockito.verify(itemRepository, Mockito.times(1)).findItemByPublicId(any(UUID.class));
    }

    @Test
    void deleteFromWarehouse() {
        //given
        Item item=new Item();
        //method calls isItemExistsByPublicId() method
        Mockito.when(itemRepository.findItemByPublicId(any(UUID.class))).thenReturn(Optional.of(item));
        Store store=new Store();
        //method calls getWarehouse() method
        Mockito.when(storeRepository.getStoreByStoreTitle("Warehouse")).thenReturn(Optional.of(store));
        StoreItem storeItem=new StoreItem();
        storeItem.setItem(item);
        storeItem.setStore(store);
        storeItem.setQuantity(10);
        Mockito.when(storeItemRepository.findStoreItemByItemAndStore(any(Item.class), any(Store.class))).thenReturn(Optional.of(storeItem));

        CartItem cartItem=new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(6);

        final ArgumentCaptor<StoreItem> storeItemArgumentCaptor= ArgumentCaptor.forClass(StoreItem.class);
        Mockito.when(storeItemRepository.save(storeItemArgumentCaptor.capture())).thenReturn(storeItem);

        //when
        itemService.deleteFromWarehouse(cartItem);

        //then
        StoreItem storeItemCaptured= storeItemArgumentCaptor.getValue();
        assertEquals(4, storeItemCaptured.getQuantity());
        assertEquals(store, storeItemCaptured.getStore());
        assertEquals(item, storeItemCaptured.getItem());

        Mockito.verify(itemRepository, Mockito.times(1)).findItemByPublicId(any(UUID.class));
        Mockito.verify(storeRepository, Mockito.times(4)).getStoreByStoreTitle("Warehouse");
        Mockito.verify(storeItemRepository, Mockito.times(2)).findStoreItemByItemAndStore(any(Item.class), any(Store.class));
        Mockito.verify(storeItemRepository, Mockito.times(1)).save(any(StoreItem.class));
    }
}