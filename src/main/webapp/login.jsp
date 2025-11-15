<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Queue Management System - Login</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        padding: 20px;
        min-height: 100vh;
    }

    .login-container {
        max-width: 450px;
        margin: 50px auto;
        background: white;
        padding: 40px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    .login-header {
        text-align: center;
        margin-bottom: 30px;
    }

    .login-header h1 {
        color: #667eea;
        margin-bottom: 10px;
        font-size: 28px;
    }

    .login-header p {
        color: #999;
        font-size: 14px;
    }

    .form-group {
        margin-bottom: 20px;
    }

    label {
        display: block;
        margin-bottom: 8px;
        color: #333;
        font-weight: 600;
        font-size: 14px;
    }

    select,
    input[type="email"],
    input[type="password"] {
        width: 100%;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 14px;
        font-family: inherit;
        transition: border-color 0.3s;
    }

    select:focus,
    input:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .button-group {
        display: flex;
        gap: 10px;
        margin-top: 30px;
    }

    .login-btn,
    .register-btn {
        flex: 1;
        padding: 12px;
        border: none;
        border-radius: 5px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }

    .login-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }

    .login-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }

    .register-btn {
        background: white;
        color: #667eea;
        border: 2px solid #667eea;
    }

    .register-btn:hover {
        background: #f5f7fa;
    }
</style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>Queue Management</h1>
            <p>Sign in to your account</p>
        </div>

        <form id="loginForm" method="POST">
            <div class="form-group">
                <label for="role">Role:</label>
                <select id="role" name="role" required onchange="updateFormAction()">
                    <option value="">-- Select Role --</option>
                    <option value="admin">Administrator</option>
                    <option value="employee">Employee</option>
                    <option value="citizen">Citizen</option>
                </select>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required placeholder="your@email.com">
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required placeholder="••••••••">
            </div>

            <div class="button-group">
                <button type="submit" class="login-btn">Login</button>
                <a href="${pageContext.request.contextPath}/register.jsp" style="flex: 1; text-decoration: none;">
                    <button type="button" class="register-btn" style="width: 100%;">Register</button>
                </a>
            </div>
        </form>
    </div>

    <script>
        var contextPath = '<%= request.getContextPath() %>';
        
        function updateFormAction() {
            var role = document.getElementById('role').value;
            var form = document.getElementById('loginForm');
            
            switch(role) {
                case 'admin':
                    form.action = contextPath + '/admin/LoginServlet';
                    break;
                case 'employee':
                    form.action = contextPath + '/employee/LoginServlet';
                    break;
                case 'citizen':
                    form.action = contextPath + '/citizen/LoginServlet';
                    break;
                default:
                    form.action = '';
            }
        }
        
        // Initialize form action on page load if role is pre-selected
        window.addEventListener('DOMContentLoaded', function() {
            updateFormAction();
        });
    </script>
</body>
</html>
