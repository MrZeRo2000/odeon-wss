# Test execute FFMPEG for MP3
D:\prj\ffmpeg\ffprobe.exe -print_format json -show_format -show_streams -v quiet "D:\temp\ok\MP3 Music\Aerosmith\2004 Honkin'On Bobo\01 - Road Runner.mp3"  

# Set env C
$env:java_home = 'C:/WinApp/jdk-12.0.2'

# Deploy Int C
./gradlew clean build deployInt -x test -Ptomcat_home=D:/prj/apache-tomcat-9.0.24

# Set env D
$env:java_home = 'D:/WinApp/jdk-12.0.2'

# Deploy Int D
./gradlew clean build deployInt -x test -Ptomcat_home=D:/prj/apache-tomcat-9.0.62

# Run one test to create DEV database
./gradlew test --tests *OdeonWssApplicationTests*

# Set node
$env:Path += ";C:\Users\r1525\AppData\Local\Programs\node"