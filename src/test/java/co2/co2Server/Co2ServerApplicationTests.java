package co2.co2Server;

import co2.co2Server.database.Co2Entry;
import co2.co2Server.database.SensorDataDB;
import co2.co2Server.database.controller.Webcontroller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Co2ServerApplicationTests {
	private SensorDataDB db;
	private Webcontroller webcontroller;


	@Autowired
	public void Co2ServerApplicationIntegrationTests(SensorDataDB db,Webcontroller webcontroller) {
		this.db = db;
		this.webcontroller = webcontroller;
	}

	@BeforeEach
	void setupDb() throws Exception {
		db.removeAll();
	}

	@Test
	void contextLoads() {
		assertNotNull(db, "SensorDataDB should be injected");
	}

	@Test
	void testAddEntry1() throws Exception {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		int co2 = 400;
		int temp = 30;
		String name = "testSensor";
		Co2Entry entry = new Co2Entry(0, co2, temp, name, time );

		ResponseEntity<Co2Entry> response = webcontroller.addEntry(entry);

		assertEquals(202, response.getStatusCodeValue());

		Co2Entry dbEntry = db.getEntries().getFirst();

		assertEquals(time, dbEntry.getDate());
        assertEquals(co2, dbEntry.getCo2());
		assertEquals(temp, dbEntry.getTemperature());
		assertEquals(name, dbEntry.getSensorName());
		assertEquals(1, dbEntry.getEntryId()); // it should be one since the db should only have one entry

	}

}
