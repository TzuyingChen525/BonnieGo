
const itemsPerPage = 15; // 每頁顯示商品數量
let currentCategory = "all"; // 追蹤當前分類
let currentSortOption = null; // 當前排序方式
let currentPage = 1; // 當前頁碼

// 篩選與排序商品
function filterProducts(products, category, sortOption = null) {
   // 篩選類別或品牌
   let filteredProducts = products.filter(product => {
      if (category === "all") return true; // "all" 顯示所有商品
      if (["Stand Oil", "nonenon", "YG SELECT"].includes(category)) {
         return product.brand.toLowerCase() === category.toLowerCase(); // 比較時忽略大小寫
      }
      return product.category.includes(category); // 篩選類別
   });

   // 排序邏輯
   if (sortOption && sortOption !== "null") {
      filteredProducts = [...filteredProducts]; // 深拷貝避免修改原數據
      if (sortOption === "price-desc") {
         filteredProducts.sort((a, b) => b.price - a.price); // 價格由高到低
      } else if (sortOption === "price-asc") {
         filteredProducts.sort((a, b) => a.price - b.price); // 價格由低到高
      }
   }

   return filteredProducts;
}

// 分頁處理
function paginateProducts(products, page, itemsPerPage) {
   const totalItems = products.length;
   const totalPages = Math.ceil(totalItems / itemsPerPage);
   const startIndex = (page - 1) * itemsPerPage;
   const endIndex = startIndex + itemsPerPage;

   return {
      paginatedProducts: products.slice(startIndex, endIndex),
      totalPages,
   };
}

// 渲染商品列表
function displayProducts(category, sortOption = null, page = 1) {
   const productList = document.getElementById("product-list");
   const categoryTitle = document.getElementById("category-title");

   // 更新篩選條件
   currentCategory = category;
   currentSortOption = sortOption;
   currentPage = page;

   // 篩選與排序
   const filteredProducts = filterProducts(products, category, sortOption);
   const { paginatedProducts, totalPages } = paginateProducts(filteredProducts, page, itemsPerPage);

   // 更新分類標題
   categoryTitle.textContent = category === "all" ? "所有商品" : category;

   // 渲染商品
   productList.innerHTML = "";
   if (paginatedProducts.length === 0) {
      productList.innerHTML = "<p>未找到符合條件的商品。</p>";
   } else {
      paginatedProducts.forEach(product => {
         productList.innerHTML += `
            <div class="product-item">
               <img src="${product.imageUrl}" alt="${product.name}">
               <h3>${product.name}</h3>
               <p class="price my-1">$${product.price}</p>
               <p class="description py-1 px-1 my-2" style="font-size:0.8rem;background-color:rgba(184, 186, 189, 0.2);border-radius:5px;height:30px;">${product.description}</p>
               
            <button class="wishlist-btn btn"
                     onclick="addToFavorites({
                     product_id: '${product.product_id}', 
                     name: '${product.name}', 
                     price: ${product.price}, 
                     imageUrl: '${product.imageUrl}'
                     })">
               <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
               <path d="M440-501Zm0 381L313-234q-72-65-123.5-116t-85-96q-33.5-45-49-87T40-621q0-94 63-156.5T260-840q52 0 99 22t81 62q34-40 81-62t99-22q81 0 136 45.5T831-680h-85q-18-40-53-60t-73-20q-51 0-88 27.5T463-660h-46q-31-45-70.5-72.5T260-760q-57 0-98.5 39.5T120-621q0 33 14 67t50 78.5q36 44.5 98 104T440-228q26-23 61-53t56-50l9 9 19.5 19.5L605-283l9 9q-22 20-56 49.5T498-172l-58 52Zm280-160v-120H600v-80h120v-120h80v120h120v80H800v120h-80Z" />
               </svg>
            </button>

         <button class="add-to-cart-btn btn" onclick="addToCart({product_id:'${product.product_id}',name: '${product.name}', price: ${product.price}, imageUrl: '${product.imageUrl}', quantity: 1,stock:'${product.stock}' })"><svg xmlns="http://www.w3.org/2000/svg" height="24px"
                     viewBox="0 0 24 24" width="24px" fill="#5f6368">
                     <path d="M0 0h24v24H0zm18.31 6l-2.76 5z" fill="none" />
                     <path
                        d="M11 9h2V6h3V4h-3V1h-2v3H8v2h3v3zm-4 9c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2zm-9.83-3.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.86-7.01L19.42 4h-.01l-1.1 2-2.76 5H8.53l-.13-.27L6.16 6l-.95-2-.94-2H1v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.13 0-.25-.11-.25-.25z" />
         </svg></button>
            </div>`;
      });
   }

   // 渲染分頁按鈕
   renderPagination(totalPages, currentPage);
}

// 渲染分頁按鈕
function renderPagination(totalPages, currentPage) {
   const pagination = document.getElementById("pagination");
   pagination.innerHTML = ""; //清空分頁

   if (totalPages <= 1) return; // 無需分頁按鈕

   // 上一頁按鈕
   if (currentPage > 1) {
      pagination.innerHTML += `
         <button class="btn btn-sm btn-outline-secondary mx-1" 
         onclick="displayProducts('${currentCategory}', '${currentSortOption}', ${currentPage - 1})">«</button>`;
   }

   // 中間頁碼按鈕
   for (let i = 1; i <= totalPages; i++) {
      pagination.innerHTML += `
         <button class="btn btn-sm btn-outline-secondary mx-1 ${currentPage === i ? "active" : ""}" 
               onclick="displayProducts('${currentCategory}', '${currentSortOption}', ${i})">
            ${i}
         </button>`;
   }

   // 下一頁按鈕
   if (currentPage < totalPages) {
      pagination.innerHTML += `
         <button class="btn btn-sm btn-outline-secondary mx-1" 
               onclick="displayProducts('${currentCategory}', '${currentSortOption}', ${currentPage + 1})">»</button>`;
   }
}

// 綁定事件
document.querySelectorAll(".category-item").forEach(item => {
   item.addEventListener("click", function (e) {
      e.preventDefault();
      const category = item.getAttribute("data-category");
      displayProducts(category, currentSortOption, 1);
   });
});

document.getElementById("sortSelect").addEventListener("change", function () {
   displayProducts(currentCategory, this.value, 1);
});

// 從 API 獲取商品資料
async function fetchProducts() {
   try {
      const response = await fetch(`http://localhost:8080/api/products?size=${itemsPerPage}`, {
         method: "GET",
         headers: { "Content-Type": "application/json" },
      });

      if (!response.ok) {
         throw new Error(`HTTP error! status: ${response.status}`);
      }

      const apiData = await response.json();
      const products = apiData.content.map(item => ({
         product_id: item.product_id,
         name: item.name,
         category: ["all", item.category], // 確保 category 是陣列
         price: item.price,
         brand: item.brand.trim(), // 去除多餘空格
         imageUrl: item.imageUrl, // 默認圖片
         description: item.description,
         stock: item.stock
      }));

      console.log(products);


      return products; // 返回處理後的商品數據
   } catch (error) {
      console.error("無法加載商品資料:", error);
      return [];
   }
}


// 加載商品並初始化顯示
let products = []; // 初始化為空數據

document.addEventListener("DOMContentLoaded", async () => {
   // 從後端獲取商品資料
   products = await fetchProducts();

   if (products.length === 0) {
      console.error("商品列表為空，請檢查 API 或資料格式。");
   }

   // 顯示商品
   displayProducts("all", null, 1);
});










