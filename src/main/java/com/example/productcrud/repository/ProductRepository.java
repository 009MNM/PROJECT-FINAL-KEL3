package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByOwner(User owner);

    Optional<Product> findByIdAndOwner(Long id, User owner);

    @Query(value = "SELECT * FROM products p WHERE " +
            "p.owner_id = :ownerId AND " +
            "(:keyword IS NULL OR LOWER(p.name::text) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category_id = :categoryId)",
            countQuery = "SELECT COUNT(*) FROM products p WHERE " +
                    "p.owner_id = :ownerId AND " +
                    "(:keyword IS NULL OR LOWER(p.name::text) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
                    "(:categoryId IS NULL OR p.category_id = :categoryId)",
            nativeQuery = true)
    Page<Product> searchAndFilter(@Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  @Param("ownerId") Long ownerId,
                                  Pageable pageable);
}