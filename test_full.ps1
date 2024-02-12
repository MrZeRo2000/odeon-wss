Write-Host ******** INIT DB ******************
& .\db_init.ps1
Write-Host ******** INIT DB COMPLETED ********

Write-Host ******** DELETE TEST DB ***********
& .\del_test_db.ps1
Write-Host ******** DELETE TEST DB COMPLETED *

Write-Host ******** TEST RUN *****************
.\gradlew clean build test
Write-Host ******** TEST RUN COMPLETED *******