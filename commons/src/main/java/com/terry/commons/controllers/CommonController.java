package com.terry.commons.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.terry.commons.services.CommonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Validated
public class CommonController  <RQ,RS,S extends CommonService<RQ,RS>>{
	protected S service;
	
	public CommonController(S service) {
		this.service = service;
	}
	
	@GetMapping
	public ResponseEntity<List<RS>> listar(){
		return ResponseEntity.ok(service.listar());
		
	}
	@GetMapping("/{id}")
	public ResponseEntity<RS> obtenerPorId(@PathVariable
			@Positive(message = "El Id debe ser un entero positivo")Long id){
		return ResponseEntity.ok(service.obtenerPorId(id));
	}
	
	@PostMapping
	public ResponseEntity<RS> insertar(@Valid 
			@RequestBody RQ request){
		return ResponseEntity.status(HttpStatus.CREATED).body(service.insertar(request));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<RS> actualizar(@PathVariable 
			@Positive(message = "El Id debe ser un entero positivo")Long id, @Valid @RequestBody RQ request){
		return ResponseEntity.ok(service.actualizar(request, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<RS> eliminarPorId(@PathVariable 
			@Positive(message = "El Id debe ser un entero positivo")Long id){
		return ResponseEntity.ok(service.eliminar( id));
	}
	
}
