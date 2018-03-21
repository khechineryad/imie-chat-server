
import com.mysql.cj.jdbc.Driver;

        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.SQLException;

public class Connect 	{
    private static final Connect instance = new Connect();
    private static Connection connection;

    private Connect()
    {
        try
        {
            String url = "jdbc:mysql://localhost/db_chat?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String utilisateur = "root";
            String motDePasse = "";
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(url, utilisateur , motDePasse);
            System.out.println("Connexion à la base de données : ok");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static Connection getConnection()
    {
        return connection;
    }
}