package com.shop.backend.repository;

import com.shop.backend.models.OrderDetail;
import com.shop.backend.models.Product;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT od.product, COUNT(od) as orderCount FROM OrderDetail od GROUP BY od.product ORDER BY orderCount DESC")
    List<Object[]> findBestSellingProducts();

    @Query("SELECT od.product, COUNT(od) as orderCount FROM OrderDetail od GROUP BY od.product ORDER BY orderCount DESC")
    List<Object[]> findTopNBestSellingProducts(@Param("limit") int limit);
    @Query("SELECT od.product, COUNT(od) as orderCount FROM OrderDetail od GROUP BY od.product HAVING COUNT(od) > 0 ORDER BY orderCount DESC")
    List<Object[]> findAllBestSellingProducts();
    @Query("SELECT od.product, COUNT(od) as orderCount FROM OrderDetail od JOIN od.product p WHERE p.category.name = :categoryName GROUP BY od.product ORDER BY orderCount DESC")
    List<Object[]> findTopNBestSellingProductsByCategory(@Param("categoryName") String categoryName, @Param("limit") int limit);

    boolean existsByOrder_UserAndProduct(User user, Product product);
}
