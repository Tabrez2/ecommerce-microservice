# 🚀 Microservices Startup Guide

## 📋 Prerequisites Check
Before starting any services, ensure these infrastructure components are running:

### ✅ Infrastructure Services (Must be running first)
1. **MongoDB** (port 27017) - ✅ Running
2. **PostgreSQL** (port 5432) - ✅ Running  
3. **Kafka** (port 9092) - ✅ Running

## 🎯 Service Startup Order

**IMPORTANT:** Start services in this specific order due to dependencies:

### Step 1: Start Core Infrastructure Services
```bash
# 1. Config Server (Port 8888)
cd services/config-server
.\mvnw.cmd spring-boot:run

# 2. Discovery Service/Eureka (Port 8761)
cd services/discovery
.\mvnw.cmd spring-boot:run
```

### Step 2: Start Business Services
```bash
# 3. Customer Service (Port 8090)
cd services/customer
.\mvnw.cmd spring-boot:run

# 4. Product Service (Port 8050)
cd services/product
.\mvnw.cmd spring-boot:run

# 5. Order Service (Port 8070)
cd services/order
.\mvnw.cmd spring-boot:run
```

### Step 3: Start API Gateway
```bash
# 6. Gateway Service (Port 8222)
cd services/gateway
.\mvnw.cmd spring-boot:run
```

## 🔍 Service Health Checks

After starting each service, verify it's running:

### Config Server
```bash
curl http://localhost:8888/customer-service/default
# Should return 200 OK with configuration
```

### Discovery Service (Eureka)
```bash
curl http://localhost:8761
# Should show Eureka dashboard with registered services
```

### Customer Service
```bash
curl http://localhost:8090/api/v1/customer
# Should return 200 OK (empty array if no customers)
```

### Product Service
```bash
curl http://localhost:8050/api/v1/product
# Should return product data
```

### Order Service
```bash
curl http://localhost:8070/api/v1/orders
# Should return order data
```

### Gateway Service
```bash
curl http://localhost:8222/api/v1/customer
# Should proxy to customer service
```

## 🚨 Common Issues & Solutions

### 1. Port Already in Use
**Error:** "Web server failed to start. Port XXXX was already in use."

**Solution:**
```bash
# Find process using port
netstat -aon | findstr ":XXXX"
# Kill the process
taskkill /F /PID <PID>
```

### 2. MongoDB Authentication Error
**Error:** "Command find requires authentication"

**Solution:** Ensure MongoDB user exists:
```javascript
// In mongosh
use admin
db.createUser({
  user: "tabrez",
  pwd: "tabrez",
  roles: ["root"]
})
```

### 3. PostgreSQL Connection Issues
**Error:** "Connection to localhost:5432 refused"

**Solution:** Ensure PostgreSQL is running and databases exist:
```sql
-- Create databases if they don't exist
CREATE DATABASE customer;
CREATE DATABASE product;
CREATE DATABASE "order";
```

### 4. Service Registration Issues
**Error:** "Cannot execute request on any known server"

**Solution:** Ensure Discovery Service is running before starting other services

## 📊 Service Dependencies

```
Config Server (8888) ← All services depend on this
     ↓
Discovery Service (8761) ← All services register here
     ↓
Customer Service (8090) ← Gateway routes here
Product Service (8050) ← Gateway routes here  
Order Service (8070) ← Gateway routes here
     ↓
Gateway Service (8222) ← Entry point for all API calls
```

## 🎯 Quick Status Check Script

Save this as `check-status.ps1`:

```powershell
Write-Host "🔍 Checking Microservices Status..." -ForegroundColor Green

$services = @(
    @{Name="Config Server"; Port=8888; Path="/customer-service/default"},
    @{Name="Discovery Service"; Port=8761; Path="/"},
    @{Name="Customer Service"; Port=8090; Path="/api/v1/customer"},
    @{Name="Product Service"; Port=8050; Path="/api/v1/product"},
    @{Name="Order Service"; Port=8070; Path="/api/v1/orders"},
    @{Name="Gateway Service"; Port=8222; Path="/api/v1/customer"}
)

foreach ($service in $services) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($service.Port)$($service.Path)" -UseBasicParsing -TimeoutSec 5
        Write-Host "✅ $($service.Name) - RUNNING (Port $($service.Port))" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ $($service.Name) - DOWN (Port $($service.Port))" -ForegroundColor Red
    }
}
```

## 🎉 Success Criteria

Your microservices architecture is fully operational when:
- ✅ All 6 services are running without errors
- ✅ Config Server serves all configurations
- ✅ All services are registered in Eureka
- ✅ Gateway successfully routes requests to all services
- ✅ No port conflicts or connection errors

## 📝 Notes

- **Always start services in dependency order**
- **Wait for each service to fully start before starting the next**
- **Check logs for any startup errors**
- **Use Eureka dashboard to verify service registration**
- **Test API endpoints through Gateway (port 8222) for full integration**