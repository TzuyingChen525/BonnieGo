document.addEventListener('DOMContentLoaded', () => {
   const apiBase = "http://localhost:8080/api/user";
   const usernameField = document.getElementById("username");
   const emailField = document.getElementById("email");
   const phoneField = document.getElementById("phone");
   const nameField = document.getElementById("name");
   const genderField = document.getElementById("gender");
   const addressField = document.getElementById("address");

   // 登入後獲取使用者資料

   let isUserDataLoaded = false;

   const loadUserData = async () => {
      if (isUserDataLoaded) return; // 已經執行過就不再執行
      isUserDataLoaded = true;

      try {
         const response = await fetch(`${apiBase}/me`, {
            method: "GET",
            credentials: "include", // 確保帶上 Session Cookie
            headers: {
               "Content-Type": "application/json",
            },
         });

         if (response.ok) {
            const data = await response.json();
            usernameField.value = data.username || '';
            emailField.value = data.email || '';
            phoneField.value = data.phone || '';
            nameField.value = data.fullname || '';
            genderField.value = data.gender || '';
            addressField.value = data.address || '';

            // 根據角色進行重定向
            const userRole = data.role;  // 假設 API 回傳的資料中有 role
            const currentPath = window.location.pathname;

            if (userRole === "ADMIN" && currentPath !== "/admin-product.html") {
               window.location.href = "/admin-product.html";
            } else if (userRole !== "ADMIN" && currentPath !== "/user.html") {
               window.location.href = "/user.html";
            }
         } else {
            // 如果無法獲取用戶資料，則重定向到登入頁面
            window.location.href = "/login.html";
            isUserDataLoaded = false;

         }
      } catch (error) {
         console.error("加載用戶資料錯誤:", error);
         isUserDataLoaded = false;

      }
   };



   // 修改會員資料
   document.getElementById("updateUser-btn").addEventListener("click", () => {
      const updatedData = {
         email: emailField.value,
         phone: phoneField.value,
         fullname: nameField.value,
         gender: genderField.value,
         address: addressField.value
      };

      fetch(`${apiBase}/update`, {
         method: "PUT",
         credentials: "include", // 確保帶上 Session Cookie
         headers: {
            "Content-Type": "application/json"
         },
         body: JSON.stringify(updatedData)
      })
         .then(response => {
            if (!response.ok) {
               return response.json().then(error => {
                  throw new Error(error.message || "更新失敗");
               });
            }
            alert("會員資料更新成功");
         })
         .catch(err => alert(`更新失敗: ${err.message}`));
   });



   // 刪除會員
   // document.getElementById("deleteUser-btn").addEventListener("click", () => {
   //    if (!confirm("確定要刪除您的會員資料嗎？此操作無法恢復！")) {
   //       return;
   //    }

   //    fetch(`${apiBase}/delete`, {
   //       method: "DELETE",
   //       credentials: "include", // 確保帶上 Session Cookie
   //       headers: {
   //          "Content-Type": "application/json"
   //       }
   //    })
   //       .then(response => {
   //          if (!response.ok) {
   //             throw new Error("刪除會員失敗");
   //          }
   //          alert("會員已刪除");
   //          window.location.href = "/login.html";
   //       })
   //       .catch(err => alert(`刪除失敗: ${err.message}`));
   // });


   // 登出
   document.getElementById("logout-btn").addEventListener("click", () => {
      fetch(`${apiBase}/logout`, {
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


   loadUserData();// 載入用戶資料
});