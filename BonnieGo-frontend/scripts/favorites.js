// 收藏商品的陣列
let favoriteItems = [];
fetchFavoriteItems();

// 獲取收藏清單資料
async function fetchFavoriteItems() {
   try {
      const response = await fetch("http://localhost:8080/api/favorites", {
         method: "GET",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         }
      });

      if (!response.ok) {
         const errorMessage = await response.text();
         if (response.status === 404) {
            console.warn("收藏清單不存在或未登入");
         } else {
            console.log("發生錯誤：" + errorMessage);
         }
         throw new Error(errorMessage);
      }

      favoriteItems = await response.json();
      console.log("收藏清單資料:", favoriteItems);
      renderFavorites();
   } catch (error) {
      console.error("獲取收藏清單失敗:", error);
      favoriteItems = []; // 若發生錯誤，清空收藏清單
      renderFavorites();
   }
}

// 加入收藏功能
async function addToFavorites(product) {
   if (!product.product_id) {
      console.error("商品缺少 id，無法加入收藏！");
      return;
   }

   try {
      const response = await fetch("http://localhost:8080/api/favorites/add", {
         method: "POST",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify({ productId: product.product_id })
      });

      if (!response.ok) {
         const errorMessage = await response.text();
         throw new Error(errorMessage);
      }

      // 從後端獲取最新的收藏清單
      await fetchFavoriteItems();
      alert("商品已成功加入收藏清單！");
   } catch (error) {
      console.error("加入收藏失敗:", error);
      alert("商品已於收藏清單內！");
   }
}

// 移除收藏功能
async function removeFromFavorites(productId) {

   if (!productId) {
      console.error("移除收藏失敗：productId 為空！");
      alert("移除收藏失敗，無效的商品 ID！");
      return; // 提前退出，避免發送無效請求
   }

   try {
      const response = await fetch("http://localhost:8080/api/favorites/delete", {
         method: "DELETE",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify({ productId })
      });

      if (!response.ok) {
         throw new Error(await response.text());
      }

      await fetchFavoriteItems(); // 重新拉取收藏清單資料
      // alert("商品已從收藏清單移除！");
   } catch (error) {
      console.error("移除收藏失敗:", error);
      alert("移除收藏失敗：" + error.message);
   }
}

// 渲染收藏商品於 Sidebar
function renderFavorites() {
   const sidebarContainer = document.querySelector("#favoriteItemSidebar tbody");
   sidebarContainer.innerHTML = ""; // 清空現有內容

   if (favoriteItems.length === 0) {
      sidebarContainer.innerHTML = '<p style="text-align: center; margin-top: 20px;">收藏清單是空的。</p>';
   } else {
      favoriteItems.forEach((item, index) => {
         const row = `
            <tr data-id="${item.product.product_id}">
               <td><img src="${item.product.imageUrl}" alt="商品圖片" style="width: 50px; height: 50px;"></td>
               <td>${item.product.name}</td>
               <td>$${item.product.price}</td>
               <td>
                  <button onclick="removeFromFavorites(${item.product.product_id})" class="btn remove-btn">
                     <svg xmlns="http://www.w3.org/2000/svg" height="20px" viewBox="0 -960 960 960" width="20px" fill="#5f6368">
                        <path d="M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z" />
                     </svg>
                  </button>
               </td>
               <td>
                  <button onclick="moveToCart(${index})" class="btn cart-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" height="20px"
                           viewBox="0 0 24 24" width="20px" fill="#5f6368">
                           <path d="M0 0h24v24H0zm18.31 6l-2.76 5z" fill="none" />
                           <path
                           d="M11 9h2V6h3V4h-3V1h-2v3H8v2h3v3zm-4 9c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2zm-9.83-3.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.86-7.01L19.42 4h-.01l-1.1 2-2.76 5H8.53l-.13-.27L6.16 6l-.95-2-.94-2H1v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.13 0-.25-.11-.25-.25z" />
                  </svg>
                  </button>
               </td>
            </tr>
         `;
         sidebarContainer.insertAdjacentHTML("beforeend", row);
      });
   }
}

// 移入購物車功能
async function moveToCart(index) {
   const item = favoriteItems[index];
   if (!item || !item.product) {
      console.error("商品資料不完整，無法移入購物車");
      return;
   }

   try {
      // 呼叫後端 API 添加至購物車
      await addToCart(item.product);

      // 從收藏清單中移除該商品
      await removeFromFavorites(item.product.product_id);
      // alert("商品已成功移入購物車！");
   } catch (error) {
      console.error("移入購物車失敗:", error);
      alert("移入購物車失敗：" + error.message);
   }
}

// 初次加載時獲取收藏清單
window.onload = async function () {
   await fetchFavoriteItems();
};
