param (
    [string]$ScanPath = $(throw "ScanPath parameter is required"),
    [string]$OutputPath = $(throw "OutputPath parameter is required")
)

$extensions = 'AVI', 'M4V', 'MKV', 'MP4', 'MPG', 'WMV', 'VOB'
Write-Host $extensions

function GetMediaInifo {
    param(
        [string]$Name,
        [string]$FullName,
        [string]$OutputPath
    )
    Write-Host "Processing $name"
}

function ScanMedia {
    param (
        [Parameter(mandatory=$true)][string]$ScanPath,
        [Parameter(mandatory=$true)][string]$OutputPath
    )
    Write-Host "ScanMedia function ScanPath=$ScanPath, OutputPath=$OutputPath"

    $includeExtensions = $extensions | ForEach-Object {"*.$($_.ToLower())"}
    Write-Host "Include extensions: $includeExtensions"

    $files = Get-ChildItem -Path $ScanPath -include ($includeExtensions) -Recurse:$true -File:$true | Select-Object -Property Name, FullName

    New-Item -ItemType Directory -Force -Path $OutputPath

    $files | ForEach-Object -Process {
        $command = "../MediaInfo/MediaInfo.exe --Output=JSON `"$($_.FullName)`" > `"$OutputPath/$($_.Name).json`""
        Write-Host $command -ForegroundColor DarkGray
        Invoke-Expression -Command $command
    }
}

ScanMedia -ScanPath $ScanPath -OutputPath $OutputPath
