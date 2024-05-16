package com.carbonara.core.order

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface OrderRepository: ReactiveMongoRepository<OrderDao, ObjectId>
