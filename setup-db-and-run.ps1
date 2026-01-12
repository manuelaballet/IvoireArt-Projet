<#
Automates: execute db_init.sql, set env vars and run the Spring Boot app.
Usage (PowerShell):
  .\setup-db-and-run.ps1
#>

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$dbFile = Join-Path $projectRoot 'db_init.sql'

if (-not (Test-Path $dbFile)) {
    Write-Error "db_init.sql not found at $dbFile"
    exit 1
}

# Default credentials (override via env vars before running script)
$rootUser = 'root'
$rootPass = $env:DB_ROOT_PASS
if ([string]::IsNullOrEmpty($rootPass)) { $rootPass = 'MySQL@Secure2026!A9' }
$dbUser = 'ivoire_user'
$dbPass = $env:DB_PASSWORD
if ([string]::IsNullOrEmpty($dbPass)) { $dbPass = 'MySQL@Secure2026!A9' }

# Locate mysql.exe
$mysqlCmd = Get-Command mysql -ErrorAction SilentlyContinue
if ($null -eq $mysqlCmd) {
    $possible = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
    )
    foreach ($p in $possible) { if (Test-Path $p) { $mysqlCmd = $p; break } }
}

if ($null -eq $mysqlCmd) {
    Write-Error "mysql client not found. Please install MySQL client or add it to PATH."
    exit 1
}

Write-Host "Using mysql: $mysqlCmd"

# Execute the SQL file using root credentials. We use -pPASSWORD (no space) to avoid interactive prompt.
try {
    $sourceCmd = "SOURCE $dbFile;"
    Write-Host "Executing SQL initialization (this may ask for permissions)..."
    & $mysqlCmd -u $rootUser -p$rootPass -e $sourceCmd
    Write-Host "SQL initialization completed."
} catch {
    Write-Error "Failed to execute db_init.sql: $_"
    exit 1
}

# Export environment variables for Spring Boot
Write-Host "Setting environment variables for Spring Boot run (temporary for this session)..."
$env:DB_USER = $dbUser
$env:DB_PASSWORD = $dbPass

# Run Maven (prefer project mvnw if present)
Set-Location $projectRoot
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
$mvnw = Join-Path $projectRoot 'mvnw.cmd'

if (Test-Path $mvnw) {
    Write-Host "Using project mvnw"
    $runner = $mvnw
} elseif ($null -ne $mvnCmd) {
    Write-Host "Using system mvn"
    $runner = 'mvn'
} else {
    Write-Error "Maven not found. Install Maven or add a Maven wrapper (mvnw)."
    exit 1
}

Write-Host "Starting Spring Boot application (logs follow)..."
# Start and stream logs
& $runner 'spring-boot:run'

# End of script
Write-Host "Script finished."