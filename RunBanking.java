package project1;
import java.sql.*;
import java.util.Locale;
import java.util.Scanner;

class sqlinitial {
    String url = "jdbc:mysql://localhost:3306/proj1";
    String username = "root";
    String password = "noob123";
    Connection connection = DriverManager.getConnection(url, username, password);
    Statement statement = connection.createStatement();

    sqlinitial() throws SQLException {
    }
}

class privatedata extends sqlinitial {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public privatedata(String username, String password) throws SQLException {
        this.username = username;
        this.password = password;
    }

    void createacc() throws SQLException {
        String sql = "insert into admin values (?,?)";
        try {
            PreparedStatement preparedstatement = connection.prepareStatement(sql);
            preparedstatement.setString(1,username);
            preparedstatement.setString(2,password);
            preparedstatement.executeUpdate();
            System.out.println("Your account has been created");
        }
        catch(Exception e) {
            System.out.println("Error. username already exists");
        }

    }

    boolean checkacc() throws SQLException {
        String sql = "select password from admin where username = '"+username+"'";
        ResultSet result1 = statement.executeQuery(sql);
        if (result1.next() == false) {
            return false;
        }
        String pass1 = result1.getString(1);
        if (pass1.equals(password)) {
            return true;
        }
        else {
            return false;
        }

    }
}


class employee extends sqlinitial {

    public employee() throws SQLException {
    }


    String viewing(int accountid) throws SQLException {
        String sql = "select * from bank where account_id = "+accountid+"";
        ResultSet resultset = statement.executeQuery(sql);
        String retur1 = null;
        while (resultset.next()) {
            retur1 = "ID: " + resultset.getInt(1) + ", Balance: " + resultset.getInt(2) + ", Approval status: " + resultset.getString(3) + ", Pending money transfers: "
                    + resultset.getInt(4) + ", Username: " + resultset.getString(5);
        }
        if (retur1 == null) {
            return "this account does not exist or some other error has occured.";
        }
        return retur1;
    }

    String approving(int accountid) throws SQLException {
        String sql = "UPDATE bank SET status = 'approved' where account_id = "+accountid+"";
        if (statement.executeUpdate(sql) == 0) {
            return "The account does not exist and/or nothing was updated"; }
        else {
            return "This account has been approved.";
        }
    }

    String rejecting(int accountid) throws SQLException {
        String sql = "UPDATE bank SET status = 'rejected' where account_id = "+accountid+"";
        if (statement.executeUpdate(sql) == 0) {
            return "The account does not exist and/or nothing was updated"; }
        else {
            return "This account has been rejected.";
        }
    }

}

//}

class bankid extends sqlinitial {

    bankid() throws SQLException {
    }
    PreparedStatement preparedstatement;
    ResultSet resultss;

    String creatingaccount(String username) throws SQLException {
        String sql = "insert into bank (balance,username) values (?,?)";
        preparedstatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedstatement.setInt(1,0);
        preparedstatement.setString(2,username);
        preparedstatement.executeUpdate();
        resultss = preparedstatement.getGeneratedKeys();
        resultss.next();
        return "Your new account id is " + resultss.getInt(1) + " with a current balance of 0";
    }

    String depositing(int accountid, int value) throws SQLException {
        String sql = "select * from bank where account_id = ("+accountid+")";
        resultss = statement.executeQuery(sql);
        if (resultss.next() == false) {
            return "This account id does not exist/row is empty";
        }
        int balance = resultss.getInt(2);
        balance = balance + value;
        sql = "Update bank set balance = "+balance+" where account_id = "+accountid+"";
        statement.executeUpdate(sql);
        return "Your new bank balance is " + balance + " for account ID " + accountid;
    }

    String withdrawing(int accountid, int value) throws SQLException {
        String sql = "select * from bank where account_id = ("+accountid+")";
        resultss = statement.executeQuery(sql);
        if (resultss.next() == false) {
            return "This account id does not exist/row is empty";
        }
        int balance = resultss.getInt(2);
        if (balance - value < 0) {
            return "Transaction invalid: Not enough money in account";
        }
        balance = balance - value;
        sql = "Update bank set balance = "+balance+" where account_id = "+accountid+"";
        statement.executeUpdate(sql);
        return "Your new bank balance is " + balance + " for account ID " + accountid;
    }
    Object withdrawing(int accountid, int value, String moneytransfer) throws SQLException {
        String sql = "select * from bank where account_id = ("+accountid+")";
        resultss = statement.executeQuery(sql);
        resultss.next();
        int balance = resultss.getInt(2);
        balance = balance - value;
        sql = "Update bank set balance = "+balance+" where account_id = "+accountid+"";
        statement.executeUpdate(sql);
        return "Your new bank balance is " + balance + " for account ID " + accountid;
    }

    Object withdrawcheck(int accountid, int value, String moneytransfer) throws SQLException {
        String sql = "select * from bank where account_id = ("+accountid+")";
        resultss = statement.executeQuery(sql);
        resultss.next();
        int balance = resultss.getInt(2);
        if (balance - value < 0) {
            return null;
        }
        else {
            return "Ok";
        }
    }

    Object viewing(int accountid, String username) throws SQLException {
        String sql = "select * from bank where account_id = "+accountid+"";
        resultss = statement.executeQuery(sql);
        String retur = null;
        String user2 = null;
        String approval = "";
        while (resultss.next()) {
            int balance = resultss.getInt(2);
            user2 = resultss.getString(5);
            approval = resultss.getString(3);
            if (approval == null) {
                approval = "";
            }
            retur = "Your current balance is "+balance+" for account id "+accountid+"";
        }

        if (retur == null || user2 == null || !user2.equalsIgnoreCase(username)) {
            return null;
        }
        if (approval.equals("rejected")) {
            System.out.println("Error: this account has been rejected by an administrator. Contact customer support");
            return null;
        }

        return retur;
    }

    Object viewing(int accountid) throws SQLException {
        String sql = "select * from bank where account_id = "+accountid+"";
        resultss = statement.executeQuery(sql);
        String retur = null;
        while (resultss.next()) {
            int balance = resultss.getInt(2);
            retur = "Your current balance is "+balance+" for account id "+accountid+"";
        }
        if (retur == null) {
            return null;
        }

        return retur;
    }

    Object viewing(int accountid, String money, String transfer) throws SQLException {
        String sql = "select * from bank where account_id = "+accountid+"";
        resultss = statement.executeQuery(sql);
        String retur = null;
        while(resultss.next()) {
            retur = resultss.getString(1);
        }
        if (retur == null) {
            return null;
        }

        return "ok";
    }

    int accepttransfer(int accid) throws SQLException {
        String sql = "select pending_money_transfers from bank where account_id = "+accid+"";
        resultss = statement.executeQuery(sql);
        resultss.next();
        int pending = resultss.getInt(1);
        return pending;
    }

    void accepttransfer(int accid, String yes) throws SQLException {
        String sql = "select pending_money_transfers from bank where account_id = "+accid+"";
        resultss = statement.executeQuery(sql);
        resultss.next();
        int pending = resultss.getInt(1);
        String sql1 = "select balance from bank where account_id = "+accid+"";
        resultss = statement.executeQuery(sql1);
        resultss.next();
        int balance = resultss.getInt(1);
        balance = pending + balance;
        String sql2 = "Update bank set balance = (?) where account_id = (?)";
        preparedstatement = connection.prepareStatement(sql2);
        preparedstatement.setInt(1, balance);
        preparedstatement.setInt(2,accid);
        preparedstatement.executeUpdate();
        String sql3 = "Update bank set pending_money_transfers = (?) where account_id = (?)";
        preparedstatement = connection.prepareStatement(sql3);
        preparedstatement.setInt(1, 0);
        preparedstatement.setInt(2,accid);
        preparedstatement.executeUpdate();


    }

     void moneytransfer(int accid, int value) throws SQLException {
        String sql1 = "select pending_money_transfers from bank where account_id = "+accid+"";
        resultss = statement.executeQuery(sql1);
        resultss.next();
        int currenttransfer = resultss.getInt(1);
        currenttransfer = currenttransfer + value;
        String sql2 = "Update bank set pending_money_transfers = (?) where account_id = (?)";
        preparedstatement = connection.prepareStatement(sql2);
        preparedstatement.setInt(1, currenttransfer);
        preparedstatement.setInt(2,accid);
        preparedstatement.executeUpdate();
        }

    }



public class RunBanking {
    public static void main(String[] args) throws SQLException {
        System.out.println("Thank you for banking with U$AIncorporated. Are you an employee or customer?");
        Scanner scanner = new Scanner(System.in);
        String reply = scanner.nextLine();
        if (reply.toLowerCase().equals("employee")) {
            System.out.println("What is the password?");
            String passattempt = scanner.nextLine();
            while (!passattempt.equals("****")) {
                System.out.println("Try again");
                passattempt = scanner.nextLine();
            }
            System.out.println("Welcome!");
            System.out.println("Would you like to view, approve, or reject an account, or exit? Please enter one of these");
            String counter = scanner.nextLine();
            String value;
            int intvalue = 0;
            employee employee1 = new employee();
            while (!counter.toLowerCase().equals("exit")) {
                switch (counter.toLowerCase()) {
                    case "view":
                        System.out.println("What account ID would you like to view?");
                        value = scanner.nextLine();
                        try {
                            intvalue = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry");
                        }
                        System.out.println(employee1.viewing(intvalue));
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    case "approve":
                        System.out.println("What account ID would you like to approve?");
                        value = scanner.nextLine();
                        try {
                            intvalue = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        employee1.approving(intvalue);
                        System.out.println(employee1.approving(intvalue));
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    case "reject":
                        System.out.println("What account ID would you like to reject?");
                        value = scanner.nextLine();
                        try {
                            intvalue = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        employee1.approving(intvalue);
                        System.out.println(employee1.rejecting(intvalue));
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;

                    default:
                        System.out.println("Sorry that command is not recognized. Try again");
                        counter = scanner.nextLine();
                }
            }
        }
        else if (reply.toLowerCase().equals("customer")) {
            System.out.println("Are you 'new' or returning user?");
            String user = scanner.nextLine();
            if (user.toLowerCase().equals("new")) {
                System.out.println("What would you like to set as your username?");
                String username = scanner.nextLine();
                System.out.println("What would you ilke to set as your password?");
                String password = scanner.nextLine();
                privatedata newacc = new privatedata(username,password);
                newacc.createacc();
                return; }
            int x1 = 1;
            boolean login = false;
            privatedata checklog;
            String user1 = null;
            System.out.println("Alrighty please enter your login credentials.");
            while(login == false) {
                System.out.println("Attempt "+x1+" of 2.");
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                checklog = new privatedata(username, password);
                if (checklog.checkacc()) {
                    user1 = checklog.getUsername();
                    login = true;
                }
                if(x1 == 2 && login == false) {
                    System.out.println("Invalid credentials.");
                    return;
                }
                x1++;

            }
            System.out.println("Would you like to make a deposit or withdraw? Or 'view' an account, 'money transfer' to post a transfer, 'create' to sign up, or exit");
            String counter = scanner.nextLine();
            String value;
            String accid;
            bankid customaction = new bankid();
            Object x;
            while (!counter.toLowerCase().equals("exit")) {
                switch (counter.toLowerCase()) {
                    case "create":
                        System.out.println(customaction.creatingaccount(user1));
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    case "view":
                        System.out.println("Which account id would you like to view?");
                        accid = scanner.nextLine();
                        try {
                            int accid1 = Integer.parseInt(accid);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        x = customaction.viewing(Integer.parseInt(accid), user1);
                        if (x == null) {
                            System.out.println("Account may not exist or id is not part of acc...");
                            System.out.println("What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        System.out.println(x);
                        int y = customaction.accepttransfer(Integer.parseInt(accid));
                        if (y > 0) {
                            String accept;
                            System.out.println("You have a pending money order of " + y + " to your account. Would you like to accept?");
                            accept = scanner.nextLine();
                            if (accept.toLowerCase().equals("yes")) {
                                customaction.accepttransfer(Integer.parseInt(accid), "yes");
                                System.out.println("Account updated");
                                System.out.println(customaction.viewing(Integer.parseInt(accid)));
                            } else {
                                System.out.println("Alrighty then.");
                            }
                        }
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    case "deposit":
                        System.out.println("Which account id would you like to deposit?");
                        accid = scanner.nextLine();
                        try {
                            int accid1 = Integer.parseInt(accid);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        x = customaction.viewing(Integer.parseInt(accid), user1);
                        if (x == null) {
                            System.out.println("Account may not exist or id is not part of acc...");
                            System.out.println("What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        System.out.println(x);
                        System.out.println("How much would you like to deposit?");
                        value = scanner.nextLine();
                        try {
                            int value1 = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry.");
                            break;
                        }
                        System.out.println(customaction.depositing(Integer.parseInt(accid), Integer.parseInt(value)));
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    case "withdraw":
                        System.out.println("Which account id would you like to withdraw?");
                        accid = scanner.nextLine();
                        try {
                            int accid1 = Integer.parseInt(accid);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        x = customaction.viewing(Integer.parseInt(accid), user1);
                        if (x == null) {
                            System.out.println("Account may not exist or id is not part of acc...");
                            System.out.println("What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        System.out.println(x);
                        System.out.println("How much would you like to withdraw?");
                        value = scanner.nextLine();
                        try {
                            int value1 = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry.");
                            break;
                        }
                        System.out.println(customaction.withdrawing(Integer.parseInt(accid), Integer.parseInt(value)));
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    case "money transfer":
                        System.out.println("Which account id would you like to post a transfer out of?");
                        accid = scanner.nextLine();
                        try {
                            int accid1 = Integer.parseInt(accid);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        x = customaction.viewing(Integer.parseInt(accid), user1);
                        if (x == null) {
                            System.out.println("Account may not exist or id is not part of acc...");
                            System.out.println("What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        System.out.println(x);
                        System.out.println("How much would you like to transfer?");
                        value = scanner.nextLine();
                        try {
                            int value1 = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry.");
                            break;
                        }
                        x = customaction.withdrawcheck(Integer.parseInt(accid), Integer.parseInt(value), "Ok");
                        if (x == null) {
                            System.out.println("Invalid withdrawal. More money than account has.");
                            System.out.println("Starting over. What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        System.out.println(x);
                        System.out.println("What account would you like to transfer to?");
                        String accid2 = scanner.nextLine();
                        try {
                            int accid1 = Integer.parseInt(accid2);
                        } catch (NumberFormatException e) {
                            System.out.println("Error occcured. Invaid entry. Starting over.");
                            break;
                        }
                        x = customaction.viewing(Integer.parseInt(accid2), "money", "transfer");
                        if (x == null) {
                            System.out.println("Account may not exist...");
                            System.out.println("What action would you like to do next?");
                            counter = scanner.nextLine();
                            break;
                        }
                        x = customaction.withdrawing(Integer.parseInt(accid), Integer.parseInt(value), "moneytransfer");
                        System.out.println(x + " (not including pending transfers)");
                        customaction.moneytransfer(Integer.parseInt(accid2), Integer.parseInt(value));
                        System.out.println("A money transfer has been posted to selected account");
                        System.out.println("What action would you like to do next?");
                        counter = scanner.nextLine();
                        break;
                    default:
                        System.out.println("Sorry that command is not recognized. Try again. " +
                                "You may 'view', 'withdraw','deposit', post a 'money transfer', or 'create' a new bank id");
                        counter = scanner.nextLine();
                }
            }
        }
        else{
            System.out.println("User is unauthorized");
        }

        scanner.close();
    }
}


