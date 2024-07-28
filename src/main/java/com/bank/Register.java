package com.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Register")
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("uname");
        String password = request.getParameter("pwd");
        String confirmPassword = request.getParameter("cnfm");

        if (!password.equals(confirmPassword)) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Password and Confirm Password do not match');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
        } else {
            try {
                
                // Establish connection to the database   // Load the Oracle JDBC driver
                Connection conn = DBConnection.get();

                // Prepare SQL statement to insert user details
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);

                int result = ps.executeUpdate();

                if (result > 0) {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Registration successful');");
                    out.println("window.location.href = 'login.html';");
                    out.println("</script>");
                } else {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Registration failed. Please try again.');");
                    out.println("window.location.href = 'register.html';");
                    out.println("</script>");
                }

                // Close resources
                ps.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println("Error: " + e.getMessage());
            }
        }

        out.close();
    }
}

