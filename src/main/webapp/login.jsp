<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login - Queue Management</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        background: #f8f9fa;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1.5rem;
    }

    .login-container {
        max-width: 400px;
        width: 100%;
        background: white;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        padding: 2.5rem;
    }

    .login-header {
        text-align: center;
        margin-bottom: 2rem;
    }

    .login-header h1 {
        font-size: 1.5rem;
        font-weight: 600;
        color: #212529;
        margin-bottom: 0.5rem;
    }

    .login-header p {
        color: #6c757d;
        font-size: 0.875rem;
    }

    .form-group {
        margin-bottom: 1.25rem;
    }

    label {
        display: block;
        margin-bottom: 0.5rem;
        color: #212529;
        font-weight: 500;
        font-size: 0.875rem;
    }

    input[type="email"],
    input[type="password"] {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #dee2e6;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        font-family: inherit;
        transition: border-color 0.2s;
    }

    input:focus {
        outline: none;
        border-color: #495057;
    }

    .login-btn {
        width: 100%;
        padding: 0.75rem;
        background: #212529;
        color: white;
        border: none;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
        margin-top: 0.5rem;
    }

    .login-btn:hover {
        background: #000;
    }

    .register-link {
        text-align: center;
        margin-top: 1.5rem;
        padding-top: 1.5rem;
        border-top: 1px solid #e9ecef;
    }

    .register-link p {
        color: #6c757d;
        font-size: 0.875rem;
    }

    .register-link a {
        color: #212529;
        font-weight: 500;
        text-decoration: none;
    }

    .register-link a:hover {
        text-decoration: underline;
    }

    .error-message {
        background: #f8d7da;
        color: #842029;
        padding: 0.75rem;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        margin-bottom: 1.25rem;
        border: 1px solid #f5c2c7;
    }
</style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>Queue Management</h1>
            <p>Sign in to your account</p>
        </div>

        <% 
        String error = request.getParameter("error");
        if (error != null) {
        %>
            <div class="error-message">
                Invalid email or password. Please try again.
            </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/LoginServlet" method="POST">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required placeholder="your@email.com">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required placeholder="Enter your password">
            </div>

            <button type="submit" class="login-btn">Sign In</button>
        </form>

        <div class="register-link">
            <p>Don't have an account? <a href="<%= request.getContextPath() %>/register.jsp">Register here</a></p>
        </div>
    </div>
</body>
</html>
