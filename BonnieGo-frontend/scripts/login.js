const loginForm = document.getElementById("login-form");
loginForm.addEventListener("submit", async (e) => {
   e.preventDefault();

   const username = document.getElementById("username").value.trim();
   const password = document.getElementById("password").value.trim();

   if (!username || !password) {
      alert("請輸入帳號和密碼");
      return;
   }

   const data = {
      username: username,
      password: password
   };

   try {
      const response = await fetch("http://localhost:8080/api/user/login", {
         method: "POST",
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify(data),
         credentials: "include" // 攜帶 Cookie
      });


      if (response.ok) {
         localStorage.setItem('userID', data.id); // 保存用戶 ID
         localStorage.setItem('username', data.username); // 保存用戶名
      } else {
         alert('登入失敗，請檢查帳號或密碼');
      }

      const result = await response.json(); // 解析響應數據
      console.log("Login successful:", result);

      // 根據角色重定向
      const userRole = result.role;
      if (userRole === "ADMIN") {
         window.location.href = "/admin-product.html";
      } else {
         window.location.href = "/user.html";
      }

   } catch (error) {
      console.error("Error during login:", error);
      alert(`Login failed: ${error.message}`);
   }
});


