@echo off
echo 🔨 Compile Java project...

REM Tạo thư mục build nếu chưa có
if not exist build mkdir build

REM Compile tất cả file .java với classpath đúng
javac -cp "lib\mysql-connector-j-8.4.0.jar" -d build -sourcepath src src\*.java src\config\*.java src\dao\*.java src\dao\impl\*.java src\dto\*.java src\exception\*.java src\model\*.java src\service\*.java src\ui\*.java src\utils\*.java

if %errorlevel% equ 0 (
    echo ✅ Compile thành công! 
    echo 📁 File .class được lưu trong thư mục build/
) else (
    echo ❌ Compile thất bại! Kiểm tra lỗi phía trên.
)

pause
