package com.shop.backend.services;


import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.User;

import java.util.List;

public interface CartService {



    List<CartDTO> getCartItems(Long userId);

}
