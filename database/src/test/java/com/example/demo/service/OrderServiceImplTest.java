package com.example.demo.service;

import com.example.demo.dao.OrderItemRepository;
import com.example.demo.dao.OrderRepository;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepo;
    @Mock
    private OrderItemRepository orderItemRepo;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void saveOrder() {
        //given
        Order order=new Order();
        Mockito.when(orderRepo.save(any(Order.class))).thenReturn(order);

        //when
        Order orderSaved= orderService.saveOrder(order);

        //then
        assertNotNull(orderSaved);
        assertEquals(order, orderSaved);
    }

    @Test
    void saveOrderItem() {
        //given
        OrderItem orderItem=new OrderItem();
        Mockito.when(orderItemRepo.save(any(OrderItem.class))).thenReturn(orderItem);

        //when
        OrderItem orderItemSaved= orderService.saveOrderItem(orderItem);

        //then
        assertNotNull(orderItemSaved);
        assertEquals(orderItem, orderItemSaved);
    }
}