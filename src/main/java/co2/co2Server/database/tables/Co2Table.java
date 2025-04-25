package co2.co2Server.database.tables;

public class Co2Table {
    // Table name, and column names
    public static final String TABLE_NAME = "co2Entries";
    public static final String Entry_ID = "EntryId";
    public static final String CO2VALUE = "co2Value";
    public static final String Temperature = "temperature";
    public static final String SensorName = "sensorName";
    public static final String Date = "date";

    public static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS %s (
                %s integer PRIMARY KEY,
                %s integer NOT NULL,
                %s integer,
                %s text,
                %s date
            )
            """.formatted(TABLE_NAME, Entry_ID, CO2VALUE, Temperature, SensorName, Date);


    public static final int INSERT_CO2Value_INDEX = 1;
    public static final int INSERT_Temperature_INDEX = 2;
    public static final int INSERT_SensorName_INDEX = 3;
    public static final int INSERT_Date_INDEX = 4;
    public static final String INSERT = """
            INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)
            """.formatted(TABLE_NAME, CO2VALUE, Temperature, SensorName, Date);

    public static final int DELETE_Entry_ID_INDEX = 1;
    public static final String DELETE = """
            DELETE FROM %s WHERE %s = ?
            """.formatted(TABLE_NAME, Entry_ID);

    public static final String SELECT = """
            SELECT %s, %s, %s, %s FROM %s
            """.formatted(Entry_ID, CO2VALUE, Temperature, SensorName, TABLE_NAME);

    public static final String UPDATE_TEMPLATE = """
            UPDATE %s SET %s = ? WHERE %s = ?
            """;

    private Co2Table() {
    }
}
