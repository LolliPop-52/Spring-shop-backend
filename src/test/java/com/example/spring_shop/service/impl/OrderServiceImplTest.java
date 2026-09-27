package com.example.spring_shop.service.impl;

import com.example.spring_shop.domain.*;
import com.example.spring_shop.domain.Order;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.CreatorNewOrderDetailsDTO;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.dto.OrderDetailsDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.mapper.OrderMapper;
import com.example.spring_shop.repository.OrderRepository;
import com.example.spring_shop.repository.PickupPointRepository;
import com.example.spring_shop.repository.ProductRepository;
import com.example.spring_shop.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class OrderServiceImplTest {

    @Mock
    private PickupPointRepository pickupPointRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BucketServiceImpl bucketService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CreatorNewOrderDTO creatorNewOrderDTO;
    private User user;
    private PickupPoint pickupPoint;
    private Product product;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = TestDataFactory.createOrderDTO();
        product = TestDataFactory.createProduct();
        creatorNewOrderDTO = TestDataFactory.createCreatorNewOrderDTO();
        user = TestDataFactory.createUser();
        pickupPoint = TestDataFactory.createPickupPoint();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("createOrder: Успешное создание заказа с несколькими позициями")
    void createOrder_Success() {
        Map<Long, BigDecimal> productPrices = Map.of(
                1L, BigDecimal.valueOf(1000),
                2L, BigDecimal.valueOf(2500),
                3L, BigDecimal.valueOf(3700)
        );

        List<CreatorNewOrderDetailsDTO> items = productPrices.entrySet().stream()
                .map(entry -> new CreatorNewOrderDetailsDTO(entry.getKey(), entry.getValue(), BigDecimal.ONE))
                .toList();

        creatorNewOrderDTO = TestDataFactory.createCreatorNewOrderDTO(dto -> {
            dto.userEmail(user.getEmail());
            dto.addressId(pickupPoint.getId());
            dto.orderDetails(items);
        });

        Order curOrder = Order.builder()
                .id(1L)
                .user(user)
                .pickupPoint(pickupPoint)
                .deliveryStatus(DeliveryStatus.PROCESSING)
                .paymentStatus(PaymentStatus.UNPAID)
                .build();

        List<OrderDetails> orderDetails = productPrices.entrySet().stream()
                .map(entry -> OrderDetails.builder()
                        .id(1L)
                        .order(curOrder)
                        .product(Product.builder().id(entry.getKey()).price(entry.getValue()).build())
                        .amount(BigDecimal.ONE)
                        .totalPrice(entry.getValue())
                        .deliveryStatus(DeliveryStatus.PROCESSING)
                        .paymentType(PaymentType.ONLINE)
                        .paymentStatus(PaymentStatus.PAID)
                        .build()).toList();

        BigDecimal totalSum = orderDetails.stream()
                .map(OrderDetails::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = orderDetails.stream()
                .map(OrderDetails::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        curOrder.setOrderDetails(orderDetails);
        curOrder.setTotalPrice(totalSum);
        curOrder.setTotalAmount(totalAmount);

        OrderDTO orderDTO = TestDataFactory.createOrderDTO();

        when(productRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            BigDecimal price = productPrices.get(id);
            if (price == null) {
                return Optional.empty();
            }
            return Optional.of(TestDataFactory.createProduct().toBuilder().id(id).price(price).build());
        });

        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(pickupPointRepository.findById(pickupPoint.getId())).thenReturn(Optional.of(pickupPoint));
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(creatorNewOrderDTO);

        assertNotNull(result);

        verify(bucketService, times(1)).clearOrderedItems(creatorNewOrderDTO);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        verify(orderMapper, times(1)).toDto(savedOrder);

        assertEquals(curOrder.getUser(), savedOrder.getUser());
        assertEquals(curOrder.getPickupPoint(), savedOrder.getPickupPoint());
        assertEquals(curOrder.getTotalPrice(), savedOrder.getTotalPrice());
        assertEquals(curOrder.getTotalAmount(), savedOrder.getTotalAmount());
        assertEquals(curOrder.getDeliveryStatus(), savedOrder.getDeliveryStatus());
        assertEquals(curOrder.getPaymentStatus(), savedOrder.getPaymentStatus());

    }


    @Test
    @DisplayName("createOrder: Товар не найден")
    public void createOrder_ProductNotFound_ThrowException(){

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())).thenReturn(Optional.of(user));
        when(pickupPointRepository.findById(creatorNewOrderDTO.getAddressId())).thenReturn(Optional.of(pickupPoint));

        assertThatThrownBy(() -> orderService.createOrder(creatorNewOrderDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + TestDataFactory.DEFAULT_ID + " not found");

        verify(orderRepository, never()).save(any(Order.class));

    }

    @Test
    @DisplayName("createOrder: Пользователь не найден")
    public void createOrder_UserNotFound_ThrowException(){

        when(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(creatorNewOrderDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + creatorNewOrderDTO.getUserEmail() + " not found");

        verify(orderRepository, never()).save(any(Order.class));

    }

    @Test
    @DisplayName("createOrder: Пункт выдачи не найден")
    public void createOrder_PickupPointNotFound_ThrowException(){

        when(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())).thenReturn(Optional.of(user));
        when(pickupPointRepository.findById(creatorNewOrderDTO.getAddressId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(creatorNewOrderDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + creatorNewOrderDTO.getUserEmail() + " not found");

        verify(orderRepository, never()).save(any(Order.class));

    }

    @Test
    @DisplayName("createOrder: Несовпадение цены товара выбрасывает DataIntegrityViolationException")

    public void createOrder_PriceMismatch_ThrowsException() {

        Product mockProduct = product.toBuilder().price(BigDecimal.valueOf(9999)).build();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mockProduct));
        when(userRepository.findFirstByEmail(creatorNewOrderDTO.getUserEmail())).thenReturn(Optional.of(user));
        when(pickupPointRepository.findById(creatorNewOrderDTO.getAddressId())).thenReturn(Optional.of(pickupPoint));

        assertThatThrownBy(() -> orderService.createOrder(creatorNewOrderDTO))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Price mismatch for product");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("getAllActiveOrder: Получение всех активных заказов пользователя")
    void getAllActiveOrder_Success () {
        Order order = TestDataFactory.createOrder().toBuilder()
                .id(1L)
                .user(user)
                .pickupPoint(pickupPoint)
                .totalPrice(BigDecimal.valueOf(3000))
                .build();

        when(orderRepository.findAllByUserEmail(creatorNewOrderDTO.getUserEmail())).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDTO);

        List<OrderDTO> orders = orderService.getAllActiveOrder(creatorNewOrderDTO.getUserEmail()).getOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(1L, orders.getFirst().getId());
        verify(orderRepository).findAllByUserEmail(creatorNewOrderDTO.getUserEmail());
    }
}