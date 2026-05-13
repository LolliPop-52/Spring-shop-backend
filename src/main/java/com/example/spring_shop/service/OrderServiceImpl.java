package com.example.spring_shop.service;


import com.example.spring_shop.domain.*;
import com.example.spring_shop.dto.BucketItemDTO;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.dto.OrderDetailsDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.mapper.OrderMapper;
import com.example.spring_shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {



    private final PickupPointRepository pickupPointRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    private final BucketService bucketService;

    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(CreatorNewOrderDTO creatorNewOrderDTO) {

        Order newOrder = new Order();
        
        List<OrderDetails> orderDetailsList = creatorNewOrderDTO.getOrderDetails().stream()
                .map(c -> {
                    Product curProduct = productRepository.findById(c.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException(creatorNewOrderDTO.getAddressId()));
                    if(curProduct.getPrice().compareTo(c.getPriceOnOrder()) == 0){
                        return OrderDetails.builder()
                                .order(newOrder)
                                .product(curProduct)
                                .amount(c.getAmount())
                                .createdTime(LocalDateTime.now())
                                .totalPrice(curProduct.getPrice().multiply(c.getAmount()))
                                .deliveryStatus(DeliveryStatus.PROCESSING)
                                .paymentType(PaymentType.valueOf(creatorNewOrderDTO.getPaymentType()))
                                .paymentStatus(PaymentStatus.UNPAID)
                                .build();
                    } else {
                        throw new DataIntegrityViolationException("Price mismatch for product");
                    }
                }).toList();

        newOrder.setUser(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException(creatorNewOrderDTO.getUserEmail())));
        newOrder.setPickupPoint(pickupPointRepository.findById(creatorNewOrderDTO.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(creatorNewOrderDTO.getUserEmail())));
        newOrder.setPaymentStatus(PaymentStatus.UNPAID);
        newOrder.setDeliveryStatus(DeliveryStatus.PROCESSING);

        for (OrderDetails orderDetails : orderDetailsList) {
            newOrder.addDetails(orderDetails);
        }

        bucketService.clearOrderedItems(creatorNewOrderDTO);

        orderRepository.save(newOrder);


        return orderMapper.toDto(newOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllActiveOrder(String email) {
        return orderRepository.findAllByUserEmail(email).stream()
                .map(orderMapper::toDto)
                .toList();
    }
}
