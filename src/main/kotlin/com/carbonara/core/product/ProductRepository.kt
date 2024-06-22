package com.carbonara.core.product

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductRepository: ReactiveMongoRepository<ProductDao, ObjectId> {

        fun findFirstByIsActiveIsTrue() : Mono<ProductDao>

        fun findAllByIsActiveIsTrue() : Flux<ProductDao>
}
