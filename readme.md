# Test execute FFMPEG for MP3
`..\ffmpeg\ffprobe.exe -print_format json -show_format -show_streams -v quiet "..\odeon-test-data\ok\MP3 Music\Aerosmith\2004 Honkin'On Bobo\01 - Road Runner.mp3"`

# Set env Work
`$env:java_home = '%LOCALAPPDATA%\Programs\jdk-17.0.4.1\'`

# Set env Home
`$env:java_home = 'D:/WinApp/jdk-17.0.4.1'`

# Deploy Int
`./gradlew clean build deployInt -x test`

# Run one test to create DEV database
`./gradlew test --tests *OdeonWssApplicationTests*`

# Set node
`$env:Path += ";$env:LOCALAPPDATA\Programs\node"`

# Run full tests environment variable
`spring_profiles_active=full-tests`