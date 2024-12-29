// 加載購物車資料並渲染商品列表
window.onload = async function () {
   const cartTable = document.getElementById('productTable');
   let totalAmount = 0;

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
         console.error("獲取購物車資料失敗:", errorMessage);
         cartTable.innerHTML = '<tr><td colspan="5" style="text-align: center;">購物車載入失敗</td></tr>';
         document.getElementById('totalAmount').textContent = "$0";
         return;
      }

      const cart = await response.json();
      if (cart.length > 0) {
         cart.forEach(item => {
            totalAmount += item.product.price * item.quantity;  // 計算總金額
            const row = document.createElement('tr');

            row.innerHTML = `
               <td><img src="${item.product.imageUrl}" alt="${item.product.name}" style="height: 100px;width: 100px;"></td>
               <td>${item.product.name}</td>
               <td><input type="number" value="${item.quantity}" min="1" style="width: 50px;"></td>
               <td>$${item.product.price}</td>
               <td>$${item.product.price * item.quantity}</td>
            `;
            cartTable.appendChild(row);
         });

         // 更新總金額
         document.getElementById('totalAmount').textContent = `$${totalAmount}`;
      } else {
         // 如果購物車是空的，顯示提示
         cartTable.innerHTML = '<tr><td colspan="5" style="text-align: center;">購物車為空</td></tr>';
         document.getElementById('totalAmount').textContent = "$0";
      }

   } catch (error) {
      console.error("獲取購物車資料失敗:", error);
      cartTable.innerHTML = '<tr><td colspan="5" style="text-align: center;">購物車載入失敗</td></tr>';
      document.getElementById('totalAmount').textContent = "$0";
   }
};

// 綁定結帳按鈕事件
document.getElementById('pay-btn').addEventListener('click', async () => {
   const recipient = document.getElementById('recipient').value.trim();
   const address = document.getElementById('address').value.trim();
   const phone = document.getElementById('phone').value.trim();

   if (!recipient || !address || !phone) {
      alert("請完整填寫收件人資訊");
      return;
   }

   try {
      const response = await fetch("http://localhost:8080/api/cart", {
         method: "GET",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         }
      });

      if (!response.ok) {
         throw new Error(await response.text());
      }

      const cart = await response.json();
      if (cart.length === 0) {
         alert("購物車為空，無法結帳");
         return;
      }

      // 構造訂單商品數據
      const orderItem = cart.map(item => ({
         productId: item.product.product_id,
         quantity: item.quantity,
         subtotal: item.product.price * item.quantity,
      }));

      const orderData = {
         recipient,
         address,
         phone,
         orderItem,
      };

      const orderResponse = await fetch("http://localhost:8080/api/order", {
         method: "POST",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify(orderData),
      });

      if (!orderResponse.ok) {
         throw new Error(await orderResponse.text());
      }

      alert("訂單提交成功");

      // 清空購物車資料庫中的商品
      await fetch("http://localhost:8080/api/cart/clear", {
         method: "DELETE",
         credentials: "include",
         headers: {
            "Content-Type": "application/json"
         }
      });

      window.location.href = "./userorderhistory.html"; // 跳轉到訂單歷史頁

   } catch (error) {
      console.error("提交訂單失敗:", error);
      alert("訂單提交失敗：" + error.message);
   }
});




