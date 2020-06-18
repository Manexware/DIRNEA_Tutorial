package org.dirnea.lab03.repository;

import org.dirnea.lab03.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}