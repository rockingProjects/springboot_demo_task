package com.example.demo.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.component.ICEncrypt;
import com.example.demo.dao.KingdomDao;
import com.example.demo.dao.PersonDao;
import com.example.demo.dto.CreatePersonRequestDTO;
import com.example.demo.entity.KingdomEntity;
import com.example.demo.entity.PersonEntity;
import com.example.demo.exception.KingdomNotFoundException;

@Component
public class UC_DEMO_001_01_CreatePersonActivity {
	
	@Autowired
	private KingdomDao kingdomDao;
	
	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private ICEncrypt encrypt;
	
	public void doIt(String kingdomIdentifier, CreatePersonRequestDTO request) throws KingdomNotFoundException {
		KingdomEntity kingdom = findKingdom(kingdomIdentifier);
		String identifier = generateIdentifier();
		createPerson(identifier, kingdom, request);
	}

	private KingdomEntity findKingdom(String kingdomIdentifier) throws KingdomNotFoundException {
		KingdomEntity kingdom = kingdomDao.findByIdentifier(kingdomIdentifier);
		if (kingdom == null) {
			throw new KingdomNotFoundException("Unknown kingdom with identifier " + kingdomIdentifier);
		}
		return kingdom;
	}

	private String generateIdentifier() {
		return encrypt.randomString(20);
	}

	private void createPerson(String identifier, KingdomEntity kingdom, CreatePersonRequestDTO request) {
		PersonEntity person = PersonEntity.builder().withFirstName(request.getFirstName())
		.withLastName(request.getLastName())
		.withSex(request.getSex())
		.withKingdom(kingdom)
		.withIdentifier(identifier)
		.build();
		
		this.personDao.create(person);
	}
}
