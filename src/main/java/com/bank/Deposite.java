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
 * Servlet implementation class Deposite
 */
@WebServlet("/Deposite")
public class Deposite extends HttpServlet {
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

        // Get the account details from the form
        String accountNumber = request.getParameter("anum");
        String depositAmountStr = request.getParameter("amt");

        // Validate input
        if (accountNumber == null || accountNumber.isEmpty() || depositAmountStr == null || depositAmountStr.isEmpty()) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Account Number and Amount cannot be empty');");
            out.println("window.location.href = 'withdraw.html';");
            out.println("</script>");
            return;
        }

        double depositAmount;
        try {
            depositAmount = Double.parseDouble(depositAmountStr);
        } catch (NumberFormatException e) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Invalid amount');");
            out.println("window.location.href = 'withdraw.html';");
            out.println("</script>");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        boolean isSuccess = false;

        try {
            // Get database connection
            conn = DBConnection.get();

            // Check if account exists and get the current balance
            String checkAccountSql = "SELECT balance FROM accounts WHERE account_number = ?";
            pstmt = conn.prepareStatement(checkAccountSql);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");

                // Update the account balance
                String updateSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                pstmt1 = conn.prepareStatement(updateSql);
                pstmt1.setDouble(1, currentBalance + depositAmount);
                pstmt1.setString(2, accountNumber);

                int rowsAffected = pstmt1.executeUpdate();
                if (rowsAffected > 0) {
                    isSuccess = true;
                }
            } else {
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Account not found');");
                out.println("window.location.href = 'deposit.html';");
                out.println("</script>");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
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

        // Send response to the user
        if (isSuccess) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Deposit Successful');");
            out.println("window.location.href = 'deposit.html';");
            out.println("</script>");
        } else {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Deposit Failed');");
            out.println("window.location.href = 'deposit.html';");
            out.println("</script>");
        }
	}

}
