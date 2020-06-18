package org.dirnea.lab03.model;

import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Orden_zarpa")
@Data
public class Order extends RepresentationModel<Order> {

    private @Id @GeneratedValue Long id;
    private String description;
    private Status status;

    public Order(){}

    public Order(String description, Status status) {
        this.description = description;
        this.status = status;
    }

    public Order(Link initialLink, String description, Status status) {
        super(initialLink);
        this.description = description;
        this.status = status;
    }

    public Order(Iterable<Link> initialLinks, String description, Status status) {
        super(initialLinks);
        this.description = description;
        this.status = status;
    }
}
