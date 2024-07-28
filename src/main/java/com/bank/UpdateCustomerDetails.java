package com.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UpdateCustomerDetails
 */
@WebServlet("/UpdateCustomerDetails")
public class UpdateCustomerDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Check for user session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            // If the session is null or the user is not logged in, redirect to login page
            response.sendRedirect("login.html");
            return;
        }

        // Get form data
        String accNo = request.getParameter("accno");
        String name = request.getParameter("name");
        String aadhaar = request.getParameter("aadhaar");
        String pan = request.getParameter("pan");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;

        try {
            // Get database connection
            conn = DBConnection.get();

            // Check if account exists
            String checkAccountSql = "SELECT * FROM accounts WHERE account_number = ?";
            pstmt = conn.prepareStatement(checkAccountSql);
            pstmt.setString(1, accNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Account exists, update details
                String updateDetailsSql = "UPDATE accounts SET account_name = ?, aadhar_number = ?, pancard_number = ?, mobile_number = ?, address = ? WHERE account_number = ?";
                pstmt1 = conn.prepareStatement(updateDetailsSql);
                pstmt1.setString(1, name);
                pstmt1.setString(2, aadhaar);
                pstmt1.setString(3, pan);
                pstmt1.setString(4, phone);
                pstmt1.setString(5, address);
                pstmt1.setString(6, accNo);

                int rowsUpdated = pstmt1.executeUpdate();
                if (rowsUpdated > 0) {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Details updated successfully');");
                    out.println("window.location.href = 'nameupdate.html';");
                    out.println("</script>");
                } else {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Failed to update details');");
                    out.println("window.location.href = 'nameupdate.html';");
                    out.println("</script>");
                }
            } else {
                // Account does not exist
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Account not found');");
                out.println("window.location.href = 'nameupdate.html';");
                out.println("</script>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<script type=\"text/javascript\">");
            out.println("alert('An error occurred');");
            out.println("window.location.href = 'nameupdate.html';");
            out.println("</script>");
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}

}
