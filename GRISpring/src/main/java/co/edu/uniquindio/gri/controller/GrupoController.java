package co.edu.uniquindio.gri.controller;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import co.edu.uniquindio.gri.extractor.ExtractorApSocial;
import co.edu.uniquindio.gri.extractor.ExtractorArte;
import co.edu.uniquindio.gri.extractor.ExtractorBibliograficas;
import co.edu.uniquindio.gri.extractor.ExtractorEvaluacion;
import co.edu.uniquindio.gri.extractor.ExtractorFormacion;
import co.edu.uniquindio.gri.extractor.ExtractorGenerales;
import co.edu.uniquindio.gri.extractor.ExtractorInfoAdicional;
import co.edu.uniquindio.gri.extractor.ExtractorTecnicas;
import co.edu.uniquindio.gri.model.Grupo;
import co.edu.uniquindio.gri.model.LineasInvestigacion;
import co.edu.uniquindio.gri.model.Tipo;
import co.edu.uniquindio.gri.model.TipoProduccion;

public class GrupoController implements Callable<Grupo> {

	private String url;
	private Grupo grupo;

	public GrupoController(String url, Grupo grupo) {
		this.url = url;
		this.grupo = grupo;
	}

	@Override
	public Grupo call() throws Exception {

		JSoupUtil util = new JSoupUtil();
		int statusConnectionCode = util.getStatusConnectionCode(url);

		if (statusConnectionCode == 200) {

			Document document = util.getHtmlDocument(url);
			Elements entradas = document.select("body>table");
			Elements entradaNombre = document.select("span.celdaEncabezado");

			/*
			 * Extraer Nombre del Grupo de Investigación
			 */

			for (Element elem : entradaNombre) {
				if (elem.toString().contains("span class")) {
					grupo.setNombre(elem.text().toUpperCase());
				}
			}

			extraerDatos(entradas);

		} else {
			System.out.println("El Status Code no es OK es: " + statusConnectionCode);
		}
		return grupo;
	}

	public void extraerDatos(Elements entradas) {
		ArrayUtils utils = new ArrayUtils();
		ExtractorGenerales extractor = new ExtractorGenerales();
		ExtractorBibliograficas extractorBibliograficas = new ExtractorBibliograficas();
		ExtractorTecnicas extractorTecnicas = new ExtractorTecnicas();
		ExtractorApSocial extractorApSocial = new ExtractorApSocial();
		ExtractorFormacion extractorFormacion = new ExtractorFormacion();
		ExtractorEvaluacion extractorEvaluacion = new ExtractorEvaluacion();
		ExtractorInfoAdicional extractorInfoAdicional = new ExtractorInfoAdicional();
		ExtractorArte extractorArte = new ExtractorArte();

		/*
		 * Extraer Datos Generales del grupo
		 */
		ArrayList<String> elemInfoGeneral = new ArrayList<>();
		for (Element elem : entradas) {
			if (elem.text().startsWith("Datos básicos")) {
				ArrayList<String> elemDatos = utils.ordenarArreglo(elem.toString());
				elemInfoGeneral.addAll(elemDatos);

			} else if (elem.text().startsWith("Líneas de investigación declaradas por el grupo")) {
				ArrayList<String> elemLineas = utils.ordenarArreglo(elem.toString());
				extractor.extraerLineasInvestigacionG(elemLineas, grupo);
			}

		}

		grupo = extractor.extraerDatosGeneralesG(grupo, elemInfoGeneral);

		for (Element elem : entradas) {

			/*
			 * Extraer Lista de integrantes activos dentro del grupo de investigacion
			 */

			if (elem.text().startsWith("Integrantes del grupo")) {
				ArrayList<String> elemIntegrantes = new ArrayList<>();
				elemIntegrantes.add(elem.toString());
				elemIntegrantes = utils.limpiarIntegrantes(elemIntegrantes);
				extractor.extraerIntegrantes(elemIntegrantes, grupo);

			}

			/*
			 * Extraer Producciones bibliograficas
			 */

			else if (elem.text().startsWith("Artículos publicados")) {
				ArrayList<String> elemArticulos = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerArticulosG(elemArticulos, grupo);

			} else if (elem.text().startsWith("Libros")) {
				ArrayList<String> elemLibros = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerLibrosG(elemLibros, grupo);

			} else if (elem.text().startsWith("Capítulos de libro publicados")) {
				ArrayList<String> elemCapLibros = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerCapLibrosG(elemCapLibros, grupo);

			} else if (elem.text().startsWith("Documentos de trabajo")) {
				ArrayList<String> elemDocumentosTrabajo = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerDocumentosTrabajoG(elemDocumentosTrabajo, grupo);

			} else if (elem.text().startsWith("Otra publicación divulgativa")) {
				ArrayList<String> elemOtraProdBibliografica = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerOtraProdBibliograficaG(elemOtraProdBibliografica, grupo);

			} else if (elem.text().startsWith("Otros artículos publicados")) {
				ArrayList<String> elemOtroArticulo = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerOtroArticuloG(elemOtroArticulo, grupo);

			} else if (elem.text().startsWith("Otros Libros publicados")) {
				ArrayList<String> elemOtroLibro = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerOtroLibroG(elemOtroLibro, grupo);

			} else if (elem.text().startsWith("Traducciones")) {
				ArrayList<String> elemTraduccion = utils.ordenarArreglo(elem.toString());
				extractorBibliograficas.extraerTraduccionesG(elemTraduccion, grupo);

			}

			/*
			 * Extraer Producciones tecnicas
			 */

			else if (elem.text().startsWith("Cartas, mapas o similares")) {
				ArrayList<String> elemMapas = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_CARTA_MAPA, Constantes.CARTA_MAPA, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemMapas, tipo, grupo);

			} else if (elem.text().startsWith("Consultorías científico tecnológicas e Informes técnicos")) {
				ArrayList<String> elemConsultorias = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_CONSULTORIA, Constantes.CONSULTORIA, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemConsultorias, tipo, grupo);

			} else if (elem.text().startsWith("Diseños industriales")) {
				ArrayList<String> elemDisenios = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_DISEÑO_INDUSTRIAL, Constantes.DISEÑO_INDUSTRIAL, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemDisenios, tipo, grupo);

			} else if (elem.text().startsWith("Esquemas de trazados de circuito integrado")) {
				ArrayList<String> elemEsquemas = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_ESQUEMA_IC, Constantes.ESQUEMA_IC, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemEsquemas, tipo, grupo);

			} else if (elem.text().startsWith("Innovaciones en Procesos y Procedimientos")) {
				ArrayList<String> elemInnovacionesProc = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_INNOVACION_PROCESO, Constantes.INNOVACION_PROCESO, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemInnovacionesProc, tipo, grupo);

			} else if (elem.text().startsWith("Innovaciones generadas en la Gestión Empresarial")) {
				ArrayList<String> elemInnovacionesGest = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_INNOVACION_EMPRESARIAL, Constantes.INNOVACION_EMPRESARIAL,
						tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemInnovacionesGest, tipo, grupo);

			} else if (elem.text().startsWith("Plantas piloto")) {
				ArrayList<String> elemPlantasPiloto = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_PLANTA_PILOTO, Constantes.PLANTA_PILOTO, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemPlantasPiloto, tipo, grupo);

			} else if (elem.text().startsWith("Otros productos tecnológicos")) {
				ArrayList<String> elemOtrosProdTecno = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_OTRO_PRODUCTO_TECNOLOGICO, Constantes.OTRO_PRODUCTO_TECNOLOGICO,
						tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemOtrosProdTecno, tipo, grupo);

			} else if (elem.text().startsWith("Prototipos")) {
				ArrayList<String> elemPrototipos = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_PROTOTIPO, Constantes.PROTOTIPO, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemPrototipos, tipo, grupo);

			} else if (elem.text().startsWith("Regulaciones")) {
				ArrayList<String> elemRegulaciones = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_NORMA, Constantes.NORMA, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemRegulaciones, tipo, grupo);

			} else if (elem.text().startsWith("Reglamentos técnicos")) {
				ArrayList<String> elemReglamentos = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_REGLAMENTO_TECNICO, Constantes.REGLAMENTO_TECNICO, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemReglamentos, tipo, grupo);

			} else if (elem.text().startsWith("Guias de práctica clínica")) {
				ArrayList<String> elemGuiasClinicas = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_GUIA_CLINICA, Constantes.GUIA_CLINICA, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemGuiasClinicas, tipo, grupo);

			} else if (elem.text().startsWith("Proyectos de ley")) {
				ArrayList<String> elemProyectoLey = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_PROYECTO_LEY, Constantes.PROYECTO_LEY, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemProyectoLey, tipo, grupo);

			} else if (elem.text().startsWith("Softwares")) {
				ArrayList<String> elemSoftwares = utils.ordenarArreglo(elem.toString());
				TipoProduccion tipoProduccion = new TipoProduccion(Constantes.ID_TECNICA, Constantes.TECNICA);
				Tipo tipo = new Tipo(Constantes.ID_SOFTWARE, Constantes.SOFTWARE, tipoProduccion);
				extractorTecnicas.extraerProdTecnicaG(elemSoftwares, tipo, grupo);

			} else if (elem.text().startsWith("Empresas de base tecnológica")) {
				ArrayList<String> elemEmpresasTecno = utils.ordenarArreglo(elem.toString());
				extractorTecnicas.extraerEmpresasTecnoG(elemEmpresasTecno, grupo);

			}

			/*
			 * Extraer la Apropiacion social
			 */

			else if (elem.text().startsWith("Ediciones")) {
				ArrayList<String> elemEdiciones = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerEdicionesG(elemEdiciones, grupo);

			} else if (elem.text().startsWith("Informes de investigación")) {
				ArrayList<String> elemInformes = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerInformesG(elemInformes, grupo);

			} else if (elem.text().startsWith("Redes de Conocimiento Especializado")) {
				ArrayList<String> elemRedesConocimiento = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerRedesYParticipacionG(elemRedesConocimiento, grupo);

			} else if (elem.text().startsWith("Generación de Contenido Impreso")) {
				ArrayList<String> elemContImpreso = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerContenidoImpresoMultimediaVirtualG(elemContImpreso, grupo);

			} else if (elem.text().startsWith("Generación de Contenido Multimedia")) {
				ArrayList<String> elemContMultimedia = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerContenidoImpresoMultimediaVirtualG(elemContMultimedia, grupo);

			} else if (elem.text().startsWith("Generación de Contenido Virtual")) {
				ArrayList<String> elemContVirtual = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerContenidoImpresoMultimediaVirtualG(elemContVirtual, grupo);

			} else if (elem.text().startsWith("Estrategias de Comunicación del Conocimiento")) {
				ArrayList<String> elemEstComunicacion = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerEstrategiasComunicacionG(elemEstComunicacion, grupo);

			} else if (elem.text().startsWith("Estrategias Pedagógicas para el fomento a la CTI")) {
				ArrayList<String> elemEstPedagogicasCTI = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerEstrategiasYParticipacionCtiG(elemEstPedagogicasCTI, grupo);

			} else if (elem.text().startsWith("Espacios de Participación Ciudadana")) {
				ArrayList<String> elemEspPartCiudadana = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerRedesYParticipacionG(elemEspPartCiudadana, grupo);

			} else if (elem.text().startsWith("Participación Ciudadana en Proyectos de CTI")) {
				ArrayList<String> elemPartCiudCTI = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerEstrategiasYParticipacionCtiG(elemPartCiudCTI, grupo);

			} else if (elem.text().startsWith("Eventos Científicos")) {
				ArrayList<String> elemEventos = utils.ordenarArreglo(elem.toString());
				extractorApSocial.extraerEventosG(elemEventos, grupo);

			}

			/*
			 * Extraer las Actividades de Formacion
			 */

			else if (elem.text().startsWith("Curso de Corta Duración Dictados")) {
				ArrayList<String> elemCursosCortaDuracion = utils.ordenarArreglo(elem.toString());
				extractorFormacion.extraerCursosCortosG(elemCursosCortaDuracion, grupo);

			} else if (elem.text().startsWith("Trabajos dirigidos/turorías")) {
				ArrayList<String> elemTrabajosDirigidos = utils.ordenarArreglo(elem.toString());
				extractorFormacion.extraerTrabajosDirigidosG(elemTrabajosDirigidos, grupo);

			}

			/*
			 * Extraer las Actividades de Evaluacion
			 */

			else if (elem.text().startsWith("Jurado/Comisiones evaluadoras de trabajo de grado")) {
				ArrayList<String> elemJuradoComite = utils.ordenarArreglo(elem.toString());
				extractorEvaluacion.extraerJuradoG(elemJuradoComite, grupo);

			} else if (elem.text().startsWith("Participación en comités de evaluación")) {
				ArrayList<String> elemPartComites = utils.ordenarArreglo(elem.toString());
				extractorEvaluacion.extraerPartipacionComitesG(elemPartComites, grupo);

			}

			/*
			 * Extraer Informacion adicional
			 */

			else if (elem.text().startsWith("Demás trabajos")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorInfoAdicional.extraerDemasTrabajosG(elemDemasTrabajos, grupo);

			} else if (elem.text().startsWith("Proyectos 1")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorInfoAdicional.extraerProyectosG(elemDemasTrabajos, grupo);

			}

			/*
			 * Extraer Producciones en Arte
			 */

			else if (elem.text().startsWith("Obras o productos")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorArte.extraerObrasG(elemDemasTrabajos, grupo);

			} else if (elem.text().startsWith("Registros de acuerdo de licencia")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorArte.extraerRegistrosAcuerdoG(elemDemasTrabajos, grupo);

			} else if (elem.text().startsWith("Industrias Creativas y culturales")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorArte.extraerIndustriasG(elemDemasTrabajos, grupo);

			} else if (elem.text().startsWith("Eventos artísticos")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorArte.extraerEventoArtisticoG(elemDemasTrabajos, grupo);

			} else if (elem.text().startsWith("Talleres Creativos")) {
				ArrayList<String> elemDemasTrabajos = utils.ordenarArreglo(elem.toString());
				extractorArte.extraerTallerCreativoG(elemDemasTrabajos, grupo);

			}
		}
	}
}
