@ECHO ******** INIT DB ******************
CALL db_init.bat
@ECHO ******** INIT DB COMPLETED ********

@ECHO ******** DELETE TEST DB ***********
CALL del_test_db.bat
@ECHO ******** DELETE TEST DB COMPLETED *

@ECHO ******** TEST RUN *****************
gradlew clean build test
@ECHO ******** TEST RUN COMPLETED *******