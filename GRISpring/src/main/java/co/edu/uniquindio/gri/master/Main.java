package co.edu.uniquindio.gri.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import co.edu.uniquindio.gri.controller.Constantes;
import co.edu.uniquindio.gri.controller.GrupoController;
import co.edu.uniquindio.gri.dao.GrupoDAO;
import co.edu.uniquindio.gri.dao.InvestigadorDAO;
import co.edu.uniquindio.gri.dao.LineasInvestigacionDAO;
import co.edu.uniquindio.gri.model.Grupo;
import co.edu.uniquindio.gri.model.GruposInves;
import co.edu.uniquindio.gri.model.Investigador;
import co.edu.uniquindio.gri.model.LineasInvestigacion;

@Component
public class Main implements CommandLineRunner{
	
	@Autowired
	GrupoDAO grupoDAO;
	
	@Autowired
	InvestigadorDAO investigadorDAO;
	
	@Autowired
    ThreadPoolTaskExecutor threadPool;
	
	@Autowired
	LineasInvestigacionDAO lineasInvestigacionDAO;
	
	public static List<Investigador> investigadores = Collections.synchronizedList(new ArrayList<Investigador>());
	
	public static List<LineasInvestigacion> lineasInvestigacionBD = Collections.synchronizedList(new ArrayList<LineasInvestigacion>());
	
	@Override
	public void run(String... arg0) throws Exception {
		
		long startTime = System.currentTimeMillis();
		long stopTime = 0;
		long elapsedTime = 0;
		
		lineasInvestigacionBD=leerLineasInvestigacion();

		List<Grupo> result = scrapData();
		
		for(int i = 0; i<result.size(); i++){
			List<GruposInves> investigadores = result.get(i).getInvestigadores();
			for(int j = 0; j<investigadores.size(); j++){
				Investigador inv = investigadores.get(j).getInvestigador();
				investigadorDAO.save(inv);
			} 
			grupoDAO.save(result.get(i));
		}
		
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.err.println(elapsedTime);
	}
	
	public List<Grupo> scrapData() {

		List<Grupo> gruposInicial = leerDataSet();
		List<String> urlSet = llenarUrlSet(gruposInicial);
		
		List<Grupo> grupos = new ArrayList<Grupo>();
		List<Future<Grupo>> futureList = new ArrayList<>();
		
		for (int i = 0; i < urlSet.size(); i++) {

			GrupoController worker = new GrupoController(urlSet.get(i), gruposInicial.get(i));
			Future<Grupo> result = threadPool.submit(worker);
				futureList.add(result);
		}

		threadPool.shutdown();
		
		for (Future<Grupo> future : futureList) {
			try {
				grupos.add(future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		return grupos;
	}
	
	public List<Grupo> leerDataSet() {
		return grupoDAO.findAll();
	}
	
	public List<LineasInvestigacion> leerLineasInvestigacion() {
		return lineasInvestigacionDAO.findAll();
	}
	
	
	public List<String> llenarUrlSet(List<Grupo> grupos){	
		
		//Identificador de un grupo especifico para realizar pruebas unitarias
		Long aux = 9219L;
		
		List<String> urlSet = new ArrayList<String>();
		for (int i = 0; i < grupos.size(); i++) {
//			for (int i = 0; i < 1; i++) {
			if(grupos.get(i).getId()==aux) {
				String cadena = "00000000000000" + grupos.get(i).getId();
				cadena = cadena.substring(cadena.length() - Constantes.LINK_GRUPLAC, cadena.length());
				String url = "https://scienti.colciencias.gov.co/gruplac/jsp/visualiza/visualizagr.jsp?nro=" + cadena;
				urlSet.add(url);
			}
		}
		return urlSet;
	}
}
