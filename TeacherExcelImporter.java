import java.io.*;
import java.sql.*;
import java.util.Random;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TeacherExcelImporter {
    public void importFromExcel(String excelPath) {
        try (
            FileInputStream fis = new FileInputStream(new File(excelPath));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/onlineexam", "root", "Bhuvana@4000"
            )
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = row.getCell(0).getStringCellValue();
                String dept = row.getCell(1).getStringCellValue();
                String email = row.getCell(2).getStringCellValue();
                
                String insertTeacher = "INSERT INTO teachers (name, email, department) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(insertTeacher, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, name);
                pst.setString(2, email);

                pst.setString(3, dept);
                pst.executeUpdate();

                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int teacherId = rs.getInt(1);
                    String loginId = "TCH" + String.format("%03d", teacherId);
                    String password = generateRandomPassword(8);

                    String insertLogin = "INSERT INTO teacher_logins (login_id, teacher_id, password) VALUES (?, ?, ?)";
                    PreparedStatement pstLogin = conn.prepareStatement(insertLogin);
                    pstLogin.setString(1, loginId);
                    pstLogin.setInt(2, teacherId);
                    pstLogin.setString(3, password);
                    pstLogin.executeUpdate();

                    System.out.println("Login generated for " + name + ": " + loginId + ", Password: " + password);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String excelPath = "Teachers.xlsx"; // Your teacher Excel file
        new TeacherExcelImporter().importFromExcel(excelPath);
    }
}


//java -cp "C:\Users\Bhuvanashri SK\Desktop\Online exam\libs\*;." TeacherExcelImporter.java 
