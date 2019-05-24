package co.edu.uniquindio.gri.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniquindio.gri.model.Investigador;
import co.edu.uniquindio.gri.repository.InvestigadorRepository;

@Service
public class InvestigadorDAO {
	
	@Autowired
	InvestigadorRepository investigadorRepository;
	
	public void save(Investigador investigador){
		investigadorRepository.save(investigador);
	}
}
