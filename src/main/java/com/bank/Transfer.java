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
 * Servlet implementation class Transfer
 */
@WebServlet("/Transfer")
public class Transfer extends HttpServlet {
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

        // Get the transfer details from the form
        String amountStr = request.getParameter("amount");
        String acc1 = request.getParameter("acc1");
        String acc1Name = request.getParameter("acc1_name");
        String acc2 = request.getParameter("acc2");
        String acc2Name = request.getParameter("acc2_name");

        // Validate input
        if (amountStr == null || amountStr.isEmpty() || acc1 == null || acc1.isEmpty() || acc2 == null || acc2.isEmpty() || acc1Name == null || acc1Name.isEmpty() || acc2Name == null || acc2Name.isEmpty()) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('All fields are required');");
            out.println("window.location.href = 'transfer.html';");
            out.println("</script>");
            return;
        }

        double transferAmount;
        try {
            transferAmount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Invalid amount');");
            out.println("window.location.href = 'transfer.html';");
            out.println("</script>");
            return;
        }

        if (transferAmount <= 0) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Amount should be greater than zero');");
            out.println("window.location.href = 'transfer.html';");
            out.println("</script>");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        boolean isSuccess = false;

        try {
            // Get database connection
            conn = DBConnection.get();
            conn.setAutoCommit(false); // Start transaction

            // Check if both accounts exist and validate names
            String checkAccountSql = "SELECT balance, account_name FROM accounts WHERE account_number = ?";
            pstmt = conn.prepareStatement(checkAccountSql);

            // Check sender account
            pstmt.setString(1, acc1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                String senderName = rs.getString("account_name");

                if (!acc1Name.equals(senderName)) {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Sender name mismatch');");
                    out.println("window.location.href = 'transfer.html';");
                    out.println("</script>");
                    return;
                }

                // Check if balance is sufficient for withdrawal
                if (currentBalance - transferAmount < 500) {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Insufficient funds: Balance must remain above ₹500');");
                    out.println("window.location.href = 'transfer.html';");
                    out.println("</script>");
                    return;
                }

                // Check receiver account
                pstmt.setString(1, acc2);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    double receiverBalance = rs.getDouble("balance");
                    String receiverName = rs.getString("account_name");

                    if (!acc2Name.equals(receiverName)) {
                        out.println("<script type=\"text/javascript\">");
                        out.println("alert('Receiver name mismatch');");
                        out.println("window.location.href = 'transfer.html';");
                        out.println("</script>");
                        return;
                    }

                    // Update balances
                    String updateSenderSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                    pstmt1 = conn.prepareStatement(updateSenderSql);
                    pstmt1.setDouble(1, currentBalance - transferAmount);
                    pstmt1.setString(2, acc1);
                    int rowsAffectedSender = pstmt1.executeUpdate();

                    String updateReceiverSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
                    pstmt2 = conn.prepareStatement(updateReceiverSql);
                    pstmt2.setDouble(1, receiverBalance + transferAmount);
                    pstmt2.setString(2, acc2);
                    int rowsAffectedReceiver = pstmt2.executeUpdate();

                    if (rowsAffectedSender > 0 && rowsAffectedReceiver > 0) {
                        conn.commit();
                        isSuccess = true;
                    } else {
                        conn.rollback();
                    }
                } else {
                    out.println("<script type=\"text/javascript\">");
                    out.println("alert('Receiver account not found');");
                    out.println("window.location.href = 'transfer.html';");
                    out.println("</script>");
                    return;
                }
            } else {
                out.println("<script type=\"text/javascript\">");
                out.println("alert('Sender account not found');");
                out.println("window.location.href = 'transfer.html';");
                out.println("</script>");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Send response to the user
        if (isSuccess) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Transfer Successful');");
            out.println("window.location.href = 'transfer.html';");
            out.println("</script>");
        } else {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Transfer Failed');");
            out.println("window.location.href = 'transfer.html';");
            out.println("</script>");
        }
	}

}
