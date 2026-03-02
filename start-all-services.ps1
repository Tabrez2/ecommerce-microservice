# 🚀 Microservices Startup Script
# Run this script to start all microservices in the correct order

Write-Host "🚀 Starting Microservices Architecture..." -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Yellow

# Function to check if a port is in use
function Test-Port {
    param($Port)
    $tcpConnection = Test-NetConnection -ComputerName localhost -Port $Port -WarningAction SilentlyContinue
    return $tcpConnection.TcpTestSucceeded
}

# Function to start a service
function Start-Service {
    param($ServiceName, $ServicePath, $Port)
    
    Write-Host "`n🔧 Starting $ServiceName (Port $Port)..." -ForegroundColor Cyan
    
    if (Test-Port $Port) {
        Write-Host "⚠️  Port $Port is already in use. $ServiceName may already be running." -ForegroundColor Yellow
        return $true
    }
    
    try {
        $process = Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd /d `"$ServicePath`" && .\mvnw.cmd spring-boot:run" -PassThru -WindowStyle Hidden
        Write-Host "✅ $ServiceName started successfully (PID: $($process.Id))" -ForegroundColor Green
        
        # Wait a bit for service to initialize
        Start-Sleep -Seconds 10
        return $true
    }
    catch {
        Write-Host "❌ Failed to start $ServiceName : $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to wait for service to be ready
function Wait-ForService {
    param($ServiceName, $Port, $Path = "", $Timeout = 60)
    
    Write-Host "⏳ Waiting for $ServiceName to be ready..." -ForegroundColor Blue
    $startTime = Get-Date
    
    while (((Get-Date) - $startTime).TotalSeconds -lt $Timeout) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port$Path" -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -eq 200) {
                Write-Host "✅ $ServiceName is ready!" -ForegroundColor Green
                return $true
            }
        }
        catch {
            # Service not ready yet, continue waiting
        }
        Start-Sleep -Seconds 2
    }
    
    Write-Host "❌ $ServiceName failed to become ready within $Timeout seconds" -ForegroundColor Red
    return $false
}

# Main startup sequence
$basePath = Split-Path -Parent $PSScriptRoot
$success = $true

# Step 1: Start Config Server
$configPath = "$basePath\services\config-server"
if (Start-Service -ServiceName "Config Server" -ServicePath $configPath -Port 8888) {
    if (-not (Wait-ForService -ServiceName "Config Server" -Port 8888 -Path "/customer-service/default")) {
        $success = $false
    }
} else {
    $success = $false
}

if (-not $success) {
    Write-Host "❌ Startup failed at Config Server. Exiting..." -ForegroundColor Red
    exit 1
}

# Step 2: Start Discovery Service
$discoveryPath = "$basePath\services\discovery"
if (Start-Service -ServiceName "Discovery Service" -ServicePath $discoveryPath -Port 8761) {
    if (-not (Wait-ForService -ServiceName "Discovery Service" -Port 8761)) {
        $success = $false
    }
} else {
    $success = $false
}

if (-not $success) {
    Write-Host "❌ Startup failed at Discovery Service. Exiting..." -ForegroundColor Red
    exit 1
}

# Step 3: Start Business Services
$services = @(
    @{Name="Customer Service"; Path="$basePath\services\customer"; Port=8090; HealthPath="/api/v1/customer"},
    @{Name="Product Service"; Path="$basePath\services\product"; Port=8050; HealthPath="/api/v1/product"},
    @{Name="Order Service"; Path="$basePath\services\order"; Port=8070; HealthPath="/api/v1/orders"}
)

foreach ($service in $services) {
    if (Start-Service -ServiceName $service.Name -ServicePath $service.Path -Port $service.Port) {
        if (-not (Wait-ForService -ServiceName $service.Name -Port $service.Port -Path $service.HealthPath)) {
            Write-Host "⚠️  $($service.Name) may still be starting..." -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠️  Continuing with other services..." -ForegroundColor Yellow
    }
}

# Step 4: Start Gateway Service
$gatewayPath = "$basePath\services\gateway"
if (Start-Service -ServiceName "Gateway Service" -ServicePath $gatewayPath -Port 8222) {
    if (-not (Wait-ForService -ServiceName "Gateway Service" -Port 8222 -Path "/api/v1/customer")) {
        Write-Host "⚠️  Gateway Service may still be starting..." -ForegroundColor Yellow
    }
}

Write-Host "`n=========================================" -ForegroundColor Yellow
Write-Host "🎉 Microservices startup sequence completed!" -ForegroundColor Green
Write-Host "`n📊 Service Status:" -ForegroundColor Cyan

# Final status check
$finalServices = @(
    @{Name="Config Server"; Port=8888; Path="/customer-service/default"},
    @{Name="Discovery Service"; Port=8761; Path="/"},
    @{Name="Customer Service"; Port=8090; Path="/api/v1/customer"},
    @{Name="Product Service"; Port=8050; Path="/api/v1/product"},
    @{Name="Order Service"; Port=8070; Path="/api/v1/orders"},
    @{Name="Gateway Service"; Port=8222; Path="/api/v1/customer"}
)

foreach ($service in $finalServices) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($service.Port)$($service.Path)" -UseBasicParsing -TimeoutSec 5
        Write-Host "✅ $($service.Name) - RUNNING (Port $($service.Port))" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ $($service.Name) - DOWN (Port $($service.Port))" -ForegroundColor Red
    }
}

Write-Host "`n🔗 Access Points:" -ForegroundColor Cyan
Write-Host "• Config Server: http://localhost:8888" -ForegroundColor White
Write-Host "• Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "• API Gateway: http://localhost:8222" -ForegroundColor White
Write-Host "• Customer Service: http://localhost:8090/api/v1/customer" -ForegroundColor White
Write-Host "• Product Service: http://localhost:8050/api/v1/product" -ForegroundColor White
Write-Host "• Order Service: http://localhost:8070/api/v1/orders" -ForegroundColor White

Write-Host "`n💡 Use 'Get-Process java' to see all running Java processes" -ForegroundColor Gray
Write-Host "💡 Use 'Stop-Process -Name java' to stop all Java processes" -ForegroundColor Gray