package com.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/BankingLoginServlet")
public class BankingLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");

        try {
            
            // Establish connection to the database   // Load the Oracle JDBC driver
        	
            Connection conn = DBConnection.get();
            // Prepare SQL statement to check user credentials
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, pwd);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // User exists, set session and redirect to welcome page
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Login successful');");
                out.println("window.location.href = 'createaccount.html';");
                out.println("</script>");
            } else {
                // User does not exist, redirect to login page
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Login unsuccessful. Please try again.');");
                out.println("window.location.href = 'login.html';");
                out.println("</script>");
            }

            // Close resources
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }

        out.close();
    }
}

