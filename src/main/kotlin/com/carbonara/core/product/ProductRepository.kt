package com.carbonara.core.product

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProductRepository:
        ReactiveMongoRepository<ProductDto, ObjectId>