package org.dirnea.lab03.controller;

import org.dirnea.lab03.model.Order;
import org.dirnea.lab03.model.Status;
import org.dirnea.lab03.repository.OrderRepository;
import org.dirnea.lab03.util.OrderNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    List<Order> all() {
        List<Order> orders = orderRepository.findAll();
        for (final Order order : orders) {
            Link link = linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel();
            order.add(link);
        }
        return orders;
    }


    @GetMapping("/orders/{id}")
    Order one(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.add(linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel());
        order.add(linkTo(methodOn(OrderController.class).all()).withRel("todos"));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
            order.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        }
        return order;
    }

    @PostMapping("/orders")
    Order newOrder(@RequestBody Order order) {

        order.setStatus(Status.IN_PROGRESS);
        Order new_Order = orderRepository.save(order);

        return new_Order.add(linkTo(methodOn(OrderController.class).one(new_Order.getId())).withSelfRel());
    }

    @DeleteMapping("/orders/{id}/cancel")
    Order cancel(@PathVariable Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            orderRepository.save(order);

        }
        return order;
    }

    @PutMapping("/orders/{id}/complete")
    Order complete(@PathVariable Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            orderRepository.save(order);
        }

        return order;
    }
}