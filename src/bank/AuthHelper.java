package bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthHelper {
    public static boolean verifyAccount(String acc, String password) {
        String sql = "SELECT password FROM accounts WHERE acc_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return password.equals(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
