package io.swagger.service;

import java.util.List;

import io.swagger.model.NewPet;
import io.swagger.model.Pet;

public interface IPetsService {
	
	List<Pet> findAll();
	
	Pet findById(Long id);
	
	Pet create(NewPet newPet);
	
}
