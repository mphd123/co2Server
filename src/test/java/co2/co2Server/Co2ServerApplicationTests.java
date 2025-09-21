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



	String name = "testSensor";

	Timestamp time1 = new Timestamp(System.currentTimeMillis());
	int co21 = 400;
	int temp1 = 30;
	Co2Entry entry1 = new Co2Entry(0, co21, temp1, name, time1 );

	Timestamp time2 = new Timestamp(System.currentTimeMillis()+10000);
	int co22 = 450;
	int temp2 = 300;
	Co2Entry entry2 = new Co2Entry(1, co22, temp2, name, time2 );

	Timestamp time3 = new Timestamp(System.currentTimeMillis()-10000);
	int co23 = 500;
	int temp3 = 200;
	Co2Entry entry3 = new Co2Entry(1, co23, temp3, name, time3 );


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

		ResponseEntity<Co2Entry> response = webcontroller.addEntry(entry1);

		assertEquals(202, response.getStatusCodeValue());

		Co2Entry dbEntry = db.getEntries().getFirst();

		assertEquals(time1, dbEntry.getDate());
        assertEquals(co21, dbEntry.getCo2());
		assertEquals(temp1, dbEntry.getTemperature());
		assertEquals(name, dbEntry.getSensorName());
		assertEquals(1, dbEntry.getEntryId()); // it should be one since the db should only have one entry

	}


	@Test
	void testAddEntriesAndGetInOrder() throws Exception {

		Add3Entries();
		List<Co2Entry> dbEntries = db.getEntries();


		assertEquals(co22, dbEntries.get(0).getCo2());
		assertEquals(temp2, dbEntries.get(0).getTemperature());
		assertEquals(name, dbEntries.get(0).getSensorName());
		assertEquals(time2, dbEntries.get(0).getDate());


		assertEquals(co21, dbEntries.get(1).getCo2());
		assertEquals(temp1, dbEntries.get(1).getTemperature());
		assertEquals(name, dbEntries.get(1).getSensorName());
		assertEquals(time1, dbEntries.get(1).getDate());

		assertEquals(co23, dbEntries.get(2).getCo2());
		assertEquals(temp3, dbEntries.get(2).getTemperature());
		assertEquals(name, dbEntries.get(2).getSensorName());
		assertEquals(time3, dbEntries.get(2).getDate());
	}


	@Test
	void testGetMostRecentEntry() throws Exception {


		Add3Entries();

		Co2Entry entry = db.getMostRecentEntry();


		assertEquals(co22, entry.getCo2());
		assertEquals(temp2, entry.getTemperature());
		assertEquals(name, entry.getSensorName());
		assertEquals(time2, entry.getDate());
	}

	private void  Add3Entries(){
		ResponseEntity<Co2Entry> response1 = webcontroller.addEntry(entry1);
		ResponseEntity<Co2Entry> response2 = webcontroller.addEntry(entry2);
		ResponseEntity<Co2Entry> response3 = webcontroller.addEntry(entry3);

		assertEquals(202, response1.getStatusCodeValue());
		assertEquals(202, response2.getStatusCodeValue());
		assertEquals(202, response3.getStatusCodeValue());

	}

}
