package com.carbonara.core.helper

import com.carbonara.core.address.Address
import com.carbonara.core.order.OrderDao
import com.carbonara.core.order.OrderStatus
import com.carbonara.core.payment.InternalPaymentStatus
import com.carbonara.core.payment.PaymentDetails
import com.carbonara.core.product.ProductDao
import org.bson.types.ObjectId

fun createOrderDao(
    auth0UserId: String = "auth0Id1",
    userName: String = "Mr Bean",
    deliveryAddress: Address = createDeliveryAddress(),
    products: List<ProductDao> = listOf(createProduct()),
    additionalDetails: String = "",
    paymentDetails: PaymentDetails = createPaymentDetails(),
    orderStatus: OrderStatus = OrderStatus.FINDING_AVAILABLE_RIDER,
    createdAt: String = "2024-06-01T14:00:00.0+02:00",
    updatedAt: String = "2024-06-01T14:00:00.0+02:00"
): OrderDao {
    return OrderDao(
        orderId = ObjectId(),
        auth0UserId = auth0UserId,
        userName = userName,
        deliveryAddress = deliveryAddress,
        products = products,
        additionalDetails = additionalDetails,
        paymentDetails = paymentDetails,
        orderStatus = orderStatus,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun createProduct(
    productId: ObjectId = ObjectId()
): ProductDao {
    return ProductDao(
        productId = productId,
        productName = "test-product-1",
        productPrice = 1000,
        productPictureUrl = "https://example.com",
        isActive = true,
        shortProductDescription = "Short description",
        longProductDescription = "Long description",
        isProductPlaceHolder = false
    )
}

fun createDeliveryAddress(): Address {
    return Address(
        name = "John Watson",
        street = "Baker Street",
        streetNumber = "221B",
        postCode = "123",
        city = "London",
        country = "Germany",
        googlePlaceId = "sample_google_place_id"
    )
}

fun createPaymentDetails(
    paymentId: String = "tr_123",
    internalPaymentStatus: InternalPaymentStatus = InternalPaymentStatus.PENDING
): PaymentDetails {
    return PaymentDetails(
        paymentId = paymentId,
        paymentRedirectLink = "https://example.com",
        internalPaymentStatus = internalPaymentStatus
    )
}
