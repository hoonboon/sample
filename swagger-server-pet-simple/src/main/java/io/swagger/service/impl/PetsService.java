package io.swagger.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.swagger.model.NewPet;
import io.swagger.model.Pet;
import io.swagger.service.IPetsService;

@Service
public class PetsService implements IPetsService {

	static final Map<Long, Pet> database = new HashMap<Long, Pet>();
	
	@Override
	public List<Pet> findAll() {
		List<Pet> result = new ArrayList<Pet>(database.values());
		return result;
	}

	@Override
	public Pet findById(Long id) {
		Pet result = database.get(id);
		return result;
	}

	@Override
	public Pet create(NewPet newPet) {
		Pet result = new Pet(newPet.getId(), newPet.getName(), newPet.getTag());
		database.put(result.getId(), result);
		return result;
	}

}
