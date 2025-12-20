<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="models.Ticket" %>
<%@ page import="models.Service" %>
<%@ page import="models.Agency" %>
<%@ page import="dao.TicketDAO" %>
<%@ page import="dao.ServiceDAO" %>
<%@ page import="dao.AgencyDAO" %>
<%@ page import="dao.DAOFactory" %>
<%
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer citizenId = (Integer) session.getAttribute("userId");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();
    ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
    AgencyDAO agencyDAO = DAOFactory.getInstance().getAgencyDAO();
    
    List<Ticket> myTickets = ticketDAO.findByCitizenId(citizenId);
    
    // Load service and agency names
    Map<Integer, String> serviceNames = new HashMap<>();
    Map<Integer, String> agencyNames = new HashMap<>();
    
    for (Service service : serviceDAO.findAll()) {
        serviceNames.put(service.getId(), service.getName());
    }
    
    for (Agency agency : agencyDAO.findAll()) {
        agencyNames.put(agency.getId(), agency.getName());
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Track Tickets</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        background: #f8f9fa;
        color: #212529;
        line-height: 1.6;
    }

    .header {
        background: #fff;
        border-bottom: 1px solid #e9ecef;
        padding: 1rem 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .header h1 {
        font-size: 1.25rem;
        font-weight: 600;
    }

    .back-link {
        color: #495057;
        text-decoration: none;
        font-size: 0.875rem;
    }

    .back-link:hover {
        color: #212529;
    }

    .container {
        max-width: 900px;
        margin: 2rem auto;
        padding: 0 1.5rem;
    }

    .page-title {
        margin-bottom: 2rem;
    }

    .page-title h2 {
        font-size: 1.5rem;
        font-weight: 600;
        margin-bottom: 0.25rem;
    }

    .page-title p {
        color: #6c757d;
        font-size: 0.875rem;
    }

    .tickets-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .ticket-card {
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        padding: 1.5rem;
        transition: all 0.2s;
    }

    .ticket-card:hover {
        border-color: #adb5bd;
    }

    .ticket-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .ticket-number {
        font-size: 1.5rem;
        font-weight: 700;
        font-family: 'Courier New', monospace;
        color: #212529;
    }

    .status-badge {
        padding: 0.375rem 0.75rem;
        border-radius: 0.25rem;
        font-size: 0.75rem;
        font-weight: 600;
        text-transform: uppercase;
    }

    .status-waiting { background: #fff3cd; color: #997404; }
    .status-called { background: #0dcaf0; color: #055160; font-weight: 700; animation: pulse 2s infinite; }
    .status-in_progress { background: #d1e7dd; color: #0f5132; }
    .status-completed { background: #e9ecef; color: #495057; }
    .status-cancelled { background: #f8d7da; color: #842029; }

    @keyframes pulse {
        0%, 100% { opacity: 1; }
        50% { opacity: 0.7; }
    }

    .counter-alert {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 1.5rem;
        border-radius: 0.5rem;
        margin-bottom: 1rem;
        text-align: center;
        font-size: 1.25rem;
        font-weight: 600;
        animation: slideDown 0.5s ease-out;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }

    @keyframes slideDown {
        from { transform: translateY(-20px); opacity: 0; }
        to { transform: translateY(0); opacity: 1; }
    }

    .counter-alert .counter-number {
        font-size: 2rem;
        font-weight: 700;
        margin: 0.5rem 0;
    }

    .ticket-info {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 1rem;
        margin-bottom: 1rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #e9ecef;
    }

    .info-item {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .info-label {
        font-size: 0.75rem;
        color: #6c757d;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .info-value {
        font-size: 0.875rem;
        font-weight: 500;
        color: #212529;
    }

    .queue-stats {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 1rem;
        padding: 1rem;
        background: #f8f9fa;
        border-radius: 0.375rem;
    }

    .stat-item {
        text-align: center;
    }

    .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #212529;
    }

    .stat-label {
        font-size: 0.75rem;
        color: #6c757d;
        margin-top: 0.25rem;
    }

    .empty-state {
        text-align: center;
        padding: 3rem 1.5rem;
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
    }

    .empty-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
        opacity: 0.5;
    }

    .empty-state h3 {
        font-size: 1.125rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
    }

    .empty-state p {
        color: #6c757d;
        margin-bottom: 1.5rem;
    }

    .btn {
        display: inline-block;
        padding: 0.625rem 1.25rem;
        background: #212529;
        color: #fff;
        text-decoration: none;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        font-weight: 500;
        transition: all 0.2s;
    }

    .btn:hover {
        background: #000;
    }

    @media (max-width: 640px) {
        .ticket-info {
            grid-template-columns: 1fr;
        }

        .queue-stats {
            grid-template-columns: 1fr;
        }
    }
</style>
</head>
<body>
    <div class="header">
        <h1>Track Tickets</h1>
        <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="back-link">← Back</a>
    </div>

    <div class="container">
        <div class="page-title">
            <h2>My Tickets</h2>
            <p>Track your queue position and status</p>
        </div>

        <% if (myTickets == null || myTickets.isEmpty()) { %>
            <div class="empty-state">
                <h3>No Tickets Yet</h3>
                <p>Create a new ticket to get started</p>
                <a href="<%= request.getContextPath() %>/citizen/create-ticket.jsp" class="btn">Create Ticket</a>
            </div>
        <% } else { %>
            <div class="tickets-list">
                <% for (Ticket ticket : myTickets) { %>
                <%
                    String ticketStatus = ticket.getStatus();
                    if (ticketStatus == null || ticketStatus.isEmpty()) {
                        ticketStatus = "WAITING";
                    }
                    String serviceName = serviceNames.getOrDefault(ticket.getServiceId(), "Unknown Service");
                    String agencyName = agencyNames.getOrDefault(ticket.getAgencyId(), "Unknown Agency");
                %>
                <div class="ticket-card" data-ticket-id="<%= ticket.getId() %>">
                    <div class="ticket-header">
                        <div class="ticket-number"><%= ticket.getTicketNumber() %></div>
                        <div class="status-badge status-<%= ticketStatus.toLowerCase() %>">
                            <%= ticketStatus %>
                        </div>
                    </div>
                    
                    <div class="ticket-info">
                        <div class="info-item">
                            <span class="info-label">Service</span>
                            <span class="info-value"><%= serviceName %></span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Agency</span>
                            <span class="info-value"><%= agencyName %></span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Created</span>
                            <span class="info-value"><%= ticket.getCreatedAt() != null ? ticket.getCreatedAt().toString().substring(0, 16).replace("T", " ") : "N/A" %></span>
                        </div>
                    </div>

                    <% if ("WAITING".equals(ticketStatus)) { %>
                    <div class="queue-stats">
                        <div class="stat-item">
                            <div class="stat-value" id="position-<%= ticket.getId() %>"><%= ticket.getPosition() %></div>
                            <div class="stat-label">Position</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value" id="queue-<%= ticket.getId() %>">--</div>
                            <div class="stat-label">In Queue</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value" id="wait-<%= ticket.getId() %>">--</div>
                            <div class="stat-label">Est. Wait</div>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% } %>
            </div>
        <% } %>
    </div>

    <script>
        let ws;
        let countdownTimers = {}; // Store countdown intervals for each ticket
        let waitTimes = {}; // Store current wait times in minutes
        
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
                                updateTicketWaitTime(ticketData);
                            });
                        }
                        // Handle general queue updates
                        else if (data.action === 'queueUpdate') {
                            updateAllWaitTimes();
                        }
                        // Handle individual ticket status updates
                        else {
                            updateTicketStatus(data);
                        }
                    } catch (e) {
                        console.error('Error:', e);
                    }
                };
                
                ws.onclose = function() {
                    setTimeout(connectWebSocket, 3000);
                };
            } catch (error) {
                console.error('WebSocket failed:', error);
            }
        }
        
        // Start countdown timer for a ticket
        function startCountdown(ticketId, initialMinutes) {
            // Clear existing timer if any
            if (countdownTimers[ticketId]) {
                clearInterval(countdownTimers[ticketId]);
            }
            
            // Validate input - don't start countdown if data is invalid
            if (initialMinutes === undefined || initialMinutes === null || isNaN(initialMinutes) || initialMinutes < 0) {
                console.warn('Invalid initial minutes for ticket ' + ticketId + ':', initialMinutes);
                return;
            }
            
            // Check if we have a stored start time for this ticket
            const storageKey = 'ticket_' + ticketId + '_countdown';
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
                    waitTimes[ticketId] = newTotalSeconds;
                    localStorage.setItem(storageKey, JSON.stringify({
                        startTime: Date.now(),
                        initialSeconds: newTotalSeconds
                    }));
                } else {
                    // Continue with remaining time
                    waitTimes[ticketId] = remainingSeconds;
                }
            } else {
                // First time, store the start time
                const totalSeconds = initialMinutes * 60;
                waitTimes[ticketId] = totalSeconds;
                localStorage.setItem(storageKey, JSON.stringify({
                    startTime: Date.now(),
                    initialSeconds: totalSeconds
                }));
            }
            
            // Update display immediately
            updateCountdownDisplay(ticketId);
            
            // Start interval to countdown every second
            countdownTimers[ticketId] = setInterval(() => {
                if (waitTimes[ticketId] > 0) {
                    waitTimes[ticketId]--;
                    updateCountdownDisplay(ticketId);
                }
            }, 1000); // 1000ms = 1 second
        }
        
        // Update countdown display
        function updateCountdownDisplay(ticketId) {
            const waitEl = document.getElementById('wait-' + ticketId);
            if (waitEl) {
                const totalSeconds = waitTimes[ticketId];
                
                // Check if we have valid data
                if (totalSeconds === undefined || totalSeconds === null) {
                    waitEl.textContent = '--';
                    waitEl.style.color = '#6c757d';
                    return;
                }
                
                if (totalSeconds === 0) {
                    waitEl.textContent = 'Next!';
                    waitEl.style.color = '#198754';  // Green color
                    // Clear timer once we reach 0
                    if (countdownTimers[ticketId]) {
                        clearInterval(countdownTimers[ticketId]);
                        delete countdownTimers[ticketId];
                    }
                    // Clear from localStorage
                    localStorage.removeItem('ticket_' + ticketId + '_countdown');
                } else {
                    const minutes = Math.floor(totalSeconds / 60);
                    const seconds = totalSeconds % 60;
                    // Pad seconds with leading zero
                    const paddedSeconds = seconds < 10 ? '0' + seconds : seconds;
                    waitEl.textContent = '~' + minutes + 'm ' + paddedSeconds + 's';
                    waitEl.style.color = '';  // Reset color
                }
            }
        }
        
        // Stop countdown for a ticket (when status changes)
        function stopCountdown(ticketId) {
            if (countdownTimers[ticketId]) {
                clearInterval(countdownTimers[ticketId]);
                delete countdownTimers[ticketId];
                delete waitTimes[ticketId];
            }
            // Clear from localStorage
            localStorage.removeItem('ticket_' + ticketId + '_countdown');
        }
        
        // Update the ticket display when we get new data
        function updateTicketStatus(data) {
            const ticketCards = document.querySelectorAll('.ticket-card');
            
            ticketCards.forEach(card => {
                const ticketNumber = card.querySelector('.ticket-number').textContent.trim();
                
                // Is this the ticket that was updated?
                if (data.ticketNumber === ticketNumber) {
                    // Update the status badge
                    const statusBadge = card.querySelector('.status-badge');
                    statusBadge.className = 'status-badge status-' + data.status.toLowerCase();
                    statusBadge.textContent = data.status;
                    
                    // Hide queue stats if no longer waiting
                    if (data.status !== 'WAITING') {
                        const queueStats = card.querySelector('.queue-stats');
                        if (queueStats) {
                            queueStats.style.display = 'none';
                        }
                        // Stop countdown when status changes from WAITING
                        const ticketId = card.getAttribute('data-ticket-id');
                        if (ticketId) {
                            stopCountdown(ticketId);
                        }
                    }
                    
                    // Show notification when ticket is called or being served
                    if (data.status === 'CALLED') {
                        const counterInfo = data.counterId ? ' at Counter #' + data.counterId : '';
                        showNotification('Your turn! Please proceed to the designated counter' + counterInfo);
                        playNotificationSound();
                        
                        // Add prominent counter alert to the card
                        addCounterAlert(card, data.counterId);
                    } else if (data.status === 'IN_PROGRESS') {
                        const counterInfo = data.counterId ? ' at Counter #' + data.counterId : '';
                        showNotification('Ticket ' + ticketNumber + ' is now being served' + counterInfo);
                        playNotificationSound();
                        
                        // Add counter alert if not already present
                        addCounterAlert(card, data.counterId);
                    }
                }
            });
        }
        
        // Add counter alert to ticket card
        function addCounterAlert(card, counterId) {
            // Check if alert already exists
            if (card.querySelector('.counter-alert')) {
                return;
            }
            
            const counterNumber = counterId || 'N/A';
            const alertDiv = document.createElement('div');
            alertDiv.className = 'counter-alert';
            alertDiv.innerHTML = `
                <div>YOUR TURN</div>
                <div class="counter-number">Counter #${counterNumber}</div>
                <div>Please proceed to the designated counter</div>
            `;
            
            // Insert at the beginning of the card
            card.insertBefore(alertDiv, card.firstChild);
        }
        
        // Update wait time for a specific ticket from WebSocket data
        function updateTicketWaitTime(ticketData) {
            const ticketCards = document.querySelectorAll('.ticket-card');
            
            ticketCards.forEach(card => {
                const ticketNumber = card.querySelector('.ticket-number').textContent.trim();
                
                if (ticketData.ticketNumber === ticketNumber) {
                    const ticketId = card.getAttribute('data-ticket-id');
                    
                    // Update position
                    const positionEl = document.getElementById('position-' + ticketId);
                    if (positionEl) {
                        positionEl.textContent = ticketData.position;
                    }
                    
                    // Update queue count
                    const queueEl = document.getElementById('queue-' + ticketId);
                    if (queueEl) {
                        queueEl.textContent = (ticketData.position + 1);
                    }
                    
                    // Start countdown with new time
                    if (ticketId) {
                        startCountdown(ticketId, ticketData.estimatedWaitMinutes);
                    }
                }
            });
        }
        
        // Update wait times for all WAITING tickets
        function updateAllWaitTimes() {
            const ticketCards = document.querySelectorAll('.ticket-card');
            
            ticketCards.forEach(card => {
                const ticketNumber = card.querySelector('.ticket-number').textContent.trim();
                const statusBadge = card.querySelector('.status-badge');
                
                // Only update if ticket is still WAITING
                if (statusBadge && statusBadge.textContent.trim() === 'WAITING') {
                    fetchWaitTime(ticketNumber, card);
                }
            });
        }
        
        // Fetch wait time from server for a specific ticket
        function fetchWaitTime(ticketNumber, card) {
            fetch('<%= request.getContextPath() %>/citizen/GetWaitTimeServlet?ticketNumber=' + ticketNumber)
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        console.error('Error:', data.error);
                        return;
                    }
                    
                    // Get the ticket ID from card
                    const ticketId = card.getAttribute('data-ticket-id');
                    
                    // Update position
                    const positionEl = document.getElementById('position-' + ticketId);
                    if (positionEl) {
                        positionEl.textContent = data.position;
                    }
                    
                    // Update "in queue" count (position + 1 because 0 means next in line)
                    const queueEl = document.getElementById('queue-' + ticketId);
                    if (queueEl) {
                        queueEl.textContent = (data.position + 1);
                    }
                    
                    // Update estimated wait time and start countdown
                    const waitEl = document.getElementById('wait-' + ticketId);
                    if (waitEl && ticketId) {
                        const estimatedMinutes = data.estimatedWaitMinutes;
                        
                        // Start countdown timer
                        startCountdown(ticketId, estimatedMinutes);
                    }
                })
                .catch(error => {
                    console.error('Failed to fetch wait time:', error);
                });
        }
        
        // Show browser notification
        function showNotification(message) {
            if (!("Notification" in window)) {
                alert(message);
                return;
            }
            
            if (Notification.permission === "granted") {
                new Notification("Queue Update", { body: message });
            } else if (Notification.permission !== "denied") {
                Notification.requestPermission().then(function(permission) {
                    if (permission === "granted") {
                        new Notification("Queue Update", { body: message });
                    }
                });
            }
        }
        
        // Play a simple beep sound
        function playNotificationSound() {
            try {
                const audioContext = new (window.AudioContext || window.webkitAudioContext)();
                const oscillator = audioContext.createOscillator();
                const gainNode = audioContext.createGain();
                
                oscillator.connect(gainNode);
                gainNode.connect(audioContext.destination);
                
                oscillator.frequency.value = 800;
                oscillator.type = 'sine';
                
                gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
                gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
                
                oscillator.start(audioContext.currentTime);
                oscillator.stop(audioContext.currentTime + 0.5);
            } catch (e) {
                console.log('Could not play sound');
            }
        }
        
        // Ask permission for notifications
        if ("Notification" in window && Notification.permission === "default") {
            Notification.requestPermission();
        }
        
        // Start the connection when page loads
        connectWebSocket();
        
        // Load initial wait times for all WAITING tickets
        setTimeout(function() {
            updateAllWaitTimes();
        }, 1000);  // Wait 1 second for WebSocket to connect
    </script>
</body>
</html>
