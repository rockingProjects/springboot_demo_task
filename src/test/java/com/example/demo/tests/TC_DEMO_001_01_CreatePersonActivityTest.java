package com.example.demo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.DemoApplication;
import com.example.demo.base.TestRestClient;
import com.example.demo.component.ICEncrypt;
import com.example.demo.dao.KingdomDao;
import com.example.demo.dao.PersonDao;
import com.example.demo.dto.CreatePersonRequestDTO;
import com.example.demo.entity.KingdomEntity;
import com.example.demo.entity.PersonEntity;
import com.example.demo.entity.SexEnum;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class}, webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // To reset the DB after each test method
class TC_DEMO_001_01_CreatePersonActivityTest {

	@Autowired
	private KingdomDao kingdomDao;
	
	@MockBean
	private ICEncrypt encrypt;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private PersonDao personDao;
	
	/**
	 * <pre>
	 * Description:
	 * A person gets created successfully
	 * 
	 * prepare:
	 * Kingdom k with
	 * - identifier = 11111111111111111111
	 * - name = "Germany"
	 * 
	 * mocks:
	 * ICEncrypt.randomString deliveres "12345123451234512345" as result
	 * 
	 * test:
	 * Call endpoint with
	 * - kingdomIdentifier = 11111111111111111111
	 * - payload = CreatePersonRequestDTO with
	 *  - first name = King
	 *  - last name = Kong
	 *  - sex = male
	 * 
	 * check:
	 * Endpoint deliveres:
	 * - 200OK
	 * - Body is null
	 * 
	 * In database:
	 * - PersonEntity with:
	 *  - identifier = 12345123451234512345
	 *  - first name = King
	 *  - last name = Kong
	 *  - sex = male
	 *  - associated to Kingdom k
	 * 
	 * </pre>
	 * @throws Exception
	 */
	@Test
	void testSuccess() throws Exception {
		
		//prepare
		
		KingdomEntity kingdom = KingdomEntity.builder()
		.withIdentifier("11111111111111111111")
		.withName("Germany")
		.build();
		
		this.kingdomDao.create(kingdom);
		
		//mocks
		
		Mockito.when(this.encrypt.randomString(Mockito.anyInt())).thenReturn("12345123451234512345");
		
		//test
		
		CreatePersonRequestDTO request = CreatePersonRequestDTO.builder()
		.withFirstName("King")
		.withLastName("Kong")
		.withSex(SexEnum.male).build();
		
		ResponseEntity<String> result = new TestRestClient()
		.withPath("/kingdom/11111111111111111111/person")
		.withPort(this.port)
		.withPayload(request)
		.expectOkay()
		.fire(String.class);
		
		//check
		
		assertNull(result.getBody());
		
		List<PersonEntity> persons = this.personDao.findAll();
		assertEquals(1, persons.size());
		PersonEntity person = persons.get(0);
		
		assertEquals("King", person.getFirstName());
		assertEquals("Kong", person.getLastName());
		assertEquals("12345123451234512345", person.getIdentifier());
		assertEquals(SexEnum.male, person.getSex());
		
		KingdomEntity kingdomOfPerson = person.getKingdom();
		assertNotNull(kingdomOfPerson);
		assertEquals(kingdom.getId(), kingdomOfPerson.getId());
	}
	
	/**
	 * <pre>
	 * Description:
	 * Creating a person failes because of missing kingdom
	 * 
	 * prepare:
	 * Kingdom k with
	 * - identifier = 11111111111111111111
	 * - name = "Germany"
	 * 
	 * mocks:
	 * ICEncrypt.randomString deliveres "12345123451234512345" as result
	 * 
	 * test:
	 * Call endpoint with
	 * - kingdomIdentifier = notvalididentifier
	 * - payload = CreatePersonRequestDTO with
	 *  - first name = King
	 *  - last name = Kong
	 *  - sex = male
	 * 
	 * check:
	 * Endpoint deliveres:
	 * - 404 Not Found
	 * - Body is ""Unknown kingdom with identifier notvalididentifier"
	 * 
	 * In database:
	 * - no PersonEntity created
	 * 
	 * </pre>
	 * @throws Exception
	 */
	@Test
	void testFailKingdomNotFound() throws Exception {
		//prepare
		
		KingdomEntity kingdom = KingdomEntity.builder()
		.withIdentifier("11111111111111111111")
		.withName("Germany")
		.build();
		
		this.kingdomDao.create(kingdom);
		
		//mocks
		
		Mockito.when(this.encrypt.randomString(Mockito.anyInt())).thenReturn("12345123451234512345");
		
		//test
		
		CreatePersonRequestDTO request = CreatePersonRequestDTO.builder()
		.withFirstName("King")
		.withLastName("Kong")
		.withSex(SexEnum.male).build();
		
		ResponseEntity<String> result = new TestRestClient()
		.withPath("/kingdom/notvalididentifier/person")
		.withPort(this.port)
		.withPayload(request)
		.withExpectedStatus(HttpStatus.NOT_FOUND)
		.fire(String.class);
		
		//check
		
		assertEquals("Unknown kingdom with identifier notvalididentifier", result.getBody());
		
		List<PersonEntity> persons = this.personDao.findAll();
		assertEquals(0, persons.size());
	}
	
	/**
	 * <pre>
	 * Description:
	 * Creating a person fails because of missing field in request
	 * 
	 * prepare:
	 * Kingdom k with
	 * - identifier = 11111111111111111111
	 * - name = "Germany"
	 * 
	 * mocks:
	 * ICEncrypt.randomString deliveres "12345123451234512345" as result
	 * 
	 * test:
	 * Call endpoint with
	 * - kingdomIdentifier = notvalididentifier
	 * - payload = CreatePersonRequestDTO with
	 *  - first name = King
	 *  - sex = male
	 * 
	 * check:
	 * Endpoint deliveres:
	 * - 400 Bad Request
	 * - Body is "CreatePersonRequestDTO.lastName:NotEmpty"
	 * 
	 * In database:
	 * - no PersonEntity created
	 * 
	 * </pre>
	 * @throws Exception
	 */
	@Test
	void testValidationFailedException() throws Exception {
		//prepare
		
		KingdomEntity kingdom = KingdomEntity.builder()
		.withIdentifier("11111111111111111111")
		.withName("Germany")
		.build();
		
		this.kingdomDao.create(kingdom);
		
		//mocks
		
		Mockito.when(this.encrypt.randomString(Mockito.anyInt())).thenReturn("12345123451234512345");
		
		//test
		
		CreatePersonRequestDTO request = CreatePersonRequestDTO.builder()
		.withFirstName("King") //lastname is missing
		.withSex(SexEnum.male).build();
		
		ResponseEntity<String> result = new TestRestClient()
		.withPath("/kingdom/11111111111111111111/person")
		.withPort(this.port)
		.withPayload(request)
		.withExpectedStatus(HttpStatus.BAD_REQUEST)
		.fire(String.class);
		
		//check
		
		assertEquals("CreatePersonRequestDTO.lastName:NotEmpty", result.getBody());
		
		List<PersonEntity> persons = this.personDao.findAll();
		assertEquals(0, persons.size());
	}

}
