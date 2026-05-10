package com.example.spring_shop.service;


import com.example.spring_shop.domain.*;
import com.example.spring_shop.dto.BucketItemDTO;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.dto.OrderDetailsDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.mapper.OrderMapper;
import com.example.spring_shop.repository.OrderRepository;
import com.example.spring_shop.repository.PickupPointRepository;
import com.example.spring_shop.repository.ProductRepository;
import com.example.spring_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PickupPointRepository pickupPointRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

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
                                .order(newOrder).
                                product(curProduct).
                                amount(c.getAmount()).
                                totalPrice(curProduct.getPrice().multiply(c.getAmount())).
                                deliveryStatus(DeliveryStatus.PROCESSING).
                                paymentType(PaymentType.valueOf(c.getPaymentType())).
                                build();
                    } else {
                        throw new DataIntegrityViolationException("Price mismatch for product");
                    }
                }).toList();

        newOrder.setUser(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException(creatorNewOrderDTO.getUserEmail())));
        newOrder.setPickupPoint(pickupPointRepository.findById(creatorNewOrderDTO.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("")));
        newOrder.setTotalPrice(creatorNewOrderDTO.getTotalPrice());
        newOrder.setTotalAmount(creatorNewOrderDTO.getTotalAmount());

        for (OrderDetails orderDetails : orderDetailsList) {
            newOrder.addDetails(orderDetails);
        }

        orderRepository.save(newOrder);

        return orderMapper.toDto(newOrder);
    }
}
