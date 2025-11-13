package com.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import jakarta.persistence.GenerationType;

import jakarta.persistence.*;
@Getter
@Setter
@ToString(exclude = "cart")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JsonBackReference
    private Cart cart;
    @ManyToOne
    private Product product;
    private Integer quantity;
}
