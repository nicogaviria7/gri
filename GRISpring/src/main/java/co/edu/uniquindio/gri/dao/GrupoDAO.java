package co.edu.uniquindio.gri.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniquindio.gri.model.Grupo;
import co.edu.uniquindio.gri.repository.GrupoRepository;

@Service
public class GrupoDAO {

	@Autowired
	GrupoRepository grupoRepository;
	
	public List<Grupo> findAll(){
		return grupoRepository.findAll();
	}
	
	public Grupo findOne(Long id){
		return grupoRepository.findOne(id);
	}
	
	public void save (Grupo grupo){
		grupoRepository.save(grupo);
	}
}
