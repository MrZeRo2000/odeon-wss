cmd /c gradlew test --tests *OdeonWssApplicationTests*
xcopy db\database\odeon-test.db db\database\odeon.db /Y
