package com.terry.commons.services;

import java.util.List;

public interface CommonService <RQ, RS>{
	
	List<RS> listar();
	
	RS obtenerPorId(Long id);
	
	RS insertar(RQ request);
	
	RS actualizar(RQ request, Long id);
	
	RS eliminar(Long id);
	 // Este es el método específico que no está en CommonService
    boolean tipoIsPresent(Long id);

}
