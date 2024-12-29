// 解析 URL 查詢參數
const urlParams = new URLSearchParams(window.location.search);
const orderId = urlParams.get('orderId');

// 更新訂單編號
if (orderId) {
   document.getElementById('orderId').textContent = orderId;
} else {
   document.getElementById('orderId').textContent = '無效的訂單編號';
}
