package com.shop.backend.controller;

import com.shop.backend.dto.ProductOptionDTO;
import com.shop.backend.dto.WishListDTO;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.WishList;
import com.shop.backend.repository.ProductOptionRepository;
import com.shop.backend.services.ProductOptionService;
import com.shop.backend.services.ProductService;
import com.shop.backend.services.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/wishlist")
public class WishListController {
    @Autowired
    private WishListService wishListService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductOptionService productOptionService;


    @PostMapping
    public ResponseEntity<WishListDTO> createWishList(@RequestBody WishListDTO wishListDTO,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        WishList createdWishList = wishListService.createWishList(wishListDTO, username);
        WishListDTO responseDTO = convertToDTO(createdWishList);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<WishListDTO>> getWishListByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<WishList> wishLists = wishListService.getWishListByUser(username);
        List<WishListDTO> wishListDTOs = wishLists.stream()
                .map(wishList -> {
                    Product product = wishList.getProduct();
                    WishListDTO dto = convertToDTO(wishList);

                    // 옵션이 없는 경우 기본값 설정
                    if (wishList.getProductOption() == null) {
                        dto.setOption("옵션을 선택해주세요.");
                    }

                    dto.setProductImage(getProductImageUrl(product));
                    dto.setPrice(product.getPrice());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(wishListDTOs);
    }


    @DeleteMapping("/{wishListId}")
    public ResponseEntity<?> deleteWishList(@PathVariable Long wishListId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        wishListService.deleteWishList(wishListId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteWishListByUserAndProduct(@PathVariable Long productId,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        wishListService.deleteWishListByUserAndProduct(productId, username);
        return ResponseEntity.ok().build();
    }

    // 찜 여부 확인
    @GetMapping("/product/{productId}")
    public ResponseEntity<Boolean> checkWishlistStatus(@PathVariable Long productId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean isWishlisted = wishListService.isProductInWishlist(productId, username);
        return ResponseEntity.ok(isWishlisted);
    }

    //옵션 정보 가져오기
    @GetMapping("/options")
    public ResponseEntity<List<ProductOptionDTO>> getAllOptions() {
        List<ProductOptionDTO> options = productOptionService.getAllOptions();
        return ResponseEntity.ok(options);
    }

    private WishListDTO convertToDTO(WishList wishList) {
        WishListDTO wishListDTO = new WishListDTO();
        wishListDTO.setWishListId(wishList.getWishListId());
        wishListDTO.setUserId(wishList.getUser().getUserId());
        wishListDTO.setProductId(wishList.getProduct().getProductId());
        wishListDTO.setProductName(wishList.getProduct().getName());

        // 상품 옵션 정보 추가
        if (wishList.getProductOption() != null) {
            wishListDTO.setOptionId(wishList.getProductOption().getOptionId());
            wishListDTO.setOption(getProductOption(wishList.getProduct(), wishList.getProductOption().getOptionId()));
        }

        return wishListDTO;
    }

    // 상품 이미지 URL을 반환하는 메서드
    private String getProductImageUrl(Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getImages().get(0).getImageUrl(); // 첫 번째 이미지의 URL 반환
        }
        return null; // 이미지가 없을 경우 null 반환
    }

    // 상품 옵션을 반환하는 메서드
    private String getProductOption(Product product, Long optionId) {
        if (product.getOptions() != null && !product.getOptions().isEmpty()) {
            // 주어진 optionId와 일치하는 옵션 찾기
            ProductOption option = product.getOptions().stream()
                    .filter(opt -> opt.getOptionId().equals(optionId))
                    .findFirst()
                    .orElse(null);
            if (option != null) {
                return "Color: " + option.getColor() + ", Size: " + option.getSize();
            }
        }
        return "Color: - , Size: - "; // 옵션이 없거나 일치하는 옵션이 없을 경우
    }
}
