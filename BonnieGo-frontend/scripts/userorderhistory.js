let orders = []; // 初始化全局變數，用於存儲訂單資料

async function loadOrders() {
   try {
      const response = await fetch("http://localhost:8080/api/order", {
         method: "GET",
         credentials: "include"
      });

      if (!response.ok) {
         throw new Error(await response.text());
      }

      // 將伺服器返回的訂單數據存入全局變數
      orders = await response.json();
      const orderTable = document.getElementById('orderTable');
      orderTable.innerHTML = '';

      // 生成訂單列表
      orders.forEach(order => {
         const row = document.createElement('tr');
         row.innerHTML = `
               <td>${order.id}</td>
               <td>${order.orderDate}</td>
               <td>${order.status}</td>
               <td>
                  <button class="btn btn-secondary btn-sm" onclick="showOrderDetails('${order.id}')">查看訂單詳情</button>
               </td>
         `;
         orderTable.appendChild(row);
      });
   } catch (error) {
      console.error("載入訂單失敗:", error);
   }
}

window.onload = loadOrders;


// 顯示訂單詳情
function showOrderDetails(orderId) {
   // 從全局變數 orders 中尋找匹配的訂單
   const order = orders.find(o => o.id === parseInt(orderId, 10)); // 注意 id 是數字型別
   // console.log(order);

   if (order) {
      // 顯示訂單基本資訊
      document.getElementById('orderId').textContent = `#${order.id}`;
      document.getElementById('orderDate').textContent = order.orderDate;
      document.getElementById('orderStatus').textContent = order.status;

      // 填充產品資料
      const productTable = document.getElementById('productTable');
      productTable.innerHTML = '';
      let totalAmount = 0; // 初始化總金額

      order.productItems.forEach(product => {
         const productRow = document.createElement('tr');
         productRow.innerHTML = `
         <td><img src="${product.imageUrl}" alt="${product.name}" width="50"></td>
         <td>${product.name}</td>
         <td>${product.quantity}</td>
         <td>$${product.unitPrice.toFixed(2)}</td>
         <td>$${(product.quantity * product.unitPrice).toFixed(2)}</td>
      `;
         productTable.appendChild(productRow);

         // 計算訂單總金額
         totalAmount += product.quantity * product.unitPrice;
      });

      // 更新總金額欄位
      document.getElementById('totalPrice').textContent = `$${totalAmount.toFixed(2)}`;

      // 填充配送資訊
      document.getElementById('recipient').textContent = order.recipient;
      document.getElementById('address').textContent = order.address;
      document.getElementById('phone').textContent = order.phone;

      // 顯示訂單詳情區域
      document.getElementById('orderDetailsSection').style.display = 'block';
      document.getElementById('footer').style.display = 'block';

   } else {
      // 若未找到訂單，顯示提示訊息
      console.error(`未找到訂單 ID: ${orderId}`);
      alert('未找到該訂單的詳情，請稍後再試。');
   }
}
