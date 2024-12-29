// 獲取相關 DOM 元素
const addTableButton = document.getElementById('addTable');
const formContainer = document.getElementById('formContainer');

// 表單模板生成函數
function createNewForm(index) {
   const formId = `customizationForm-${index}`;
   return `
      <div class="Customization-section my-3 px-3 py-3" id="${formId}">
         <form>
            <div class="form-rows">
               <div class="form-group">
                  <label for="page-${index}">代購商品頁網址：</label>
                  <input type="text" id="page-${index}" name="page-${index}"
                     placeholder="例：https://standoil.kr/product/detail.html?product_no=412&cate_no=405&display_group=1"
                     required>
                  <small class="error-message"></small>
               </div>
               <div class="form-group">
                  <label for="productName-${index}">商品名稱：</label>
                  <input type="text" id="productName-${index}" name="productName-${index}" placeholder="例：Chubby bag" required>
                  <small class="error-message"></small>
               </div>
               <div class="form-group">
                  <label for="productSpecifications-${index}">商品規格：</label>
                  <input type="text" id="productSpecifications-${index}" name="productSpecifications-${index}" placeholder="例：Black"
                     required>
                  <small class="error-message"></small>
               </div>
               <div class="form-group">
                  <label for="productPrice-${index}">韓幣單價：</label>
                  <input type="text" id="productPrice-${index}" name="productPrice-${index}" placeholder="例：119000" required>
                  <small class="error-message"></small>
               </div>
               <div class="form-group">
                  <label for="amount-${index}">購買數量：</label>
                  <input type="text" id="amount-${index}" name="amount-${index}" placeholder="例：1" required>
                  <small class="error-message"></small>
               </div>
            </div>
         </form>
         ${index > 0 ? `<button class="btn btn-danger btn-sm deleteForm" data-id="${formId}">刪除</button>` : ''}
      </div>
   `;
}

// 當按下「新增訂購表單」按鈕
addTableButton.addEventListener('click', () => {
   const formCount = formContainer.children.length + 1;
   const newFormHTML = createNewForm(formCount);
   formContainer.insertAdjacentHTML('beforeend', newFormHTML);
});

// 當按下「刪除」按鈕
formContainer.addEventListener('click', (e) => {
   if (e.target.classList.contains('deleteForm')) {
      const formId = e.target.getAttribute('data-id');
      const formElement = document.getElementById(formId);
      if (formElement) {
         formElement.remove();
      }
   }
});
