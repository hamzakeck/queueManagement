<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register - Queue Management</title>
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

    .register-container {
        max-width: 500px;
        width: 100%;
        background: white;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        padding: 2.5rem;
    }

    .register-header {
        text-align: center;
        margin-bottom: 2rem;
    }

    .register-header h1 {
        font-size: 1.5rem;
        font-weight: 600;
        color: #212529;
        margin-bottom: 0.5rem;
    }

    .register-header p {
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

    select,
    input[type="text"],
    input[type="email"],
    input[type="password"],
    input[type="number"] {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #dee2e6;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        font-family: inherit;
        transition: border-color 0.2s;
    }

    select:focus,
    input:focus {
        outline: none;
        border-color: #495057;
    }

    .hidden-field {
        display: none;
    }

    .register-btn {
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

    .register-btn:hover {
        background: #000;
    }

    .validation-error {
        color: #842029;
        font-size: 0.75rem;
        margin-top: 0.25rem;
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

    .success-message {
        background: #d1e7dd;
        color: #0f5132;
        padding: 0.75rem;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        margin-bottom: 1.25rem;
        border: 1px solid #badbcc;
        display: none;
    }

    .login-link {
        text-align: center;
        margin-top: 1.5rem;
        padding-top: 1.5rem;
        border-top: 1px solid #e9ecef;
    }

    .login-link p {
        color: #6c757d;
        font-size: 0.875rem;
    }

    .login-link a {
        color: #212529;
        font-weight: 500;
        text-decoration: none;
    }

    .login-link a:hover {
        text-decoration: underline;
    }
</style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h1>Queue Management</h1>
            <p>Create your account</p>
        </div>

        <form id="registerForm" method="POST" action="${pageContext.request.contextPath}/RegisterServlet" onsubmit="return validateForm()">
            <div class="success-message" id="successMessage">
                Account created successfully! Redirecting...
            </div>

            <div class="form-group">
                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" required placeholder="Ahmed">
            </div>

            <div class="form-group">
                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" required placeholder="Ahmadi">
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required placeholder="your@email.com">
            </div>

            <div class="form-group">
                <label for="cin">CIN (National ID):</label>
                <input type="text" id="cin" name="cin" required placeholder="AB123456">
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required placeholder="••••••••" minlength="6">
                <div class="validation-error" id="passwordError"></div>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required placeholder="••••••••" minlength="6">
                <div class="validation-error" id="confirmPasswordError"></div>
            </div>

            <button type="submit" class="register-btn">Register</button>
        </form>

        <div class="login-link">
            <p>Already have an account? <a href="<%= request.getContextPath() %>/login.jsp">Sign in here</a></p>
        </div>
    </div>

    <script>
        function validateForm() {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            var passwordError = document.getElementById('passwordError');
            var confirmPasswordError = document.getElementById('confirmPasswordError');

            // Reset error messages
            passwordError.textContent = '';
            confirmPasswordError.textContent = '';

            // Check password length
            if (password.length < 6) {
                passwordError.textContent = 'Password must be at least 6 characters';
                return false;
            }

            // if passwords match
            if (password !== confirmPassword) {
                confirmPasswordError.textContent = 'Passwords do not match';
                return false;
            }

            return true;
        }
    </script>
</body>
</html>
