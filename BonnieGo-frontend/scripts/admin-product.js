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


const apiBaseUrl = "http://localhost:8080/api/products";
let products = [];
let editIndex = null; // 用於追蹤當前編輯的商品

// 1. 從後端獲取商品列表並渲染
async function fetchProducts() {
   try {
      const response = await fetch(apiBaseUrl);
      if (!response.ok) throw new Error("Failed to fetch products");

      const data = await response.json();
      products = data.content || []; // 確保資料正確賦值
      renderTable();
   } catch (error) {
      console.error("Error fetching products:", error);
   }
}

// 2. 渲染商品列表初始化表格
function renderTable() {
   const productTable = document.getElementById("product-table");
   productTable.innerHTML = ""; // 清空表格

   products.forEach((product, index) => {
      const row = document.createElement("tr");
      row.innerHTML = `
         <td>
            <img src="${product.imageUrl || "https://via.placeholder.com/150"}" alt="${product.name}" 
               style="width: 50px; height: 50px; object-fit: cover;">
         </td>
         <td>${product.name}</td>
         <td>${product.brand}</td>
         <td>${product.category}</td>
         <td>$${product.price}</td>
         <td>${product.stock}</td>
         <td>${product.description}</td>
         <td>
            <button class="btn btn-secondary btn-sm" onclick="editProduct(${index})">編輯</button>
            <button class="btn btn-danger btn-sm" onclick="deleteProduct(${product.product_id})">下架</button>
         </td>
      `;
      productTable.appendChild(row);
   });
}

// 3. 開啟模態框
function openModal(mode, index = null) {
   document.getElementById("product-modal").style.display = "flex";
   if (mode === "add") {
      document.getElementById("modal-title").textContent = "新增商品";
      document.getElementById("product-form").reset();
      editIndex = null;
   } else if (mode === "edit") {
      document.getElementById("modal-title").textContent = "編輯商品";
      const product = products[index];
      document.getElementById("product-name").value = product.name;
      document.getElementById("product-brand").value = product.brand;
      document.getElementById("product-price").value = product.price;
      document.getElementById("product-category").value = product.category;
      document.getElementById("product-stock").value = product.stock;
      document.getElementById("product-description").value = product.description;
      editIndex = index;
   }
}

// 4. 關閉模態框
function closeModal() {
   document.getElementById("product-modal").style.display = "none";
}

// 5. 新增或編輯商品
document.getElementById("product-form").addEventListener("submit", async function (e) {
   e.preventDefault();

   const name = document.getElementById("product-name").value;
   const brand = document.getElementById("product-brand").value;
   const price = parseFloat(document.getElementById("product-price").value);
   const category = document.getElementById("product-category").value;
   const stock = parseInt(document.getElementById("product-stock").value);
   const description = document.getElementById("product-description").value;
   const imageFile = document.getElementById("product-image").files[0];

   let imageUrl = "";

   // 上傳圖片到伺服器
   if (imageFile) {
      try {
         imageUrl = await uploadImage(imageFile); // 調用圖片上傳 API
      } catch (error) {
         console.error("圖片上傳錯誤:", error);
         alert("圖片上傳失敗");
         return;
      }
   }

   const productData = { name, brand, price, category, stock, description, imageUrl };

   try {
      if (editIndex === null) {
         // 新增商品
         const response = await fetch(apiBaseUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(productData),
         });
         if (!response.ok) throw new Error("Failed to add product");
      } else {
         // 編輯商品
         const productId = products[editIndex]?.product_id; // 確保正確取得商品 ID
         if (!productId) throw new Error("無效的商品 ID");

         const response = await fetch(`${apiBaseUrl}/${productId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(productData),
         });
         if (!response.ok) throw new Error("Failed to update product");
      }

      closeModal();
      await fetchProducts(); // 重新獲取商品列表
   } catch (error) {
      console.error("Error saving product:", error);
      alert("保存商品失敗：" + error.message);
   }
});

// 6. 刪除商品
async function deleteProduct(productId) {
   if (!confirm("確定要下架此商品嗎？")) return;
   // console.log(productId);

   try {
      const response = await fetch(`${apiBaseUrl}/${productId}`, { method: "DELETE" });
      if (!response.ok) {
         const errorDetails = await response.text(); // 讀取伺服器返回的錯誤細節
         throw new Error(`Failed to delete product: ${response.status} - ${errorDetails}`);
      }

      alert("商品下架成功");
      await fetchProducts(); // 重新獲取商品列表
   } catch (error) {
      console.error("Error deleting product:", error);
      alert("下架商品失敗：" + error.message);
   }
}

// 7. 圖片上傳函式
async function uploadImage(file) {
   const formData = new FormData();
   formData.append("file", file); // 確保 key 是 "file"

   try {
      const response = await fetch(`${apiBaseUrl}/upload`, {
         method: "POST",
         body: formData,
      });

      if (!response.ok) {
         const error = await response.text();
         throw new Error("Image upload failed: " + error);
      }

      const result = await response.json();
      return result.url; // 返回圖片 URL
   } catch (error) {
      console.error("圖片上傳錯誤:", error);
      throw error;
   }
}


// 8. 編輯商品
function editProduct(index) {
   openModal("edit", index);
}

// 9. 初始化頁面
document.addEventListener("DOMContentLoaded", fetchProducts);



