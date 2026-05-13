package com.example.spring_shop.service;

import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.OrderDTO;

import java.util.List;


public interface OrderService {
    OrderDTO createOrder(CreatorNewOrderDTO creatorNewOrderDTO);
    List<OrderDTO> getAllActiveOrder(String email);
}
