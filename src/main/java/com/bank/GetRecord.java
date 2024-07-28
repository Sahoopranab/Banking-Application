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
 * Servlet implementation class GetRecord
 */
@WebServlet("/GetRecord")
public class GetRecord extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Check for user session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        // Get the account number from the form
        String accountNumber = request.getParameter("anum");

        // Validate input
        if (accountNumber == null || accountNumber.isEmpty()) {
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Account Number cannot be empty');");
            out.println("window.location.href = 'getdata.html';");
            out.println("</script>");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.get();

            // SQL query to get account details based on account number
            String sql = "SELECT account_number, account_name, age, aadhar_number,pancard_number, mobile_number, address, balance FROM accounts WHERE account_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            rs = pstmt.executeQuery();

            // Display account details
            out.println("<html>");
            out.println("<head>");
            out.println("<style>");
            out.println("body { font-family: Arial, Helvetica, sans-serif; margin: 0; padding: 0; }");
            out.println("h2 { text-align: center; margin: 20px 0; }");
            out.println("nav { width: 100%; background-color: green; }");
            out.println("ul.nav { list-style: none; margin: 0; padding: 0; display: flex; justify-content: center; align-items: center; height: 50px; }");
            out.println("ul.nav li { margin: 0; position: relative; }");
            out.println("ul.nav a { display: block; padding: 0 15px; color: white; text-decoration: none; line-height: 50px; transition: background-color 0.3s, color 0.3s; }");
            out.println("ul.nav a:hover, ul.nav li.current a { color: green; background: white; }");
            out.println("table { margin: 20px auto; border-collapse: collapse; width: 80%; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }");
            out.println("table th, table td { padding: 12px; text-align: left; border: 1px solid #ddd; }");
            out.println("table th { background-color: #4CAF50; color: white; }");
            out.println("table tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("table tr:hover { background-color: #f1f1f1; }");
            out.println("a { color: green; text-decoration: none; }");
            out.println("a:hover { text-decoration: underline; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<nav>");
            out.println("<ul class='nav'>");
            out.println("<li><a href='createaccount.html'>Create Account</a></li>");
            out.println("<li><a href='withdraw.html'>Withdraw Amount</a></li>");
            out.println("<li><a href='deposit.html'>Deposit Amount</a></li>");
            out.println("<li><a href='getdata.html'>Get Details</a></li>");
            out.println("<li><a href='nameupdate.html'>Update Details</a></li>");
            out.println("<li><a href='deleterecord.html'>Delete Account</a></li>");
            out.println("<li><a href='transfer.html'>Transfer Amount</a></li>");
            out.println("<li><a href='CreditCard'>Credit Card</a></li>");
            out.println("<li><a href='login.html'>Logout</a></li>");
            out.println("</ul>");
            out.println("</nav>");
            out.println("<h2>Account Details</h2>");
            out.println("<table>");
            out.println("<tr><th>Account Number</th><th>Account Name</th><th>Age</th><th>Aadhar Number</th><th>Pan Number</th><th>Mobile Number</th><th>Address</th><th>Balance</th></tr>");

            if (rs.next()) {
                String accNumber = rs.getString("account_number");
                String accName = rs.getString("account_name");
                int age = rs.getInt("age");
                String aadharNumber = rs.getString("aadhar_number");
                String PanNumber = rs.getString("pancard_number");
                String mobileNumber = rs.getString("mobile_number");
                String address = rs.getString("address");
                double balance = rs.getDouble("balance");

                out.println("<tr>");
                out.println("<td>" + accNumber + "</td>");
                out.println("<td>" + accName + "</td>");
                out.println("<td>" + age + "</td>");
                out.println("<td>" + aadharNumber + "</td>");
                out.println("<td>" + PanNumber + "</td>");
                out.println("<td>" + mobileNumber + "</td>");
                out.println("<td>" + address + "</td>");
                out.println("<td>" + balance + "</td>");
                out.println("</tr>");
            } else {
                out.println("<tr><td colspan='7'>No records found</td></tr>");
            }

            out.println("</table>");
            out.println("<div style='text-align: center; margin: 20px;'><a href='getdata.html'>Back</a></div>");
            out.println("</body>");
            out.println("</html>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Database error occurred');");
            out.println("window.location.href = 'getdata.html';");
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
