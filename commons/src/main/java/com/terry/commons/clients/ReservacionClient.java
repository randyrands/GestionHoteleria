package com.terry.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-reservas")
public interface ReservacionClient {
	
	@GetMapping("/id-habitacion/{id}")
	boolean regionIsPresent(@PathVariable Long id);
	
	@GetMapping("/id-huesped/{id}")
	boolean tipoIsPresent(@PathVariable Long id);

}
