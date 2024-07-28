package com.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CreateAccount
 */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            // If the session is null or the user is not logged in, redirect to login page
            response.sendRedirect("login.html");
            return;
        }

        // Get the account details from the form
        String accountNumber = request.getParameter("anum");
        String accountName = request.getParameter("aname");
        String age = request.getParameter("age");
        String aadharNumber = request.getParameter("adhar");
        String PanNumber = request.getParameter("pan");
        String mobileNumber = request.getParameter("mobile");
        String address = request.getParameter("address");
        String balance = request.getParameter("abal");

        // JDBC connection
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSuccess = false;

        try {
        	conn = DBConnection.get();

            // Prepare SQL query
            String sql = "INSERT INTO accounts (account_number, account_name, age, aadhar_number,pancard_number,mobile_number, address, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountName);
            pstmt.setInt(3, Integer.parseInt(age));
            pstmt.setString(4, aadharNumber);
            pstmt.setString(5, PanNumber);
            pstmt.setString(6, mobileNumber);
            pstmt.setString(7, address);
            pstmt.setDouble(8, Double.parseDouble(balance));

            // Execute update
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isSuccess = true;
            }

        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (isSuccess) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Account Created Successfully');");
            out.println("window.location.href = 'createaccount.html';");
            out.println("</script>");
        } else {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Account Creation Failed');");
            out.println("window.location.href = 'createaccount.html';");
            out.println("</script>");
        }
	}

}
