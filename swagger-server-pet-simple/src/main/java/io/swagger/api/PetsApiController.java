package io.swagger.api;

import io.swagger.model.ErrorModel;
import io.swagger.model.NewPet;
import io.swagger.model.Pet;
import io.swagger.service.IPetsService;
import io.swagger.annotations.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T13:44:46.316Z")

@Controller
public class PetsApiController implements PetsApi {

	private static final Logger log = LoggerFactory.getLogger(PetsApiController.class);
	
	@Autowired
	private IPetsService service;

    public ResponseEntity<Pet> addPet(@ApiParam(value = "Pet to add to the store" ,required=true )  @Valid @RequestBody NewPet pet) {
        Pet newPet = service.create(pet);
    	return new ResponseEntity<Pet>(newPet, HttpStatus.OK);
    }

    public ResponseEntity<Pet> findPetById(@ApiParam(value = "ID of pet to fetch",required=true ) @PathVariable("id") Long id) {
        Pet result = service.findById(id);
        return new ResponseEntity<Pet>(result, HttpStatus.OK);
    }

    public ResponseEntity<List<Pet>> findPets(@ApiParam(value = "tags to filter by") @RequestParam(value = "tags", required = false) List<String> tags,
        @ApiParam(value = "maximum number of results to return") @RequestParam(value = "limit", required = false) Integer limit) {
        
    	// TODO: implement filter by tag and result limit
    	List<Pet> result = service.findAll();
        return new ResponseEntity<List<Pet>>(result, HttpStatus.OK);
    }

}
