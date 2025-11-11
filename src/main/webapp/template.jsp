<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // This is a template - copy the style section and adapt the body content
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Queue Management System - Template</title>
<style>
    /* Reset */
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f5f7fa;
    }

    /* Navbar for Dashboards */
    .navbar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 20px 40px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .navbar h1 {
        font-size: 24px;
    }

    .navbar-right {
        display: flex;
        align-items: center;
        gap: 30px;
    }

    .user-info {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
    }

    .user-info p {
        font-size: 14px;
        opacity: 0.9;
    }

    .user-info .role {
        font-weight: 600;
        font-size: 16px;
    }

    .logout-btn {
        background-color: rgba(255, 255, 255, 0.2);
        color: white;
        border: 2px solid white;
        padding: 8px 20px;
        border-radius: 5px;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 14px;
        font-weight: 600;
    }

    .logout-btn:hover {
        background-color: white;
        color: #667eea;
    }

    .container {
        max-width: 1200px;
        margin: 40px auto;
        padding: 0 20px;
    }

    /* For Login/Register pages */
    body.auth-page {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 20px;
        min-height: 100vh;
    }

    .auth-container {
        max-width: 500px;
        margin: 50px auto;
        background: white;
        padding: 40px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    .auth-header {
        text-align: center;
        margin-bottom: 30px;
    }

    .auth-header h1 {
        color: #667eea;
        margin-bottom: 10px;
        font-size: 28px;
    }

    .auth-header p {
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
    input[type="text"],
    input[type="email"],
    input[type="password"],
    input[type="number"] {
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

    .btn {
        flex: 1;
        padding: 12px;
        border: none;
        border-radius: 5px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }

    .btn-primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }

    .btn-primary:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }

    .btn-secondary {
        background: white;
        color: #667eea;
        border: 2px solid #667eea;
    }

    .btn-secondary:hover {
        background: #f5f7fa;
    }

    .hidden-field {
        display: none;
    }

    .validation-error {
        color: #e74c3c;
        font-size: 12px;
        margin-top: 5px;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .navbar {
            flex-direction: column;
            gap: 15px;
        }

        .navbar-right {
            width: 100%;
            justify-content: space-between;
        }

        .user-info {
            align-items: flex-start;
        }
    }
</style>
</head>
<body>
    <!-- For Dashboard Pages -->
    <div class="navbar">
        <h1>🔗 Queue Management System</h1>
        <div class="navbar-right">
            <div class="user-info">
                <p>user@email.com</p>
                <p class="role">ROLE</p>
            </div>
            <form action="LogoutServlet" method="POST" style="display: inline;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <h2>Dashboard Content</h2>
        <p>Your content here</p>
    </div>
</body>
</html>
