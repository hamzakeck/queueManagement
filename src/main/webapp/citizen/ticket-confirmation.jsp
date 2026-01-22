<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="models.Agency" %>
<%@ page import="models.Service" %>
<%@ page import="dao.AgencyDAO" %>
<%@ page import="dao.ServiceDAO" %>
<%@ page import="dao.DAOFactory" %>
<%
    // Check if user is logged in and is citizen
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Get ticket information from session
    Integer ticketId = (Integer) session.getAttribute("newTicketId");
    String ticketNumber = (String) session.getAttribute("newTicketNumber");
    Integer position = (Integer) session.getAttribute("newTicketPosition");
    Integer agencyId = (Integer) session.getAttribute("newTicketAgencyId");
    Integer serviceId = (Integer) session.getAttribute("newTicketServiceId");

    if (ticketId == null || ticketNumber == null) {
        response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
        return;
    }

    // Get agency and service details
    AgencyDAO agencyDAO = DAOFactory.getInstance().getAgencyDAO();
    ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
    
    Agency agency = agencyDAO.findById(agencyId);
    Service service = serviceDAO.findById(serviceId);

    // Calculate estimated wait time (position * service time)
    int estimatedWait = 0;
    if (position != null && service.getEstimatedTime() > 0) {
        estimatedWait = position * service.getEstimatedTime();
    }

    // Clear the session attributes after retrieval (one-time view)
    // Comment out if you want to keep them for refresh
    // session.removeAttribute("newTicketId");
    // session.removeAttribute("newTicketNumber");
    // session.removeAttribute("newTicketPosition");
    // session.removeAttribute("newTicketAgencyId");
    // session.removeAttribute("newTicketServiceId");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket Created - Queue Management</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
    }

    .confirmation-container {
        max-width: 600px;
        background: white;
        border-radius: 15px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        overflow: hidden;
    }

    .success-header {
        background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
        color: white;
        padding: 40px;
        text-align: center;
    }

    .success-icon {
        font-size: 64px;
        margin-bottom: 15px;
        animation: scaleIn 0.5s ease-out;
    }

    @keyframes scaleIn {
        from {
            transform: scale(0);
        }
        to {
            transform: scale(1);
        }
    }

    .success-header h1 {
        font-size: 28px;
        margin-bottom: 10px;
    }

    .success-header p {
        font-size: 16px;
        opacity: 0.95;
    }

    .ticket-details {
        padding: 40px;
    }

    .ticket-number {
        text-align: center;
        margin-bottom: 30px;
    }

    .ticket-number-label {
        font-size: 14px;
        color: #666;
        margin-bottom: 10px;
    }

    .ticket-number-value {
        font-size: 72px;
        font-weight: bold;
        color: #667eea;
        font-family: 'Courier New', monospace;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    }

    .info-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-bottom: 30px;
    }

    .info-item {
        background: #f5f7fa;
        padding: 20px;
        border-radius: 10px;
        text-align: center;
    }

    .info-label {
        font-size: 12px;
        color: #666;
        text-transform: uppercase;
        margin-bottom: 8px;
    }

    .info-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
    }

    .info-value.large {
        font-size: 32px;
        color: #667eea;
    }

    .details-section {
        border-top: 2px solid #f0f0f0;
        padding-top: 20px;
        margin-bottom: 30px;
    }

    .details-section h3 {
        color: #333;
        margin-bottom: 15px;
        font-size: 18px;
    }

    .detail-row {
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        border-bottom: 1px solid #f0f0f0;
    }

    .detail-label {
        color: #666;
        font-size: 14px;
    }

    .detail-value {
        color: #333;
        font-weight: 600;
        font-size: 14px;
    }

    .action-buttons {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 15px;
    }

    .btn {
        padding: 15px;
        border-radius: 8px;
        text-decoration: none;
        text-align: center;
        font-weight: 600;
        font-size: 14px;
        transition: all 0.3s;
        cursor: pointer;
        border: none;
    }

    .btn-primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }

    .btn-primary:hover {
        opacity: 0.9;
        transform: translateY(-2px);
    }

    .btn-secondary {
        background: white;
        color: #667eea;
        border: 2px solid #667eea;
    }

    .btn-secondary:hover {
        background: #f5f7fa;
    }

    .note {
        background: #fff8e1;
        border-left: 4px solid #ffc107;
        padding: 15px;
        margin-top: 20px;
        border-radius: 5px;
        font-size: 13px;
        color: #666;
    }

    .note strong {
        color: #333;
    }

    @media print {
        body {
            background: white;
        }
        .action-buttons {
            display: none;
        }
    }
</style>
</head>
<body>
    <div class="confirmation-container">
        <div class="success-header">
            <h1>Ticket Created Successfully!</h1>
            <p>Your queue ticket has been generated</p>
        </div>

        <div class="ticket-details">
            <div class="ticket-number">
                <div class="ticket-number-label">YOUR TICKET NUMBER</div>
                <div class="ticket-number-value"><%= ticketNumber %></div>
            </div>

            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Position in Queue</div>
                    <div class="info-value large"><%= position != null ? position : "N/A" %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Estimated Wait</div>
                    <div class="info-value large" id="wait-time">
                        --
                    </div>
                </div>
            </div>

            <div class="details-section">
                <h3>Ticket Details</h3>
                <div class="detail-row">
                    <span class="detail-label">Ticket ID</span>
                    <span class="detail-value">#<%= ticketId %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Agency</span>
                    <span class="detail-value"><%= agency.getName() %> - <%= agency.getCity() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Service</span>
                    <span class="detail-value"><%= service.getName() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Status</span>
                    <span class="detail-value" style="color: #ff9800;">WAITING</span>
                </div>
            </div>

            <div class="note">
                <strong>Important:</strong> Please arrive at the agency before your turn. 
                You can track your ticket status in real-time from the dashboard.
            </div>

            <div class="action-buttons">
                <a href="<%= request.getContextPath() %>/citizen/track-ticket.jsp?ticketId=<%= ticketId %>" class="btn btn-primary">
                    Track Ticket
                </a>
                <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="btn btn-secondary">
                    Dashboard
                </a>
            </div>
        </div>
    </div>

    <script>
        let ws;
        let countdownTimer = null;
        let waitTimeSeconds = 0;
        const ticketNumber = '<%= ticketNumber %>';
        
        // Connect to WebSocket for real-time updates
        function connectWebSocket() {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = protocol + '//' + window.location.host + '<%= request.getContextPath() %>/queue-updates';
            
            try {
                ws = new WebSocket(wsUrl);
                
                ws.onmessage = function(event) {
                    try {
                        const data = JSON.parse(event.data);
                        
                        // Handle wait time updates
                        if (data.action === 'waitTimeUpdate') {
                            data.tickets.forEach(ticketData => {
                                if (ticketData.ticketNumber === ticketNumber) {
                                    updateWaitTime(ticketData.estimatedWaitMinutes);
                                }
                            });
                        }
                        // Handle general queue updates
                        else if (data.action === 'queueUpdate') {
                            fetchWaitTime();
                        }
                        // Handle ticket status change
                        else if (data.ticketNumber === ticketNumber) {
                            if (data.status === 'CALLED' || data.status === 'IN_PROGRESS') {
                                showNotification('Your turn! Please proceed to the counter.');
                                window.location.href = '<%= request.getContextPath() %>/citizen/track-ticket.jsp';
                            }
                        }
                    } catch (e) {
                        console.error('WebSocket error:', e);
                    }
                };
                
                ws.onclose = function() {
                    setTimeout(connectWebSocket, 3000);
                };
            } catch (error) {
                console.error('WebSocket connection failed:', error);
            }
        }
        
        // Start countdown timer
        function startCountdown(initialMinutes) {
            if (countdownTimer) {
                clearInterval(countdownTimer);
            }
            
            if (initialMinutes === undefined || initialMinutes === null || isNaN(initialMinutes) || initialMinutes < 0) {
                console.warn('Invalid wait time:', initialMinutes);
                return;
            }
            
            // Check if we have a stored start time for this ticket
            const storageKey = 'ticket_' + ticketNumber + '_countdown';
            let countdownData = localStorage.getItem(storageKey);
            
            if (countdownData) {
                // Parse stored data
                countdownData = JSON.parse(countdownData);
                const startTime = countdownData.startTime;
                const initialSeconds = countdownData.initialSeconds;
                
                // Calculate elapsed time since start
                const now = Date.now();
                const elapsedSeconds = Math.floor((now - startTime) / 1000);
                const remainingSeconds = Math.max(0, initialSeconds - elapsedSeconds);
                
                // Check if the new estimate is significantly different (more than 30 seconds)
                const newTotalSeconds = initialMinutes * 60;
                if (Math.abs(newTotalSeconds - initialSeconds) > 30) {
                    // New estimate from server, restart countdown
                    waitTimeSeconds = newTotalSeconds;
                    localStorage.setItem(storageKey, JSON.stringify({
                        startTime: Date.now(),
                        initialSeconds: newTotalSeconds
                    }));
                } else {
                    // Continue with remaining time
                    waitTimeSeconds = remainingSeconds;
                }
            } else {
                // First time, store the start time
                const totalSeconds = initialMinutes * 60;
                waitTimeSeconds = totalSeconds;
                localStorage.setItem(storageKey, JSON.stringify({
                    startTime: Date.now(),
                    initialSeconds: totalSeconds
                }));
            }
            updateCountdownDisplay();
            
            countdownTimer = setInterval(() => {
                if (waitTimeSeconds > 0) {
                    waitTimeSeconds--;
                    updateCountdownDisplay();
                }
            }, 1000);
        }
        
        // Update countdown display
        function updateCountdownDisplay() {
            const waitEl = document.getElementById('wait-time');
            if (waitEl) {
                if (waitTimeSeconds === undefined || waitTimeSeconds === null) {
                    waitEl.textContent = '--';
                    return;
                }
                
                if (waitTimeSeconds === 0) {
                    waitEl.textContent = 'Next!';
                    waitEl.style.color = '#4CAF50';
                    if (countdownTimer) {
                        clearInterval(countdownTimer);
                    }
                    // Clear from localStorage
                    localStorage.removeItem('ticket_' + ticketNumber + '_countdown');
                } else {
                    const minutes = Math.floor(waitTimeSeconds / 60);
                    const seconds = waitTimeSeconds % 60;
                    const paddedSeconds = seconds < 10 ? '0' + seconds : seconds;
                    waitEl.textContent = '~' + minutes + 'm ' + paddedSeconds + 's';
                    waitEl.style.color = '#667eea';
                }
            }
        }
        
        // Update wait time from WebSocket
        function updateWaitTime(minutes) {
            startCountdown(minutes);
        }
        
        // Fetch initial wait time from server
        function fetchWaitTime() {
            fetch('<%= request.getContextPath() %>/citizen/GetWaitTimeServlet?ticketNumber=' + ticketNumber)
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        console.error('Error:', data.error);
                        return;
                    }
                    
                    // Update position
                    const positionEl = document.querySelector('.info-value.large');
                    if (positionEl && data.position !== undefined) {
                        positionEl.textContent = data.position;
                    }
                    
                    // Start countdown
                    if (data.estimatedWaitMinutes !== undefined) {
                        startCountdown(data.estimatedWaitMinutes);
                    }
                })
                .catch(error => {
                    console.error('Failed to fetch wait time:', error);
                });
        }
        
        // Show browser notification
        function showNotification(message) {
            if ("Notification" in window && Notification.permission === "granted") {
                new Notification("Queue Update", { body: message });
            }
        }
        
        // Request notification permission
        if ("Notification" in window && Notification.permission === "default") {
            Notification.requestPermission();
        }
        
        // Initialize on page load
        window.onload = function() {
            connectWebSocket();
            
            // Fetch initial wait time after WebSocket connects
            setTimeout(function() {
                fetchWaitTime();
            }, 1000);
            
            // Auto-print option (uncomment if needed)
            // setTimeout(function() {
            //     if (confirm('Would you like to print your ticket?')) {
            //         window.print();
            //     }
            // }, 500);
        };
    </script>
</body>
</html>
