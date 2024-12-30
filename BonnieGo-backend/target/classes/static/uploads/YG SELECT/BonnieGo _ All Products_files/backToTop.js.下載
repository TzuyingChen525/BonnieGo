// 獲取置頂按鈕
const topButton = document.getElementById("topBtn");

// 監聽滾動事件，當滾動超過一定高度顯示按鈕
window.onscroll = function () {
   if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
      topButton.style.display = "block";
   } else {
      topButton.style.display = "none";
   }
};

// 平滑滾動回到頂部
function scrollToTop() {
   window.scrollTo({
      top: 0,
      behavior: "smooth"
   });
}