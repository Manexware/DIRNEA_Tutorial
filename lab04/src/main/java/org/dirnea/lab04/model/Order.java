package org.dirnea.lab03.model;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "CUSTOMER_ORDER")
public class Order extends RepresentationModel<Order> {

  private @Id @GeneratedValue Long id;

  private String description;
  private Status status;

  Order() {}

  public Order(String description, Status status) {

    this.description = description;
    this.status = status;
  }
}