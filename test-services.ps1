# PowerShell script to test microservices

Write-Host "Testing Microservices Communication" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""

# Test OrderItem Service Health
Write-Host "1. Testing OrderItem Service Health..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method Get
    Write-Host "   Status: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "   Error: OrderItem Service is not running!" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Test Order Service Health
Write-Host "2. Testing Order Service Health..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
    Write-Host "   Status: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "   Error: Order Service is not running!" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Create OrderItem directly
Write-Host "3. Creating OrderItem directly in OrderItem Service..." -ForegroundColor Cyan
$itemBody = @{
    productId = "prod-999"
    productName = "Direct Test Product"
    quantity = 5
    price = 25.0
} | ConvertTo-Json

try {
    $item = Invoke-RestMethod -Uri "http://localhost:8081/api/order-items" -Method Post -Body $itemBody -ContentType "application/json"
    Write-Host "   Created OrderItem ID: $($item.id)" -ForegroundColor Green
    Write-Host "   Total Price: $($item.totalPrice)" -ForegroundColor Green
} catch {
    Write-Host "   Error creating OrderItem: $_" -ForegroundColor Red
}

Write-Host ""

# Test Order creation (which calls OrderItem Service)
Write-Host "4. Creating Order (tests REST communication)..." -ForegroundColor Cyan
$orderBody = @{
    customerId = "cust-test-001"
    items = @(
        @{
            productId = "prod-123"
            productName = "Test Product via Order"
            quantity = 3
            price = 100.0
        }
    )
    totalAmount = 300.0
    shippingAddress = "Test Address, 123"
} | ConvertTo-Json -Depth 10

try {
    $order = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Body $orderBody -ContentType "application/json"
    Write-Host "   Created Order ID: $($order.orderId)" -ForegroundColor Green
    Write-Host "   Customer ID: $($order.customerId)" -ForegroundColor Green
    Write-Host "   Items Count: $($order.items.Count)" -ForegroundColor Green
    Write-Host "   First Item ID: $($order.items[0].id)" -ForegroundColor Green
    Write-Host "   First Item Total: $($order.items[0].totalPrice)" -ForegroundColor Green
} catch {
    Write-Host "   Error creating Order: $_" -ForegroundColor Red
}

Write-Host ""

# Test Order test endpoint
Write-Host "5. Testing Order test endpoint..." -ForegroundColor Cyan
try {
    $testOrder = Invoke-RestMethod -Uri "http://localhost:8080/api/orders/test" -Method Get
    Write-Host "   Test Order ID: $($testOrder.orderId)" -ForegroundColor Green
    Write-Host "   Test Order Items: $($testOrder.items.Count)" -ForegroundColor Green
} catch {
    Write-Host "   Error testing Order endpoint: $_" -ForegroundColor Red
}

Write-Host ""

# Get OrderItem statistics
Write-Host "6. Getting OrderItem Service statistics..." -ForegroundColor Cyan
try {
    $stats = Invoke-RestMethod -Uri "http://localhost:8081/api/order-items/stats" -Method Get
    Write-Host "   Total Items: $($stats.totalItems)" -ForegroundColor Green
    Write-Host "   Total Quantity: $($stats.totalQuantity)" -ForegroundColor Green
    Write-Host "   Total Value: $($stats.totalValue)" -ForegroundColor Green
    Write-Host "   Average Price: $($stats.averagePrice)" -ForegroundColor Green
} catch {
    Write-Host "   Error getting statistics: $_" -ForegroundColor Red
}

Write-Host ""

# Search for items
Write-Host "7. Searching for 'iPhone' items..." -ForegroundColor Cyan
try {
    $searchResults = Invoke-RestMethod -Uri "http://localhost:8081/api/order-items/search?name=iPhone" -Method Get
    Write-Host "   Found $($searchResults.Count) items" -ForegroundColor Green
    if ($searchResults.Count -gt 0) {
        Write-Host "   First result: $($searchResults[0].productName)" -ForegroundColor Green
    }
} catch {
    Write-Host "   Error searching: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "====================================" -ForegroundColor Green
Write-Host "Testing Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
