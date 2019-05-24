package co.edu.uniquindio.gri.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import co.edu.uniquindio.gri.model.Investigador;
import co.edu.uniquindio.gri.model.LineasInvestigacion;
import co.edu.uniquindio.gri.model.Produccion;
import co.edu.uniquindio.gri.model.ProduccionB;
import co.edu.uniquindio.gri.model.ProduccionBGrupo;
import co.edu.uniquindio.gri.model.ProduccionGrupo;

public class ArrayUtils {

	FuzzyMatch fuzzyMatch = new FuzzyMatch();

	public ArrayList<String> ordenarArreglo(String elem) {
		ArrayList<String> elementos = new ArrayList<>();
		elementos.add(elem);
		return limpiar(elementos);
	}

	/**
	 * Método que elimina las etiquetas y caracteres especiales en la lista que
	 * tiene la estructura de la pagina web
	 * 
	 * @param elementos,
	 *            lista que contiene la estructura textual de la pagina web
	 * @return Lista con la estructura de la pagina web sin las etiquetas y los
	 *         caracteres especiales
	 */
	public ArrayList<String> limpiar(ArrayList<String> elementos) {
		String temporal = "";
		ArrayList<String> elementosLimpio = new ArrayList<>();
		ArrayList<String> aux = new ArrayList<>();
		ArrayList<String> aux2 = new ArrayList<>();
		for (int i = 0; i < elementos.size(); i++) {
			temporal = elementos.get(i).replaceAll("\n", "");
			temporal = temporal.replaceAll("&nbsp;", " ");
			temporal = temporal.replaceAll("  ", " ");
			temporal = temporal.replaceAll("&AMP", "&");
			temporal = temporal.replaceAll("&AMP;", "&");
			temporal = temporal.replaceAll("'", "");
		}
		char[] auxiliar = temporal.toCharArray();
		int posI = 0;
		int posF = 0;
		for (int j = 0; j < auxiliar.length; j++) {
			if ((auxiliar[j] == 'h') && (auxiliar[j + 1] == 'r') && (auxiliar[j + 2] == 'e')) {

				boolean primeraComilla = false;
				boolean segundaComilla = false;

				for (int i = j; i < auxiliar.length; i++) {
					if (auxiliar[i] == '"' && !primeraComilla) {
						auxiliar[i] = '>';
						primeraComilla = true;
						i++;
					}
					if (auxiliar[i] == '"' && !segundaComilla) {
						auxiliar[i] = '<';
						segundaComilla = true;
						break;
					}
				}
			}
			if (auxiliar[j] == '>') {
				posI = j;
				for (int i = j; i < auxiliar.length; i++) {
					if (auxiliar[i] == '<') {
						posF = i;
						elementosLimpio.add(temporal.substring(posI, posF));
						j = i;
						break;
					}
				}
			}
		}
		for (int i = 0; i < elementosLimpio.size(); i++) {
			temporal = "";
			temporal = elementosLimpio.get(i).replaceAll(">", " ");
			aux.add(temporal);

		}
		for (int i = 0; i < aux.size(); i++) {
			if (!aux.get(i).equals(" ")) {
				temporal = "";
				temporal = aux.get(i).substring(1);
				aux2.add(temporal.trim());
			}

		}
		aux.clear();

		for (int i = 0; i < aux2.size(); i++) {
			temporal = aux2.get(i);
			if (!temporal.equals("")) {
				aux.add(temporal.trim().toUpperCase());
			}

		}

		elementosLimpio = aux;
		return elementosLimpio;
	}

	/**
	 * Metodo que elimina las etiquetas y caracteres especiales en la lista que
	 * tiene la estructura de la pagina web del cvlac de cada investigador
	 * 
	 * @param elem,
	 *            lista que contiene la estructura textual de la pagina web
	 * @return Lista con la estructura de la pagina web sin las etiquetas y los
	 *         caracteres especiales
	 */
	public ArrayList<String> limpiarIntegrantes(ArrayList<String> elem) {
		String temporal = "";
		ArrayList<String> elementosLimpio = new ArrayList<>();
		ArrayList<String> aux = new ArrayList<>();
		ArrayList<String> aux2 = new ArrayList<>();
		for (int i = 0; i < elem.size(); i++) {
			temporal = elem.get(i).replaceAll("\n", "");
			temporal = temporal.replaceAll("&nbsp;", " ");
			temporal = temporal.replaceAll("  ", " ");
			temporal = temporal.replaceAll("&AMP", "&");
			temporal = temporal.replaceAll("&AMP;", "&");
			temporal = temporal.replaceAll("'", "");
		}
		char[] auxiliar = temporal.toCharArray();
		int posI = 0;
		int posF = 0;
		for (int j = 0; j < auxiliar.length; j++) {
			if ((auxiliar[j] == 'h') && (auxiliar[j + 1] == 'r') && (auxiliar[j + 2] == 'e')) {

				boolean primeraComilla = false;
				boolean segundaComilla = false;

				for (int i = j; i < auxiliar.length; i++) {
					if (auxiliar[i] == '"' && !primeraComilla) {
						auxiliar[i] = '>';
						primeraComilla = true;
						i++;
					}
					if (auxiliar[i] == '"' && !segundaComilla) {
						auxiliar[i] = '<';
						segundaComilla = true;
						break;
					}
				}
			}
			if (auxiliar[j] == '>') {
				posI = j;
				for (int i = j; i < auxiliar.length; i++) {
					if (auxiliar[i] == '<') {
						posF = i;
						elementosLimpio.add(temporal.substring(posI, posF));
						j = i;
						break;
					}
				}
			}
		}
		for (int i = 0; i < elementosLimpio.size(); i++) {
			temporal = "";
			temporal = elementosLimpio.get(i).replaceAll(">", " ");
			aux.add(temporal);

		}
		for (int i = 0; i < aux.size(); i++) {
			if (!aux.get(i).equals(" ")) {
				temporal = "";
				temporal = aux.get(i).substring(1);
				aux2.add(temporal.trim());
			}

		}
		aux.clear();

		for (int i = 0; i < aux2.size(); i++) {
			temporal = aux2.get(i);
			if (!temporal.equals("")) {
				aux.add(temporal.trim());
			}

		}

		elementosLimpio = aux;
		return elementosLimpio;
	}

	/**
	 * Metodo Utilizado para extraer los autores de una publicacion, en casos
	 * especiales donde la extraccion no es clara
	 * 
	 * @param autores,
	 *            Cadena donde se encuentran los nombres de los autores
	 * @return Cadena con el nombre de los autores
	 */
	public String verificarAutores(String autores, Investigador investigador) {
		String autoresFinal = "";
		String[] aux = autores.split(",");
		for (int j = 0; j < aux.length; j++) {
			int espacios = StringUtils.countMatches(aux[j], " ");
			if (espacios >= 1 && espacios <= 3) {
				autoresFinal += ", " + aux[j];
			}
		}
		if (autoresFinal.equals("")) {
			autoresFinal = investigador.getNombreInvestigadorAux();
		} else {
			autoresFinal = autoresFinal.substring(2);
		}
		return autoresFinal;
	}

	public String extraerAnio(String cadena) {

		String anio;

		if (cadena.contains("ISSN: 1") || cadena.contains("ISSN: 2")) {
			String inicioCadena = cadena.substring(0, cadena.indexOf("ISSN: "));
			String finCadena = cadena.substring(cadena.indexOf("ISSN: ") + 15, cadena.length());
			cadena = inicioCadena + finCadena;
		}

		if (cadena.contains("ISBN: ")) {
			String inicioCadena = cadena.substring(0, cadena.indexOf("ISBN: "));
			try {
				String finCadena = cadena.substring(cadena.indexOf("ISBN: ") + 20, cadena.length());
				cadena = inicioCadena + finCadena;
			} catch (Exception e) {
				cadena = inicioCadena;
			}
		}

		if (cadena.contains("DOI: ")) {
			cadena = cadena.substring(0, cadena.indexOf("DOI: "));
		}

		Pattern p = Pattern.compile("(\\.|,|:|\\s|\\/|-)\\d{2}(91|02)([^\\(])");
		Matcher m = p.matcher(new StringBuilder(cadena + " ").reverse());
		if (m.find()) {
			anio = new StringBuilder(m.group(0)).reverse() + "";
			anio = anio.substring(1, anio.length() - 1);
		} else {
			anio = "N/D";
		}

		if (StringUtils.isNumeric(anio)) {
			int anioAux = Integer.parseInt(anio);
			Calendar fecha = Calendar.getInstance();
			if (anioAux > fecha.get(Calendar.YEAR)) {
				anio = extraerAnio(cadena.substring(0, (cadena.length() - m.end())));
			}
		}
		return anio;
	}

	/**
	 * 
	 * @param elem
	 * @return
	 */
	public void identificarRepetidosG(ArrayList<ProduccionGrupo> elem, ProduccionGrupo produccion) {
		String referencia = produccion.getReferencia();
		String anio = produccion.getAnio();
		for (int j = 0; j < elem.size(); j++) {
			String referenciaAux = elem.get(j).getReferencia();
			String anioAux = elem.get(j).getAnio();
			if (anioAux.equals(anio) && fuzzyMatch.getRatio(referencia, referenciaAux) >= Constantes.FUZZY_MATCH) {
				produccion.setRepetido("SI");
				break;
			}
		}
	}

	/**
	 * 
	 * @param elem
	 * @return
	 */
	public void identificarRepetidosBibliograficosG(ArrayList<ProduccionBGrupo> elem, ProduccionBGrupo produccion) {
		String referencia = produccion.getReferencia();
		String anio = produccion.getAnio();
		String id = produccion.getIdentificador();
		for (int j = 0; j < elem.size(); j++) {
			String referenciaAux = elem.get(j).getReferencia();
			String anioAux = elem.get(j).getAnio();
			String idAux = elem.get(j).getIdentificador();
			if (id.equals(idAux)) {
				if (anioAux.equals(anio) && fuzzyMatch.getRatio(referencia, referenciaAux) >= Constantes.FUZZY_MATCH) {
					produccion.setRepetido("SI");
					break;
				}
			}
		}
	}

	/**
	 * Método que identifica las producciones repetidas de los investigadores,
	 * basándose en el algoritmo fuzzy wuzzy
	 * 
	 * @param elem, lista de las producciones del investigador
	 * @param produccion, produccion a comparar
	 */
	public void identificarRepetidosI(ArrayList<Produccion> elem, Produccion produccion) {
		String referencia = produccion.getReferencia();
		String anio = produccion.getAnio();
		for (int j = 0; j < elem.size(); j++) {
			String referenciaAux = elem.get(j).getReferencia();
			String anioAux = elem.get(j).getAnio();
			if (anioAux.equals(anio) && fuzzyMatch.getRatio(referencia, referenciaAux) >= Constantes.FUZZY_MATCH) {
				produccion.setRepetido("SI");
				break;
			}
		}
	}

	/**
	 * 
	 * @param elem
	 * @return
	 */
	public void identificarRepetidosBibliograficosI(ArrayList<ProduccionB> elem, ProduccionB produccion) {
		String referencia = produccion.getReferencia();
		String anio = produccion.getAnio();
		String id = produccion.getIdentificador();
		for (int j = 0; j < elem.size(); j++) {
			String referenciaAux = elem.get(j).getReferencia();
			String anioAux = elem.get(j).getAnio();
			String idAux = elem.get(j).getIdentificador();
			if (id.equals(idAux)) {
				if (anioAux.equals(anio) && fuzzyMatch.getRatio(referencia, referenciaAux) >= Constantes.FUZZY_MATCH) {
					produccion.setRepetido("SI");
					break;
				}
			}
		}
	}
	
	public int BuscarLineasRepetidas(List<LineasInvestigacion> elem, String nombreLinea) {
		for (int j = 0; j < elem.size(); j++) {
			String nombreAux = elem.get(j).getNombre();
			if (fuzzyMatch.getRatio(nombreLinea, nombreAux) >= Constantes.FUZZY_MATCH) {
				return j;
			}
		}
		return -1;
	}
}
