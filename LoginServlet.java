import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/student_portal", "root", "Bhava@0503");

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Store session for progress tracking later
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("cam.html"); // success
            } else {
                PrintWriter out = response.getWriter();
                out.println("<script>alert('Invalid credentials'); window.location='login.html';</script>");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
