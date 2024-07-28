package com.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CreditCard
 */
@WebServlet("/CreditCard")
public class CreditCard extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        String customerName = request.getParameter("customername");
        String accountNumber = request.getParameter("Acc_no");
        String creditCardNumber = request.getParameter("cnum");
        String expiryDate = request.getParameter("edate");
        String cvv = request.getParameter("cvv");
        String withdrawLimit = request.getParameter("withdrawlimit");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Get database connection
            conn = DBConnection.get();

            // Insert credit card details into database
            String insertCreditCardSql = "INSERT INTO credit_cards (customer_name, account_number, credit_card_number, expiry_date, cvv, withdrawal_limit) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertCreditCardSql);
            pstmt.setString(1, customerName);
            pstmt.setString(2, accountNumber);
            pstmt.setString(3, creditCardNumber);
            pstmt.setDate(4, Date.valueOf(expiryDate)); // Convert to java.sql.Date
            pstmt.setInt(5, Integer.parseInt(cvv)); // Parse CVV as an integer
            pstmt.setBigDecimal(6, new BigDecimal(withdrawLimit)); // Parse withdrawal limit as BigDecimal

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Credit Card created successfully');");
                out.println("window.location.href = 'CreditCard.html';");
                out.println("</script>");
            } else {
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Failed to create Credit Card');");
                out.println("window.location.href = 'CreditCard.html';");
                out.println("</script>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<script type=\"text/javascript\">");
            out.println("alert('An error occurred');");
            out.println("window.location.href = 'CreditCard.html';");
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
