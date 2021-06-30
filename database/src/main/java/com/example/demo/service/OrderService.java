package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    Order saveOrder(Order order);
    OrderItem saveOrderItem(OrderItem orderItem);
}
