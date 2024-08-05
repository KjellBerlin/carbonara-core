package com.carbonara.core.order

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface OrderRepository: ReactiveMongoRepository<OrderDao, ObjectId> {

    @Query("{'paymentDetails.paymentId': ?0}")
    fun findFirstByPaymentId(paymentId: String): Mono<OrderDao>

    @Query("{'auth0UserId': ?0}")
    fun findAllByAuth0UserId(auth0UserId: String): Mono<List<OrderDao>>
}
