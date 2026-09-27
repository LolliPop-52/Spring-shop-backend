package com.example.spring_shop.fixture;

import com.example.spring_shop.domain.*;
import com.example.spring_shop.dto.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestDataFactory {

    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_EMAIL = "test@gmail.com";
    public static final String DEFAULT_USER_NAME = "Ivan";
    public static final String DEFAULT_PRODUCT_TITLE = "TestProduct";
    public static final String DEFAULT_PRODUCT_DESCRIPTION = "Test Product Description";
    public static final String DEFAULT_PASSWORD = "12345";
    public static final String DEFAULT_CATEGORY_NAME = "TestCategory";
    public static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    public static final String DEFAULT_ADDRESS = "Test Address 123";
    public static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(1);
    public static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(1000);
    public static final LocalTime DEFAULT_MIN_LOCALTIME = LocalTime.of(9, 0);
    public static final LocalTime DEFAULT_MAX_LOCALTIME = LocalTime.of(21, 0);
    public static final LocalDateTime DEFAULT_MIN_LOCALDATETIME = LocalDateTime.of(2026, 1, 1, 10, 0);
    public static final LocalDateTime DEFAULT_MAX_LOCALDATETIME = LocalDateTime.of(2026, 1, 1, 12, 0);
    public static final UserRole DEFAULT_USER_ROLE = UserRole.CLIENT;
    public static final String DEFAULT_USER_ROLE_STRING = DEFAULT_USER_ROLE.name();
    public static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.UNPAID;
    public static final PaymentType DEFAULT_PAYMENT_TYPE = PaymentType.ONLINE;
    public static final DeliveryStatus DEFAULT_DELIVERY_STATUS = DeliveryStatus.PROCESSING;
    public static final boolean DEFAULT_ENABLED_STATUS = true;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Category createCategory() {
        return Category.builder()
                .id(DEFAULT_ID)
                .title(DEFAULT_CATEGORY_NAME)
                .build();
    }

    public static Category createCategory(String title) {
        return createCategory().toBuilder().title(title).build();
    }

    public static PickupPoint createPickupPoint() {
        return PickupPoint.builder()
                .id(DEFAULT_ID)
                .address(DEFAULT_ADDRESS)
                .openingTime(DEFAULT_MIN_LOCALTIME)
                .closingTime(DEFAULT_MAX_LOCALTIME)
                .orders(new ArrayList<>())
                .build();
    }

    public static Product createProduct() {
        Set<Category> categories = new HashSet<>();
        categories.add(createCategory());

        return Product.builder()
                .id(DEFAULT_ID)
                .title(DEFAULT_PRODUCT_TITLE)
                .price(DEFAULT_PRICE)
                .description(DEFAULT_PRODUCT_DESCRIPTION)
                .imageUrl(DEFAULT_IMAGE_URL)
                .categories(categories)
                .build();
    }


    public static Product createProductWithPrice(BigDecimal price) {
        return createProduct().toBuilder().price(price).build();
    }

    public static User createUser() {
        User user = User.builder()
                .id(DEFAULT_ID)
                .email(DEFAULT_EMAIL)
                .name(DEFAULT_USER_NAME)
                .password(DEFAULT_PASSWORD)
                .role(DEFAULT_USER_ROLE)
                .enabled(DEFAULT_ENABLED_STATUS)
                .build();

        Bucket bucket = Bucket.builder()
                .id(DEFAULT_ID)
                .user(user)
                .items(new ArrayList<>())
                .build();

        user.setBucket(bucket);
        return user;
    }

    public static User createUser(Consumer<User.UserBuilder> customizer) {
        User user = createUser();
        User.UserBuilder builder = user.toBuilder();
        customizer.accept(builder);
        User result = builder.build();
        if (result.getBucket() != null) {
            result.getBucket().setUser(result);
        }
        return result;
    }

    public static User createAdmin() {
        return createUser().toBuilder()
                .role(UserRole.ADMIN)
                .email("admin@test.com")
                .name("Admin User")
                .build();
    }

    public static User createUnverifiedUser() {
        return createUser().toBuilder()
                .enabled(false)
                .email("unverified@test.com")
                .build();
    }


    public static Bucket createBucket() {
        return createUser().getBucket();
    }

    public static Bucket createBucketWithItems(int count) {
        User user = createUser();
        Bucket bucket = user.getBucket();
        List<BucketItem> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Product product = createProduct().toBuilder()
                    .id((long) (i + 1))
                    .title(DEFAULT_PRODUCT_TITLE + "_" + (i + 1))
                    .build();

            BucketItem item = BucketItem.builder()
                    .id((long) (i + 1))
                    .bucket(bucket)
                    .product(product)
                    .amount(DEFAULT_AMOUNT)
                    .build();

            items.add(item);
        }

        bucket.setItems(items);
        return bucket;
    }

    public static BucketItem createBucketItem() {
        Bucket bucket = createBucket();
        Product product = createProduct();
        BucketItem item = BucketItem.builder()
                .id(DEFAULT_ID)
                .bucket(bucket)
                .product(product)
                .amount(DEFAULT_AMOUNT)
                .build();
        bucket.getItems().add(item);
        return item;
    }

    public static Order createOrder() {
        User user = createUser();
        PickupPoint pickupPoint = createPickupPoint();
        Product product = createProduct();

        Order order = Order.builder()
                .id(DEFAULT_ID)
                .user(user)
                .pickupPoint(pickupPoint)
                .createdTime(DEFAULT_MIN_LOCALDATETIME)
                .updatedTime(DEFAULT_MAX_LOCALDATETIME)
                .totalPrice(DEFAULT_PRICE)
                .totalAmount(DEFAULT_AMOUNT)
                .deliveryStatus(DEFAULT_DELIVERY_STATUS)
                .paymentStatus(DEFAULT_PAYMENT_STATUS)
                .orderDetails(new ArrayList<>())
                .build();

        OrderDetails details = OrderDetails.builder()
                .id(DEFAULT_ID)
                .order(order)
                .product(product)
                .amount(DEFAULT_AMOUNT)
                .totalPrice(DEFAULT_PRICE)
                .createdTime(DEFAULT_MIN_LOCALDATETIME)
                .deliveryStatus(DEFAULT_DELIVERY_STATUS)
                .paymentType(DEFAULT_PAYMENT_TYPE)
                .paymentStatus(DEFAULT_PAYMENT_STATUS)
                .build();

        order.getOrderDetails().add(details);
        return order;
    }

    public static OrderDetails createOrderDetails() {
        return createOrder().getOrderDetails().get(0);
    }

    public static UserDTO createUserDTO() {
        return UserDTO.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_USER_NAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .confirmPassword(DEFAULT_PASSWORD)
                .role(DEFAULT_USER_ROLE_STRING)
                .bucketId(DEFAULT_ID)
                .enabled(DEFAULT_ENABLED_STATUS)
                .build();
    }

    public static UserDTO createUserDTO(Consumer<UserDTO.UserDTOBuilder> customizer) {
        UserDTO.UserDTOBuilder builder = createUserDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static UserUpdateDTO createUserUpdateDTO() {
        return UserUpdateDTO.builder()
                .newName("UpdatedName")
                .email(DEFAULT_EMAIL)
                .newEmail("new_" + DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .newPassword("newPassword123")
                .newConfirmPassword("newPassword123")
                .build();
    }

    public static UserUpdateDTO createUserUpdateDTO(Consumer<UserUpdateDTO.UserUpdateDTOBuilder> customizer) {
        UserUpdateDTO.UserUpdateDTOBuilder builder = createUserUpdateDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static CategoryDTO createCategoryDTO() {
        return CategoryDTO.builder()
                .id(DEFAULT_ID)
                .title(DEFAULT_CATEGORY_NAME)
                .build();
    }

    public static CategoryDTO createCategoryDTO(String title) {
        return CategoryDTO.builder()
                .id(DEFAULT_ID)
                .title(title)
                .build();
    }

    public static SmallProductDTO createSmallProductDTO() {
        return SmallProductDTO.builder()
                .id(DEFAULT_ID)
                .title(DEFAULT_PRODUCT_TITLE)
                .price(DEFAULT_PRICE)
                .imageUrl(DEFAULT_IMAGE_URL)
                .build();
    }

    public static SmallProductDTO createSmallProductDTO(Consumer<SmallProductDTO.SmallProductDTOBuilder> customizer) {
        SmallProductDTO.SmallProductDTOBuilder builder = createSmallProductDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static ProductDTO createProductDTO() {
        return ProductDTO.builder()
                .id(DEFAULT_ID)
                .title(DEFAULT_PRODUCT_TITLE)
                .description(DEFAULT_PRODUCT_DESCRIPTION)
                .price(DEFAULT_PRICE)
                .categories(Set.of(createCategoryDTO()))
                .imageUrl(DEFAULT_IMAGE_URL)
                .build();
    }

    public static ProductDTO createProductDTO(Consumer<ProductDTO.ProductDTOBuilder> customizer) {
        ProductDTO.ProductDTOBuilder builder = createProductDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static BucketItemDTO createBucketItemDTO() {
        return BucketItemDTO.builder()
                .id(DEFAULT_ID)
                .bucketId(DEFAULT_ID)
                .smallProductDTO(createSmallProductDTO())
                .amount(DEFAULT_AMOUNT)
                .totalPrice(DEFAULT_PRICE.multiply(DEFAULT_AMOUNT))
                .build();
    }

    public static BucketItemDTO createBucketItemDTO(Consumer<BucketItemDTO.BucketItemDTOBuilder> customizer) {
        BucketItemDTO.BucketItemDTOBuilder builder = createBucketItemDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static BucketDTO createBucketDTO() {
        return createBucketDTO(1);
    }

    public static BucketDTO createBucketDTO(int itemsCount) {
        List<BucketItemDTO> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (int i = 1; i <= itemsCount; i++) {
            long id = (long) i;
            SmallProductDTO product = SmallProductDTO.builder()
                    .id(id)
                    .title("Product " + i)
                    .price(DEFAULT_PRICE)
                    .imageUrl(DEFAULT_IMAGE_URL)
                    .build();

            BucketItemDTO item = BucketItemDTO.builder()
                    .id(id)
                    .bucketId(DEFAULT_ID)
                    .smallProductDTO(product)
                    .amount(DEFAULT_AMOUNT)
                    .totalPrice(DEFAULT_PRICE.multiply(DEFAULT_AMOUNT))
                    .build();

            items.add(item);
            totalAmount = totalAmount.add(item.getAmount());
            totalPrice = totalPrice.add(item.getTotalPrice());
        }

        return BucketDTO.builder()
                .id(DEFAULT_ID)
                .userEmail(DEFAULT_EMAIL)
                .items(items)
                .totalItemsAmount(totalAmount)
                .totalPrice(totalPrice)
                .build();
    }

    public static BucketDTO createBucketDTO(Consumer<BucketDTO.BucketDTOBuilder> customizer) {
        BucketDTO.BucketDTOBuilder builder = createBucketDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static ModifyBucketItemDTO createModifyBucketItemDTO() {
        return ModifyBucketItemDTO.builder()
                .userEmail(DEFAULT_EMAIL)
                .productId(DEFAULT_ID)
                .amount(DEFAULT_AMOUNT)
                .build();
    }

    public static ModifyBucketItemDTO createModifyBucketItemDTO(Consumer<ModifyBucketItemDTO.ModifyBucketItemDTOBuilder> customizer) {
        ModifyBucketItemDTO.ModifyBucketItemDTOBuilder builder = createModifyBucketItemDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static OrderDetailsDTO createOrderDetailsDTO() {
        return OrderDetailsDTO.builder()
                .id(DEFAULT_ID)
                .orderId(DEFAULT_ID)
                .productId(DEFAULT_ID)
                .amount(DEFAULT_AMOUNT)
                .price(DEFAULT_PRICE)
                .totalPrice(DEFAULT_PRICE.multiply(DEFAULT_AMOUNT))
                .deliveryStatus(DEFAULT_DELIVERY_STATUS.name())
                .paymentType(DEFAULT_PAYMENT_TYPE.name())
                .paymentStatus(DEFAULT_PAYMENT_STATUS.name())
                .estimatedDeliveryDate(DEFAULT_MIN_LOCALDATETIME.plusDays(3).format(formatter))
                .build();
    }

    public static OrderDetailsDTO createOrderDetailsDTO(Consumer<OrderDetailsDTO.OrderDetailsDTOBuilder> customizer) {
        OrderDetailsDTO.OrderDetailsDTOBuilder builder = createOrderDetailsDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static OrderDTO createOrderDTO() {
        OrderDetailsDTO detailsDTO = createOrderDetailsDTO();
        return OrderDTO.builder()
                .id(DEFAULT_ID)
                .address(DEFAULT_ADDRESS)
                .pickupPointId(DEFAULT_ID)
                .details(List.of(detailsDTO))
                .totalSum(detailsDTO.getTotalPrice())
                .deliveryStatus(DEFAULT_DELIVERY_STATUS.name())
                .paymentStatus(DEFAULT_PAYMENT_STATUS.name())
                .createdTime(DEFAULT_MIN_LOCALDATETIME)
                .updatedTime(DEFAULT_MAX_LOCALDATETIME)
                .build();
    }

    public static OrderDTO createOrderDTO(Consumer<OrderDTO.OrderDTOBuilder> customizer) {
        OrderDTO.OrderDTOBuilder builder = createOrderDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static CreatorNewOrderDetailsDTO createCreatorNewOrderDetailsDTO() {
        return CreatorNewOrderDetailsDTO.builder()
                .productId(DEFAULT_ID)
                .priceOnOrder(DEFAULT_PRICE)
                .amount(DEFAULT_AMOUNT)
                .build();
    }

    public static CreatorNewOrderDTO createCreatorNewOrderDTO() {
        return CreatorNewOrderDTO.builder()
                .userEmail(DEFAULT_EMAIL)
                .addressId(DEFAULT_ID)
                .paymentType(DEFAULT_PAYMENT_TYPE.name())
                .orderDetails(List.of(createCreatorNewOrderDetailsDTO()))
                .build();
    }

    public static CreatorNewOrderDTO createCreatorNewOrderDTO(Consumer<CreatorNewOrderDTO.CreatorNewOrderDTOBuilder> customizer) {
        CreatorNewOrderDTO.CreatorNewOrderDTOBuilder builder = createCreatorNewOrderDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static ActiveOrdersDTO createActiveOrdersDTO() {
        return ActiveOrdersDTO.builder()
                .orders(List.of(createOrderDTO()))
                .build();
    }

    public static PickupPointDTO createPickupPointDTO() {
        return PickupPointDTO.builder()
                .id(DEFAULT_ID)
                .address(DEFAULT_ADDRESS)
                .openingTime(DEFAULT_MIN_LOCALTIME)
                .closingTime(DEFAULT_MAX_LOCALTIME)
                .build();
    }

    public static PickupPointDTO createPickupPointDTO(Consumer<PickupPointDTO.PickupPointDTOBuilder> customizer) {
        PickupPointDTO.PickupPointDTOBuilder builder = createPickupPointDTO().toBuilder();
        customizer.accept(builder);
        return builder.build();
    }
}
