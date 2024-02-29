./gradlew.ps1 test --tests *OdeonWssApplicationTests*
xcopy.exe db\database\odeon-test.db db\database\odeon-test-01.db /Y
xcopy db\database\odeon-test.db db\database\odeon-test-02.db /Y
xcopy db\database\odeon-test.db db\database\odeon-test-03.db /Y
xcopy db\database\odeon-test.db db\database\odeon-test-04.db /Y
xcopy db\database\odeon-test.db db\database\odeon-test-05.db /Y
xcopy db\database\odeon-test.db db\database\odeon-test-06.db /Y
xcopy db\database\odeon-test.db db\database\odeon-test-07.db /Y