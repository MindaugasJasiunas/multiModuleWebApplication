package com.example.demo.dao;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}
