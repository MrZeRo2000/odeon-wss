$Folder = "$env:TEMP\odeon-wss-db"

if (Test-Path -Path $Folder)
{
    Remove-Item "$Folder\*"
}