@echo off
echo 🧹 Dọn dẹp các file .class cũ...

REM Xóa file .class ở root
if exist *.class del /q *.class

REM Xóa file .class trong các thư mục
for /d %%d in (config dao dto exception model service ui utils) do (
    if exist %%d\*.class del /q %%d\*.class
)

REM Xóa file .class trong src và subdirectories  
for /r src %%f in (*.class) do del "%%f"

echo ✅ Đã xóa tất cả file .class!

echo 🔨 Compile project...
if not exist build mkdir build

REM Compile với classpath đúng
javac -cp "lib\mysql-connector-j-8.4.0.jar" -d build -sourcepath src src\*.java src\config\*.java src\dao\*.java src\dao\impl\*.java src\dto\*.java src\exception\*.java src\model\*.java src\service\*.java src\ui\*.java src\utils\*.java

if %errorlevel% equ 0 (
    echo ✅ Compile thành công!
    echo 🚀 Chạy ứng dụng...
    java -cp "build;lib\mysql-connector-j-8.4.0.jar" Main
) else (
    echo ❌ Compile thất bại!
    pause
)
