// Admin登出
document.getElementById("logout-btn").addEventListener("click", () => {
   fetch(`http://localhost:8080/api/user/logout`, {
      method: 'POST',
      credentials: 'include' // 確保帶上 Session
   })
      .then(response => {
         if (!response.ok) {
            if (response.status === 401) {
               alert("您已登出或尚未登入");
               window.location.href = '/login.html';
            } else {
               throw new Error("登出失敗");
            }
         } else {
            alert("登出成功");
            window.location.href = '/login.html';
         }
      })
      .catch(err => alert(`登出失敗: ${err.message}`));
});

let orders = [];
let selectedOrderId = null;

// 載入訂單
async function loadOrders() {
   try {
      const response = await fetch("http://localhost:8080/api/admin/orders", {
         method: "GET",
         credentials: "include"
      });

      if (!response.ok) {
         throw new Error(await response.text());
      }

      orders = await response.json();
      console.log(orders);

      // 初始渲染訂單列表
      applyFilterAndSort();
   } catch (error) {
      console.error("載入訂單失敗:", error);
   }
}

// 顯示狀態修改模態框
function showStatusModal(orderId) {
   selectedOrderId = orderId;
   const order = orders.find(o => o.id === orderId);
   if (order) {
      document.getElementById('statusSelect').value = order.status;
      new bootstrap.Modal(document.getElementById('statusModal')).show();
   }
}

// 保存訂單狀態
async function saveStatus() {
   const status = document.getElementById('statusSelect').value;
   try {
      const response = await fetch(`http://localhost:8080/api/admin/orders/${selectedOrderId}/status`, {
         method: "PUT",
         headers: { "Content-Type": "application/json" },
         credentials: "include",
         body: JSON.stringify({ status })
      });

      if (!response.ok) {
         throw new Error(await response.text());
      }

      alert("訂單狀態更新成功");
      // 關閉模態框
      const modalElement = document.getElementById('statusModal');
      const modalInstance = bootstrap.Modal.getInstance(modalElement);
      modalInstance.hide();

      // 重新加載訂單
      loadOrders();
   } catch (error) {
      console.error("更新訂單狀態失敗:", error);
   }
}

// 篩選與排序訂單
function applyFilterAndSort() {
   const filterStatus = document.getElementById('filterStatus').value;
   const sortOrders = document.getElementById('sortOrders').value;

   // 篩選訂單
   let filteredOrders = orders;
   if (filterStatus) {
      filteredOrders = orders.filter(order => order.status === filterStatus);
   }

   // 排序訂單
   if (sortOrders === 'orderDateAsc') {
      filteredOrders.sort((a, b) => new Date(a.orderDate) - new Date(b.orderDate));
   } else if (sortOrders === 'orderDateDesc') {
      filteredOrders.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));
   }

   // 渲染篩選與排序後的訂單列表
   renderOrders(filteredOrders);
}

// 渲染訂單表格
function renderOrders(filteredOrders) {
   const adminOrderTable = document.getElementById('adminOrderTable');
   adminOrderTable.innerHTML = '';

   filteredOrders.forEach(order => {
      const row = document.createElement('tr');
      row.innerHTML = `
         <td>${order.id}</td>
         <td>${order.user.username}</td>
         <td>${order.orderDate}</td>
         <td>${order.status}</td>
         <td>
            <button class="btn btn-secondary btn-sm" onclick="showStatusModal(${order.id})">修改狀態</button>
         </td>
      `;
      adminOrderTable.appendChild(row);
   });
}

// 監聽篩選與排序選單的變更
document.getElementById('filterStatus').addEventListener('change', applyFilterAndSort);
document.getElementById('sortOrders').addEventListener('change', applyFilterAndSort);
document.getElementById('saveStatusBtn').addEventListener('click', saveStatus);

// 網頁載入時執行
window.onload = loadOrders;
