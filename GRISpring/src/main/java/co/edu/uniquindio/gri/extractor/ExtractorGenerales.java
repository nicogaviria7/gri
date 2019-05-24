package co.edu.uniquindio.gri.extractor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.edu.uniquindio.gri.controller.ArrayUtils;
import co.edu.uniquindio.gri.controller.InvestigadorController;
import co.edu.uniquindio.gri.master.Main;
import co.edu.uniquindio.gri.model.Grupo;
import co.edu.uniquindio.gri.model.Idiomas;
import co.edu.uniquindio.gri.model.Investigador;
import co.edu.uniquindio.gri.model.LineasInvestigacion;

public class ExtractorGenerales {

	ArrayUtils utils = new ArrayUtils();
	
	public Grupo extraerDatosGeneralesG(Grupo grupo, ArrayList<String> elemInfoGeneral) {

		try {
			for (int i = 0; i < elemInfoGeneral.size(); i++) {

				// Extraccion del año en que se formo el grupo de investigacion

				if (elemInfoGeneral.get(i).startsWith("AÑO Y MES DE FORMACIÓN")) {
					String anioFormacion = elemInfoGeneral.get(i + 1);
					grupo.setAnioFundacion(anioFormacion);
				}

				// Extraccion del Lider del grupo de investigacion

				if (elemInfoGeneral.get(i).startsWith("LÍDER")) {
					grupo.setLider(elemInfoGeneral.get(i + 1));
				}

				// Extraccion de la categoria del grupo de investigacion

				if (elemInfoGeneral.get(i).startsWith("CLASIFICACIÓN")) {
					grupo.setCategoria(elemInfoGeneral.get(i + 1));
					if (grupo.getCategoria().equalsIgnoreCase("ÁREA DE CONOCIMIENTO")) {
						grupo.setCategoria("SIN CATEGORÍA");
					}
				}

				// Extraccion del area de conocimiento que cobija al grupo de
				// investigacion

				if (elemInfoGeneral.get(i).startsWith("ÁREA DE CONOCIMIENTO")) {
					grupo.setAreaConocimiento(elemInfoGeneral.get(i + 1));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return grupo;

	}
	
	public void extraerLineasInvestigacionG(ArrayList<String> elem, Grupo grupo) {
		
		ArrayList<LineasInvestigacion> lineas = new ArrayList<>();
		
		for (int i = 0; i < elem.size(); i++) {
			
			if (elem.get(i).contains(".-")) {
				String nomLinea = elem.get(i).substring(elem.get(i).indexOf(".- ") + 3);
				nomLinea = StringUtils.stripAccents(nomLinea);
				nomLinea = nomLinea.trim();
				
				int posAux =utils.BuscarLineasRepetidas(Main.lineasInvestigacionBD, nomLinea);
				
				if(posAux==-1) {
					LineasInvestigacion lineaInvestigacion = new LineasInvestigacion();
					lineaInvestigacion.setNombre(nomLinea);
					lineas.add(lineaInvestigacion);
					Main.lineasInvestigacionBD.add(lineaInvestigacion);
				}else {
					lineas.add(Main.lineasInvestigacionBD.get(posAux));
				}
					
			}
			
		}
			grupo.setLineasInvestigacion(lineas);		
	}

	/**
	 * Metodo que extrae la lista de investigadores y sus respectivos links
	 * 
	 * @param elem
	 * @param grupo
	 */
	public void extraerIntegrantes(ArrayList<String> elem, Grupo grupo) {

		String link = "";

		for (int i = 0; i < elem.size(); i++) {
			if (elem.get(i).contains(".-")) {
				link = elem.get(i + 1);
				Investigador auxInvestigador = new Investigador();
				if (elem.get(i + 3).contains("Actual") || elem.get(i + 4).contains("Actual")
						|| elem.get(i + 5).contains("Actual")) {
					InvestigadorController investigadorController = new InvestigadorController(link, auxInvestigador);
					auxInvestigador = investigadorController.extraer("ACTUAL");
					System.out.println(auxInvestigador.getNombre());
					grupo.addInvestigador(auxInvestigador, "ACTUAL");
					Main.investigadores.add(auxInvestigador);
				} else {
					InvestigadorController investigadorController = new InvestigadorController(link, auxInvestigador);
					auxInvestigador = investigadorController.extraer("NO ACTUAL");
					grupo.addInvestigador(auxInvestigador, "NO ACTUAL");
				}
			}
		}
	}

	public Investigador extraerDatosGeneralesI(Investigador investigador, ArrayList<String> elemInfoPersonal, long id,
			String estado) {

		boolean pertenece = false;
		investigador.setId(id);

		// Si no posee datos personales, el perfil es privado.
		if (elemInfoPersonal.size() == 0) {
			
			investigador.setNombre("PERFIL PRIVADO");
			investigador.setNombreInvestigadorAux(investigador.getNombre());
			investigador.setId(id);
			investigador.setCategoria("SIN CATEGORÍA");
			investigador.setNivelAcademico("NO ESPECIFICADO");
			investigador.setPertenencia("NO ESPECIFICADO");

		} else {
			try {

				ArrayList<LineasInvestigacion> lineas = new ArrayList<>();
				String nomLinea = "";

				for (int i = 0; i < elemInfoPersonal.size(); i++) {

					// Extraccion de la categoria del investigador

					if (elemInfoPersonal.get(i).startsWith("CATEGORÍA")) {
						String registro = elemInfoPersonal.get(i + 1);
						String categoria = registro.substring(0, registro.indexOf('(') - 1);
						investigador.setCategoria(categoria);
					}

					// Extraccion del nombre del investigador

					if (elemInfoPersonal.get(i).equals("NOMBRE")) {
						investigador.setNombre(elemInfoPersonal.get(i + 1));
						investigador.setNombreInvestigadorAux(investigador.getNombre());
					}

					// Extraccion de la formacion academica
					if (elemInfoPersonal.get(i).startsWith("FORMACIÓN ACADÉMICA")) {
						investigador.setNivelAcademico(elemInfoPersonal.get(i + 1));
					}

//					try {
//						if (elemInfoPersonal.size() >= i && elemInfoPersonal.get(i).contains(",")
//								&& elemInfoPersonal.get(i + 2).equals("SI")) {
//							nomLinea = elemInfoPersonal.get(i).substring(0, elemInfoPersonal.get(i).length() - 1);
//							LineasInvestigacion lineaInvestigacion = new LineasInvestigacion();
//							nomLinea = StringUtils.stripAccents(nomLinea);
//							nomLinea = nomLinea.trim();
//							lineaInvestigacion.setNombre(nomLinea);
//							lineas.add(lineaInvestigacion);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}

					try {
						if (estado.equals("ACTUAL")) {
							if (elemInfoPersonal.get(i).equals("UNIVERSIDAD DEL QUINDIO UNIQUINDIO")
									&& elemInfoPersonal.get(i + 2).contains("ACTUAL")) {
								pertenece = true;
								investigador.setPertenencia("INVESTIGADOR INTERNO");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (investigador.getCategoria() == null) {
					investigador.setCategoria("SIN CATEGORÍA");
				}

				if (investigador.getNivelAcademico() == null) {
					investigador.setNivelAcademico("NO ESPECIFICADO");
				}

				investigador.setLineasInvestigacion(lineas);

				if (estado.equals("ACTUAL")) {
					if (pertenece == false) {
						investigador.setPertenencia("INVESTIGADOR EXTERNO");
					}
				} else {
					investigador.setPertenencia("NO ESPECIFICADA");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return investigador;
	}
	
	/**
	 * Metodo que extrae los idiomas con los que el investigador esta familiarizado
	 * 
	 * @param elem,
	 *            Lista de elementos que contiene los idiomas del investigador
	 */
	public void extraerIdiomas(ArrayList<String> elem, Investigador investigador) {

		ArrayList<Idiomas> auxIdiomasTemp = new ArrayList<>();

		for (int i = 5; i < elem.size() - 1; i++) {
			try {
				Idiomas idioma = new Idiomas();

				idioma.setIdioma(elem.get(i));
				idioma.setHabla(elem.get(i + 1));
				idioma.setEscribe(elem.get(i + 2));
				idioma.setLee(elem.get(i + 3));
				idioma.setEntiende(elem.get(i + 4));
				idioma.setInvestigador(investigador);
				auxIdiomasTemp.add(idioma);

			} catch (Exception e) {
				Idiomas idioma = new Idiomas();

				idioma.setIdioma("N/D");
				idioma.setHabla("N/D");
				idioma.setEscribe("N/D");
				idioma.setLee("N/D");
				idioma.setEntiende("N/D");
				idioma.setInvestigador(investigador);
				auxIdiomasTemp.add(idioma);

			}
			i = i + 4;

		}
		List<Idiomas> auxIdiomas = investigador.getIdiomas();
		if (auxIdiomas == null) {
			investigador.setIdiomas(auxIdiomasTemp);
		} else {
			auxIdiomas.addAll(auxIdiomasTemp);
			investigador.setIdiomas(auxIdiomas);
		}
	}

public void extraerLineasInvestigacionI(ArrayList<String> elem, Investigador investigador) {
		
		ArrayList<LineasInvestigacion> lineas = new ArrayList<>();
		
		for (int i = 0; i < elem.size(); i++) {
			
			if (elem.get(i).contains(".-")) {
				String nomLinea = elem.get(i).substring(elem.get(i).indexOf(".- ") + 3);
				nomLinea = StringUtils.stripAccents(nomLinea);
				nomLinea = nomLinea.trim();
				
				int posAux =utils.BuscarLineasRepetidas(Main.lineasInvestigacionBD, nomLinea);
				
				if(posAux==-1) {
					LineasInvestigacion lineaInvestigacion = new LineasInvestigacion();
					lineaInvestigacion.setNombre(nomLinea);
					lineas.add(lineaInvestigacion);
					Main.lineasInvestigacionBD.add(lineaInvestigacion);
				}else {
					lineas.add(Main.lineasInvestigacionBD.get(posAux));
				}
					
			}
			
		}
		investigador.setLineasInvestigacion(lineas);		
	}

}
