package co2.co2Server.database;



import co2.co2Server.database.tables.Co2Table;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SensorDataDB implements AutoCloseable {

    private static final String connectionString = "jdbc:sqlite:sensorData.db";
    public final Connection connection;

    public SensorDataDB() throws Exception {
        try {
            this.connection = DriverManager.getConnection(connectionString);
            createTables();
        } catch (SQLException e) {
            throw new Exception("Database connection could not be established", e);
        }
    }

    private void createTables() throws Exception {
        try (Statement s = connection.createStatement()) {
            s.execute(Co2Table.CREATE_TABLE);
        } catch (SQLException e) {
            throw new Exception("Could not create database tables", e);
        }
    }


    public void addEntry(int co2, int temp, String sensorName, Date date) throws Exception {
        try (PreparedStatement p = connection.prepareStatement(Co2Table.INSERT)) {
            p.setInt(Co2Table.INSERT_CO2Value_INDEX, co2);
            p.setInt(Co2Table.INSERT_Temperature_INDEX, temp);
            p.setString(Co2Table.INSERT_SensorName_INDEX, sensorName);
            p.setDate(Co2Table.INSERT_Date_INDEX,date);
            final int rows = p.executeUpdate();
            if (rows != 1) {
                throw new Exception("Internal database error");
            }
        } catch (SQLException e) {
            throw new Exception("Could not insert product", e);
        }
    }

    /**
     * Removes an Entry from the database.
     *
     * @param entry The product to remove
     * @throws Exception if the product cannot be removed
     */
    public void removeEntry(Co2Entry entry) throws Exception {
        try (PreparedStatement p = connection.prepareStatement(Co2Table.DELETE)) {
            p.setInt(Co2Table.DELETE_Entry_ID_INDEX, entry.getEntryId());
            p.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Could not delete product", e);
        }
    }

    /**
     * Gets a list of all Entries from the database.
     *
     * @return a list of all products
     * @throws Exception if the products cannot be read
     */

    public List<Co2Entry> getEntries() throws Exception {
        try (Statement s = connection.createStatement()) {
            // ResultSet acts like a pointer into the database table.
            final ResultSet results = s.executeQuery(Co2Table.SELECT);
            final List<Co2Entry> entries = new ArrayList<>();
            while (results.next()) {
                final int productId = results.getInt(Co2Table.Entry_ID);
                final int co2 = results.getInt(Co2Table.CO2VALUE);
                final int temp = results.getInt(Co2Table.Temperature);
                final String sensorName = results.getString(Co2Table.SensorName);
                final Date date = results.getDate(Co2Table.Date);
                entries.add(new Co2Entry(productId, co2, temp, sensorName, date));
            }
            return entries;
        } catch (SQLException e) {
            throw new Exception("Could not read products", e);
        }
    }



    @Override
    public void close() throws Exception {
        // See AutoClosable
        connection.close();
    }


}
