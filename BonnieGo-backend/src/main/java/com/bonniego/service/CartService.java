package com.bonniego.service;

import com.bonniego.exception.CartNotFoundException;
import com.bonniego.exception.ProductNotFoundException;
import com.bonniego.model.dto.UserCert;
import com.bonniego.model.entity.Cart;
import com.bonniego.model.entity.CartItem;
import com.bonniego.model.entity.Product;
import com.bonniego.repository.CartItemRepository;
import com.bonniego.repository.CartRepository;
import com.bonniego.repository.ProductRepository;
import com.bonniego.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    // 獲取用戶的購物車
    private Cart getUserCart(HttpSession session) {
        // 從 Session 中取得憑證
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        System.out.println("查找單一會員，從 Session 獲取到的憑證: " + userCert);
        if (userCert == null) {
            System.err.println("錯誤: Session 中沒有找到 userCert！Session ID: " + session.getId() + " userCert:" + userCert);
            throw new CartNotFoundException("發生錯誤: 會員未登入");
        }
        return cartRepository.findByUserId(userCert.getId())
                .orElseGet(() -> createNewCart(userCert));
    }

    // 創建購物車
    private Cart createNewCart(UserCert userCert) {
        Cart newCart = new Cart();
        newCart.setUserId(userCert.getId());
        cartRepository.save(newCart);
        return newCart;
    }

    // 獲取列出購物車商品
    public List<CartItem> getCartItems(HttpSession session) {
        Cart cart = getUserCart(session);
        System.out.println(cart.getId());
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        System.out.println(items);
        return items;
    }

    // 商品加入購物車
    public void addToCart(HttpSession session, Long productId, int quantity) {
        Cart cart = getUserCart(session);
        // 查找商品
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        // 檢查購物車中是否已有該商品
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProduct(cart.getId(), product);
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    //更新購物車商品數量
    public void updateCart(HttpSession session, Long productId, int quantity) {
        Cart cart = getUserCart(session);

        // 檢查商品是否存在於購物車中
        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProduct(
                cart.getId(),
                productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("商品不存在"))
        );

        if (cartItemOptional.isEmpty()) {
            throw new RuntimeException("購物車中未找到該商品");
        }
        // 更新數量
        CartItem cartItem = cartItemOptional.get();
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    //移除購物車商品
    public void removeFromCart(HttpSession session, Long productId) {
        Cart cart = getUserCart(session);

        // 檢查商品是否存在於購物車中
        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProduct(
                cart.getId(),
                productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("商品不存在"))
        );

        if (cartItemOptional.isEmpty()) {
            throw new RuntimeException("購物車中未找到該商品");
        }
        // 移除商品
        CartItem cartItem = cartItemOptional.get();
        cartItemRepository.delete(cartItem);
    }

    // 清空購物車
    public void clearCart(HttpSession session) {
        // 每個用戶的購物車是根據用戶ID存儲在資料庫中，並且session有用戶ID
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            System.err.println("錯誤: Session 中沒有找到 userCert！Session ID: " + session.getId());
            throw new CartNotFoundException("未登入用戶，無法清空購物車");
        }

        // 根據 userId 查找用戶的購物車
        Cart cart = cartRepository.findByUserId(userCert.getId())
                .orElseThrow(() -> new CartNotFoundException("未找到購物車"));

        // 查找該購物車內所有的 CartItem
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        // 如果購物車中有商品，則刪除所有商品
        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems);
        } else {
            System.out.println("該購物車已經是空的，無需清空");
        }
    }

}

