package com.example.ecommerce.repository;

import com.example.ecommerce.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    WishlistItem findByWishlistIdAndProductId(Long id, Long productId);

    List<WishlistItem> findByWishlistId(Long id);
}
