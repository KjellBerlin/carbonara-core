package com.carbonara.core.product

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductRepository:
        ReactiveMongoRepository<ProductDto, ObjectId> {

        fun findFirstByIsActiveIsTrue() : Mono<ProductDto>

        fun findAllByIsActiveIsTrue() : Flux<ProductDto>
}