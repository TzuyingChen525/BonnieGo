// 初始化購物車資料，確保變數 cart 總是存在，且為陣列
let cart = [];
fetchCartItems();

// 獲取購物車資料
async function fetchCartItems() {

   try {
      const response = await fetch("http://localhost:8080/api/cart", {
         method: "GET",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         }
      });

      if (!response.ok) {
         const errorMessage = await response.text();
         if (response.status === 404) {
            console.warn("購物車不存在或未登入。");
         } else {
            alert("發生錯誤：" + errorMessage);
         }
         throw new Error(errorMessage);
      }

      // 從後端獲取購物車資料
      const cartData = await response.json();
      console.log("購物車資料:", cartData);
      cart = cartData;  // 更新購物車資料
      renderCart();

   } catch (error) {
      console.error("獲取購物車失敗:", error);
      cart = []; // 若發生錯誤，清空購物車
      renderCart();
   }
}

// 添加商品到購物車
async function addToCart(product) {
   try {
      const response = await fetch("http://localhost:8080/api/cart/add", {
         method: "POST",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify({
            product_id: product.product_id,
            quantity: 1
         })
      });
      if (!response.ok) {
         throw new Error(await response.text());
      }
      await fetchCartItems(); // 重新拉取購物車資料
      // alert("商品已成功加入購物車");
   } catch (error) {
      console.error("無法添加商品:", error);
      alert("無法添加商品: " + error.message);
   }
}

// 更新購物車商品數量
async function updateItemQuantity(index, newValue) {
   const newQuantity = parseInt(newValue, 10);
   if (isNaN(newQuantity) || newQuantity < 1) {
      alert("請輸入有效的商品數量！");
      renderCart(); // 恢復原來的數量
      return;
   }

   try {
      const response = await fetch("http://localhost:8080/api/cart/update", {
         method: "PUT",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify({
            product_id: cart[index].product.product_id,
            quantity: newQuantity
         })
      });
      if (!response.ok) {
         throw new Error(await response.text());
      }
      await fetchCartItems(); // 重新拉取購物車資料
   } catch (error) {
      console.error("更新購物車失敗:", error);
      alert("更新購物車失敗: " + error.message);
   }
}

// 移入收藏清單
async function moveToFavorites(index) {
   const item = cart[index];
   if (!item || !item.product) {
      console.error("商品資料不完整，無法加入收藏");
      return;
   }

   try {
      // 呼叫後端 API，將商品加入收藏
      await addToFavorites(item.product);
      // 從購物車中移除該商品
      await removeFromCart(item.product.product_id);

   } catch (error) {
      console.error("移入收藏清單失敗:", error);
      alert("移入收藏清單失敗：" + error.message);
   }
}


// 刪除購物車商品
async function removeFromCart(productId) {
   try {
      const response = await fetch("http://localhost:8080/api/cart/delete", {
         method: "DELETE",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify({ product_id: productId })
      });
      if (!response.ok) {
         throw new Error(await response.text());
      }
      await fetchCartItems(); // 重新拉取購物車資料
   } catch (error) {
      console.error("移除商品失敗:", error);
      alert("移除商品失敗: " + error.message);
   }
}

// 渲染購物車
function renderCart() {
   const cartList = document.getElementById("cart-items");
   cartList.innerHTML = ""; // 清空購物車
   let totalAmount = 0;

   if (!cart || cart.length === 0) {
      cartList.innerHTML = '<p style="text-align: center; margin-top: 20px;">購物車是空的。</p>';
      document.getElementById("cart-total").textContent = "$0";
      updateCartCount();
      return;
   } else {
      cart.forEach((item, index) => {

         if (!item.product) {
            console.error(`商品資料缺失:`, item);
            return;
         }
         if (!item.quantity) {
            console.error(`數量無效:`, item);
            return;
         }

         const product = item.product; // 後端返回的商品詳細資訊
         totalAmount += product.price * item.quantity;

         cartList.innerHTML += `
            <tr>
               <td><img src="${product.imageUrl}" alt="${product.name}" width="50"></td>
               <td>${product.name}</td>
               <td>
                  <input 
                     type="number" 
                     value="${item.quantity}" 
                     min="1" 
                     max="${product.stock || 1}" 
                     onchange="updateItemQuantity(${index}, this.value)"
                     style="width: 50px; text-align: center;">
               </td>
               <td>$${product.price}</td>
               <td>$${product.price * item.quantity}</td>
               <td><button class="btn" onclick="moveToFavorites(${index})"><svg xmlns="http://www.w3.org/2000/svg" height="20px"
                        viewBox="0 0 24 24" width="20px" fill="#5f6368">
                        <path d="M0 0h24v24H0z" fill="none" />
                        <path
                           d="M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z" />
                     </svg></button></td>
               <td><button onclick="removeFromCart(${product.product_id})" class="btn"><svg xmlns="http://www.w3.org/2000/svg" height="20px"
                        viewBox="0 -960 960 960" width="20px" fill="#5f6368">
                        <path
                           d="M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z" />
                     </svg></button></td>
            </tr>`;
      });
   }

   // 更新總金額
   const total = cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
   document.getElementById("cart-total").textContent = `$${total}`;

   // 更新購物車數量顯示
   updateCartCount();
}

// 更新購物車數量顯示
function updateCartCount() {
   const cartCountElement = document.getElementById("shoppingCartCount");
   const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0);
   cartCountElement.textContent = totalCount;
}

// 初始化購物車
window.onload = async function () {
   await fetchCartItems(); // 等待購物車資料加載完成，獲取後端購物車資料
};
