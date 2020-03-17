
package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Local;

public interface LocalRepository {

	Collection<Local> findAccepted() throws DataAccessException;

	Local findById(int id) throws DataAccessException;

	Collection<Local> findByDireccion(String direccion) throws DataAccessException;

}