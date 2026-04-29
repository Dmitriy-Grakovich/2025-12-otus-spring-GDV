# PowerShell script to start both microservices

Write-Host "Starting OrderItem Service..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\orderitem-service'; mvn spring-boot:run"

Write-Host "Waiting 10 seconds for OrderItem Service to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "Starting Order Service..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\tasklesson23'; mvn spring-boot:run"

Write-Host ""
Write-Host "Services are starting..." -ForegroundColor Cyan
Write-Host "OrderItem Service: http://localhost:8081" -ForegroundColor Cyan
Write-Host "Order Service: http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
