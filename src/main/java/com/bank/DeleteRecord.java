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
 * Servlet implementation class DeleteRecord
 */
@WebServlet("/DeleteRecord")
public class DeleteRecord extends HttpServlet {
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
        String accNo = request.getParameter("anum");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Get database connection
            conn = DBConnection.get();

            // Delete account
            String deleteAccountSql = "DELETE FROM accounts WHERE account_number = ?";
            pstmt = conn.prepareStatement(deleteAccountSql);
            pstmt.setString(1, accNo);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Account deleted successfully');");
                out.println("window.location.href = 'deleterecord.html';");
                out.println("</script>");
            } else {
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Account not found');");
                out.println("window.location.href = 'deleterecord.html';");
                out.println("</script>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<script type=\"text/javascript\">");
            out.println("alert('An error occurred');");
            out.println("window.location.href = 'deleterecord.html';");
            out.println("</script>");
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}

}
