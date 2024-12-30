const registerForm = document.getElementById("registerForm");

registerForm.addEventListener("submit", async (e) => {
   e.preventDefault(); // 阻止默認提交

   // 獲取表單輸入值
   const username = document.getElementById("username").value.trim();
   const password = document.getElementById("password").value;
   const phone = document.getElementById("phone").value.trim();

   // 驗證輸入規則
   if (!username || username.length < 3 || username.length > 20) {
      alert("用戶名長度應為 3 到 20 個字");
      return;
   }

   if (!password || password.length < 5 || password.length > 18) {
      alert("密碼長度應為 5 到 18 個字");
      return;
   }

   if (!/^\d{10}$/.test(phone)) {
      alert("請輸入有效的電話號碼");
      return;
   }


   const formData = new FormData(registerForm); // 獲取表單數據
   const data = Object.fromEntries(formData.entries()); // 將 FormData 轉換為Object
   console.log(data); // 檢查表單數據

   try {
      const response = await fetch("http://localhost:8080/api/user/register", {
         method: "POST",
         credentials: "include", // 攜帶 Cookie
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify(data)
      });

      if (response.ok) {
         alert("註冊會員成功!");
         window.location.href = "./login.html";  // 註冊成功後跳轉至index.html
      } else {
         const errorData = await response.text(); // 先將回應內容作為文本讀取
         try {
            const parsedErrorData = JSON.parse(errorData); // 嘗試解析 JSON
            if (parsedErrorData.message && parsedErrorData.message.includes("Duplicate entry")) {
               alert("該電話號碼或用戶名已經被註冊，請更換後再試！");
            } else {
               alert("註冊會員失敗，請稍後再試！");
            }
         } catch (e) {
            alert("註冊過程中發生未知錯誤，請稍後再試！錯誤詳情：" + errorData);
            console.error("Error parsing JSON:", e, errorData);
         }
      }
   } catch (err) {
      alert("無法連接伺服器，請稍後再試！");
      console.error("Fetch error:", err);
   }
});