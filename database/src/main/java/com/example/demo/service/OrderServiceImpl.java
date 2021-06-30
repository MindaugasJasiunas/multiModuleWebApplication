package com.example.demo.service;

import com.example.demo.dao.CartItemRepository;
import com.example.demo.dao.CartRepository;
import com.example.demo.dao.OrderItemRepository;
import com.example.demo.dao.OrderRepository;
import com.example.demo.entity.*;
import com.example.demo.entity.authentication.UserEntity;
import org.springframework.stereotype.Service;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.Iterator;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    public OrderServiceImpl(OrderRepository orderRepo, OrderItemRepository orderItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Override
    public Order saveOrder(Order order){
        return orderRepo.save(order);
    }

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem){
        return orderItemRepo.save(orderItem);
    }
}
