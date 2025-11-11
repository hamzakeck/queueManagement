<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Queue Management System - Register</title>
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

    .register-container {
        max-width: 500px;
        margin: 30px auto;
        background: white;
        padding: 40px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    .register-header {
        text-align: center;
        margin-bottom: 30px;
    }

    .register-header h1 {
        color: #667eea;
        margin-bottom: 10px;
        font-size: 28px;
    }

    .register-header p {
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

    .hidden-field {
        display: none;
    }

    .button-group {
        display: flex;
        gap: 10px;
        margin-top: 30px;
    }

    .register-btn,
    .login-link-btn {
        flex: 1;
        padding: 12px;
        border: none;
        border-radius: 5px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }

    .register-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }

    .register-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }

    .login-link-btn {
        background: white;
        color: #667eea;
        border: 2px solid #667eea;
    }

    .login-link-btn:hover {
        background: #f5f7fa;
    }

    .validation-error {
        color: #e74c3c;
        font-size: 12px;
        margin-top: 5px;
    }

    .success-message {
        background: #d4edda;
        color: #155724;
        padding: 12px;
        border-radius: 5px;
        margin-bottom: 20px;
        display: none;
    }
</style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h1>🔗 Queue Management</h1>
            <p>Create your account</p>
        </div>

        <form id="registerForm" method="POST" action="${pageContext.request.contextPath}/RegisterServlet" onsubmit="return validateForm()">
            <div class="success-message" id="successMessage">
                Account created successfully! Redirecting...
            </div>

            <div class="form-group">
                <label for="role">Role:</label>
                <select id="role" name="role" required onchange="updateFieldVisibility()">
                    <option value="">-- Select Role --</option>
                    <option value="admin">Administrator</option>
                    <option value="employee">Employee</option>
                    <option value="citizen">Citizen</option>
                </select>
            </div>

            <div class="form-group">
                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" required placeholder="John">
            </div>

            <div class="form-group">
                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" required placeholder="Doe">
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required placeholder="your@email.com">
            </div>

            <!-- Citizen specific field -->
            <div class="form-group hidden-field" id="cinField">
                <label for="cin">CIN (National ID):</label>
                <input type="text" id="cin" name="cin" placeholder="AB123456">
            </div>

            <!-- Employee specific field -->
            <div class="form-group hidden-field" id="agencyIdField">
                <label for="agencyId">Agency ID:</label>
                <input type="number" id="agencyId" name="agencyId" placeholder="1">
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

            <div class="button-group">
                <button type="submit" class="register-btn">Register</button>
                <a href="${pageContext.request.contextPath}/login.jsp" style="flex: 1; text-decoration: none;">
                    <button type="button" class="login-link-btn" style="width: 100%;">Back to Login</button>
                </a>
            </div>
        </form>
    </div>

    <script>
        function updateFieldVisibility() {
            var role = document.getElementById('role').value;
            var cinField = document.getElementById('cinField');
            var agencyIdField = document.getElementById('agencyIdField');
            var cin = document.getElementById('cin');
            var agencyId = document.getElementById('agencyId');

            // Hide all role-specific fields
            cinField.classList.add('hidden-field');
            agencyIdField.classList.add('hidden-field');
            cin.required = false;
            agencyId.required = false;

            // Show relevant fields based on role
            if (role === 'citizen') {
                cinField.classList.remove('hidden-field');
                cin.required = true;
            } else if (role === 'employee') {
                agencyIdField.classList.remove('hidden-field');
                agencyId.required = true;
            }
        }

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

            // Check if passwords match
            if (password !== confirmPassword) {
                confirmPasswordError.textContent = 'Passwords do not match';
                return false;
            }

            return true;
        }

        // Initialize field visibility on page load
        window.addEventListener('load', function() {
            updateFieldVisibility();
        });
    </script>
</body>
</html>
