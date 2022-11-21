package com.pasajeros;

import java.util.Scanner;

public class App {

	private String[] pasajerosTodos = new String[30];
	private String[] pasajerosCatA = new String[5];
	private String[] pasajerosCatB = new String[5];
	private String[] pasajerosCatC = new String[5];
	private String[] pasajerosCatD = new String[5];
	private String[] pasajerosCatE = new String[5];
	private String[] pasajerosCatF = new String[5];

	private String[] conexiones = new String[90];
	private String[] lineas = new String[50];
	private String[] paradas = new String[30];

	private Scanner input = new Scanner(System.in);
	private String d = "@"; // delimitador
	private String validCi = "^[1-9]\\d{7}$";
	private String validName = "^[a-zA-Z ]{5,20}$"; // TODO: mejorar regexp.
	private String validTel = "^[0-9]\\d{8}$"; // solo acepta celular o telefonos sin guiones
	private String validCode = "^[A-Z0-9]{9}$";

	public static void main(String[] args) {
		App app = new App();
		app.menuPrincipal();
	}

	public void menuPrincipal() {
		int userInput;
		Boolean exit = false;
		do {
			System.out.printf(style("%nSeleccione un item del menú.", "b")
					+ "%n1: Menu de pasajeros. %n2: Menu de paradas. %n3: Menu de conexiones. %n0: Salir del programa.%n");
			userInput = input.nextInt();
			input.nextLine();
			switch (userInput) {
			case 1:
				menuPasajeros();
				break;
			case 2:
				menuParadas();
				break;
			case 3:
				menuConexion();
				break;
			case 0:
				exit = true;
				break;
			}
		} while (!exit);
	}

	/**
	 * Menu de CONEXIONES
	 * Registrar conexion, linea
	 * Mostrar paradas por mayor o menor distancia
	 */

	public void menuConexion() {
		Boolean exit = false;
		int userInput;
		do {
			System.out.printf(style("Menu de conexiones.", "b")
					+ "%n1: registrar conexion. %n2: registrar linea. %n3 mostrar conexion con mayor distancia. %n4 mostrar conexion con menor distancia. %n0. Volver al menu principal%n");
			userInput = input.nextInt();
			input.nextLine();
			switch (userInput) {
			case 1: {
				registrarConexion();
				break;
			}
			case 2: {
				registrarLinea();
				break;
			}
			case 3: {
				mostrarParadaConMayorDistancia();
				break;
			}
			case 4: {
				mostrarParadaConMenorDistancia();
				break;
			}
			case 0: {
				exit = true;
				break;
			}
			}
		} while (!exit);
		menuPrincipal();
	}

	public void mostrarParadaConMayorDistancia() {
		if(conexiones[2] == null) {
			System.out.printf(style("ERROR: No existen conexiones en la base de datos, registre algunas para comparar.%n", "error"));
			menuConexion();
		}
		Double maxPrev = 0.0;
		int maxI = 0;
		for (int i = 2; i < conexiones.length; i += 3) {
			Double num = Double.parseDouble(conexiones[i]);
			if (num > maxPrev) {
				maxPrev = num;
				maxI = i;
			}
		}
		System.out.printf(style("La conexion con mayor distancia entre paradas es:%n", "success") + style("Origen: ", "b") + conexiones[maxI - 2] + style(" Destino : ", "b") + conexiones[maxI - 1] + style(" Distancia: ", "b") + conexiones[maxI] + "km.");
		menuPrincipal();
	}

	public void mostrarParadaConMenorDistancia() {
		if(conexiones[2] == null) {
			System.out.printf(style("ERROR: No existen conexiones en la base de datos, registre algunas para comparar.%n", "error"));
			menuConexion();
		}
		Double minPrev = 10000.0;
		int minI = 0;
		for (int i = 2; i < conexiones.length; i += 3) {
			Double num = Double.parseDouble(conexiones[i]);
			if (num < minPrev) {
				minPrev = num;
				minI = i;
			}
		}
		
		System.out.printf(style("La conexion con menor distancia entre paradas es:%n", "success") + style("Origen: ", "b") + conexiones[minI - 2] + style(" Destino : ", "b") + conexiones[minI - 1] + style(" Distancia: ", "b") + conexiones[minI] + "km.");
		menuPrincipal();
	}

	public void registrarConexion() {
		if (conexiones[conexiones.length - 3] != null) {
			System.out.println(style("No hay cupos para mas conexiones.", "error"));
			menuPrincipal();
		}
		
		if(paradas[0] == null && paradas[2] == null) {
			System.out.printf(style("ERROR: No existen paradas, debe agregar al menos 2 paradas que conectar antes de registrar una conexion.%n", "error"));
			menuConexion();
		}
		
		// ingresar / validar datos
		String origen, destino, km;
		Boolean existeOrigen, existeDestino, isValidKm;

		System.out.printf("Ingrese el codigo de la parada de origen.%n");
		origen = input.nextLine();
		existeOrigen = existeParada(origen);

		if (!existeOrigen) {
			System.out.printf(style("No existe ninguna parada registrada con el codigo proporcionado.%n", "error"));
			menuConexion();
		}

		System.out.println("Ingrese el codigo de la parada destino");
		destino = input.nextLine();
		existeDestino = existeParada(destino);

		if (!existeDestino) {
			System.out.printf(style("No existe ninguna parada registrada con el codigo proporcionado.", "error"));
			menuConexion();
		}

		System.out.println("Ingrese la distancia en kilometros");
		km = input.nextLine();
		isValidKm = validDecimal(km);

		if (!isValidKm) {
			System.out.println("El dato ingresado es incorrecto intentelo nuevamente.");
			menuConexion();
		}

		// Checkear si existe la conexion
		Boolean existeConexion = false;
		int iConexion = 0;
		while (iConexion < conexiones.length -3 && !existeConexion) {
			if (origen.equals(conexiones[iConexion]) && destino.equals(conexiones[iConexion + 1])) {
				existeConexion = true;
			}
			iConexion += 3;
		}

		if (!existeConexion) {
			// registrarla
			int idx = encontrarIndice(conexiones);
			conexiones[idx] = origen;
			conexiones[idx + 1] = destino;
			conexiones[idx + 2] = km;
			System.out.println(style("La conexion se ha registrado correctamente.", "success"));
		} else {
			System.out.println(style("La conexion ya existe en la base de datos.", "error"));
		}
	}

	public void registrarLinea() {
		if(conexiones[0] == null) {
			System.out.printf(style("ERROR: No existen conexiones, debe agregar una conexion antes de registrar una linea.%n", "error"));
			menuConexion();
		}
		
		String origen, destino, linea, minutos, costo;
		Boolean existeConexion = false;
		Boolean existeLinea = false;

		System.out.printf("Ingrese el codigo de la parada de origen.%n");
		origen = input.nextLine();
		

		System.out.printf("Ingrese el codigo de la parada de destino.%n");
		destino = input.nextLine();

		// validar conexion
		int idx = 0;
		while (idx <= conexiones.length - 3 && !existeConexion) {
			if (conexiones[idx].equals(origen) && conexiones[idx + 1].equals(destino)) {
				existeConexion = true;
			}
			idx += 3;
		}
		
		
		System.out.printf("Ingrese el codigo de la linea. (10 digitos letras y numeros)%n");
		linea = input.nextLine();
		linea = linea.toUpperCase();
		// validar linea
//		Boolean isValidLinea = linea.matches(validCode); No funciona
		
//		if(!isValidLinea) {
//			System.out.printf(style("%nEl codigo ingresado no es valido.%n", "error"));
//			System.out.println(linea);
//			System.out.println(isValidLinea);
//			menuConexion();
//		}
		
		if (lineas[0] != null) {
			for (int i = 0; i < lineas.length; i++) {
			String[] registro = lineas[i].split(d);
			if (registro[2].equals(linea)) {
				existeLinea = true;
			}
		}
		}
		
		
		System.out.println("Ingrese el tiempo de viaje.%n");
		minutos = input.nextLine();
		Boolean isValidMins = validDecimal(minutos);
		
		System.out.println("Ingrese el costo del pasaje.%n");
		costo = input.nextLine();
		Boolean isValidCosto = validDecimal(costo);
		// validar minutos y costo.
		if(!isValidMins || !isValidCosto) {
			System.out.printf(style("%nDatos invalidos.%n", "error"));
			menuConexion();
		}

		if (existeConexion && !existeLinea) {
			int indice = encontrarIndice(lineas);
			lineas[indice] = origen + d + destino + d + linea + d + minutos + costo;
			System.out.println(style("La linea se registro correctamente", "success"));
			menuPrincipal();
		}
	}

	/**
	 * Menu de PARADAS
	 * registrar, mostrar, buscar, validar existencia
	 */

	public void menuParadas() {
		Boolean exit = false;
		int userInput;
		do {
			System.out.printf(style("%nSeleccione un item del menu.", "b")
					+ "%n1: Registrar parada. %n2: Mostrar todas las paradas. %n0: Volver al menu principal.%n");
			userInput = input.nextInt();
			input.nextLine();
			switch (userInput) {
			case 1:
				registrarParada();
				break;
			case 2:
				mostrarParada();
				break;
			case 0: 
				exit = true;
				break;
			}
		} while (!exit);
		menuPrincipal();
	}

	public Boolean existeParada(String code) {
		for (int i = 0; i < paradas.length - 1; i += 2) {
			if (paradas[i].equals(code)) {
				return true;
			}
		}

		return false;
	}

	public void mostrarParada() {
		if (paradas[0] == null) {
			System.out.println(style("No existe ninguna parada registrada", "error"));
		} else {
			System.out.printf(style("Lista de paradas registradas:%n", "success"));
			for (int i = 0; i < paradas.length; i += 2) {
				System.out.printf(style("ID: ", "b") + paradas[i] + style(" Parada: ", "b") + paradas[i + 1] + "%n");
			}
		}
		menuParadas();
	}

	public void buscarParada() {
		if (paradas[0] == null) {
			System.out.println(style("No hay paradas registradas en el sistema.", "error"));
			menuParadas();
		}

		String query;

		System.out.printf(style("%nPara buscar una parada ingrese el codigo de la parada%n", "b"));
		query = input.nextLine();

		Boolean encontrado = false;
		int i = 0;
		while (!encontrado) {
			if (paradas[i] != null && i < paradas.length) {
				if (query.equals(paradas[i])) {
					encontrado = true;
					System.out.println(style("Datos de la parada:", "success"));
					System.out.println(
							style("Codigo: ", "b") + paradas[i] + style(" Nombre: ", "b") + paradas[i + 1] + ".");
				}
				i += 2;
			} else {
				encontrado = true;
				System.out.println(style("No existen coincidencias", "error"));
			}
		}
	}

	public void registrarParada() {
		if (paradas[paradas.length - 1] != null) {
			System.out.println(style("No hay cupos para registrar una parada.", "error"));
		} else {
			String codigo, parada;
			System.out.printf("%nIngrese el codigo de la parada. Debe ser de 10 digitos entre letras y numeros.%n");
			codigo = input.nextLine();
			codigo = codigo.toUpperCase();
			Boolean isValidCode = codigo.matches(validCode);
			
			if (!isValidCode) {
				System.out.println(style("ERROR: Codigo invalido.", "error"));
				menuParadas();
			}
			
			System.out.printf("%nIngrese el nombre de la parada.%n");
			parada = input.nextLine();
			parada = parada.toUpperCase();
			if (parada.length() < 1) {
				System.out.printf(style("%nERROR: El nombre de la parada no puede estar vacio.%n", "error"));
				menuParadas();
			}
			
			Boolean existe = existeParada(codigo);
			int lastIdx = 0;
			while (lastIdx < paradas.length && paradas[lastIdx] != null) {
				lastIdx++;
			}

			if (!existe) {
				paradas[lastIdx] = codigo;
				paradas[lastIdx + 1] = parada;
				System.out.println(style("La parada ", "success") + parada + " con el identificador " + codigo + style(" se ha registrado con exito.", "success"));

			} else {
				System.out.println(style("La parada ya existe en el sistema.", "error"));
			}
		}
		menuParadas();
	}

	/**
	 * Menu de PASAJEROS:
	 * registrar, buscar y mostrar
	 */

	public void menuPasajeros() {
		Boolean exit = false;
		int userInput;
		do {
			System.out.printf(style("%nSeleccione un item del menu.", "b")
					+ "%n1: Mostrar todos los pasajeros. %n2: Buscar un pasajero. %n3: Registrar un pasajero. %n0: Volver al menu principal.%n");
			userInput = input.nextInt();
			input.nextLine();
			switch (userInput) {
			case 1:
				mostrarPasajeros(pasajerosTodos);
				break;
			case 2:
				buscarPasajero();
				break;
			case 3:
				registrarPasajero();
				break;
			case 0:
				exit = true;
				break;
			}
		} while (!exit);
		menuPrincipal();
	}

	public void buscarPasajero() {
		String query;
		System.out.printf(style("Para buscar un pasajero ingrese:", "b")
				+ "%n - numero de cedula sin puntos ni guiones. %n - O el nombre del pasajero. %n - O la categoria del pasajero para verlos todos. %nIngrese el termino de busqueda ahora o ingrese 0 para volver al menu de pasajeros:%n");

		query = input.nextLine();
		switch (query.toUpperCase()) {
		case "A":
			mostrarPasajeros(pasajerosCatA);
		case "B":
			mostrarPasajeros(pasajerosCatB);
		case "C":
			mostrarPasajeros(pasajerosCatC);
		case "D":
			mostrarPasajeros(pasajerosCatD);
		case "E":
			mostrarPasajeros(pasajerosCatE);
		case "F":
			mostrarPasajeros(pasajerosCatF);
		case "0":
			menuPasajeros();
		default: {
			if (pasajerosTodos[0] == null) {
				System.out.println(style("No hay registros que mostrar.", "error"));
			} else {
				Boolean isCi = query.matches(validCi);
				Boolean isTel = query.matches(validTel);
				Boolean isName = query.matches(validName);
				Boolean encontrado = false;
				
				if(isName) query = query.toLowerCase();
				
				int i = 0;
				while (!encontrado) {
					// evaluar length primero
					if (i < pasajerosTodos.length && pasajerosTodos[i] != null) {
						String[] registro = pasajerosTodos[i].split("@");
						
						if ((isCi && query.equals(registro[0])) || (isTel && query.equals(registro[2]))
								|| (isName && query.equals(registro[1]))) {
							encontrado = true;
							System.out.println(style("Datos del pasajero:", "success"));
							System.out.println(style("Ci: ", "b") + registro[0] + style(" Nombre: ", "b") + registro[1]
									+ style(" Telefono: ", "b") + registro[2] + style(" Categoria: ", "b") + registro[3]
									+ ".");
						}
						i++;
					} else {
						encontrado = true;
						System.out.println(style("No existen coincidencias", "error"));
					}
				}
			}
			menuPasajeros();
		}
		}
		menuPasajeros();
	}

	public void mostrarPasajeros(String[] array) {
		/**
		 * mostrar todos los pasajeros
		 */
		if (array[0] == null) {
			System.out.println(style("No existen registros", "error"));
		} else {
			System.out.println(style("Datos de los pasajeros:", "success"));
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					String[] registro = array[i].split(d);
					String mensaje = style("Ci: ", "b") + registro[0] + style(" Nombre: ", "b") + registro[1]
							+ style(" Telefono: ", "b") + registro[2] + " Categoria: " + registro[3] + ".";
					System.out.println(mensaje);
				}
			}
		}
		menuPasajeros();
	}

	public void registrarPasajero() {
		if (pasajerosTodos[pasajerosTodos.length - 1] != null) {
			System.out.println(style("No hay cupos para registrar nuevos pasajeros.", "error"));
			menuPasajeros();
		}
		/**
		 * PEDIR Y VALIDAR DATOS
		 */
		String cedula;
		String nombre;
		String categoria;
		String telefono;
		String registro;
		// CEDULA
		System.out.println("Ingrese la cédula sin puntos ni guiones:");
		cedula = input.nextLine();
		Boolean isValidCi = cedula.matches(validCi);

		if (isValidCi) {
			if (pasajerosTodos[0] != null) {
				for (int i = 0; i < pasajerosTodos.length; i++) {
					String[] item = pasajerosTodos[i].split(d);
					if (item[0] == cedula) {
						System.out.println(style("ERROR: El numero de cédula ya está registrado.", "error"));
						menuPasajeros();
					}
				}
			}

		} else {
			System.out.println(style("ERROR: debe ingresar un número de cédula válido.", "error"));
			menuPasajeros();
		}
		// NOMBRE
		System.out.println("Ingresar el nombre:");
		nombre = input.nextLine();
		Boolean isValidName = nombre.matches(validName);

		if (!isValidName) {
			System.out.println(style("ERROR: debe ingresar un nombre valido.", "error"));
			menuPasajeros();
		}
		// TELEFONO
		System.out.println("Ingresar el telefono:");
		telefono = input.next();
		Boolean isValidTel = telefono.matches(validTel);
		if (!isValidTel) {
			System.out.println(style("ERROR: debe ingresar un número de telefono válido.", "error"));
			menuPasajeros();
		}
		// CATEGORIA
		System.out.printf(style("Ingresar categoria del pasajero:", "b")
				+ "%nA: Comun. %nB: Jubilado. %nC: Pensionista. %nD: Estudiante \"A\". %nE: Estudiante \"B\". %nF: Docente.%n");
		categoria = input.nextLine();
		categoria = categoria.toUpperCase();
		Boolean isValidCat = categoria.matches("[A-F]");
		if (!isValidCat) {
			System.out.println(style("ERROR: debe ingresar una categoría válida.", "error"));
			menuPasajeros();
		}

		registro = cedula + d + nombre + d + telefono + d + categoria;

		switch (categoria) {
		case "A": {
			registrarCategoria(pasajerosCatA, registro);
			break;
		}
		case "B": {
			registrarCategoria(pasajerosCatB, registro);
			break;
		}
		case "C": {
			registrarCategoria(pasajerosCatC, registro);
			break;
		}
		case "D": {
			registrarCategoria(pasajerosCatD, registro);
			break;
		}
		case "E": {
			registrarCategoria(pasajerosCatE, registro);
			break;
		}
		case "F": {
			registrarCategoria(pasajerosCatF, registro);
			break;
		}
		default:
			menuPasajeros(); // no deberia entrar nunca en default
		}
	}

	/**
	 * METODOS EXTRA
	 * Style: Colorea salida de terminal
	 * isValidNum: valida numeros sin signo
	 * registrarCategoria, agrega pasajero a array de categoria.
	 */
	private String style(String str, String type) {
		String newStr;
		String RESET = "\033[0m";
		String BOLD = "\033[0;1m";
		String RED = "\033[1;31m";
		String GREEN = "\033[1;32m";
		switch (type) {
		case "error": {
			newStr = RED + str + RESET;
			break;
		}
		case "success": {
			newStr = GREEN + str + RESET;
			break;
		}
		default: {
			newStr = BOLD + str + RESET;
		}
		}
		return newStr;
	}

	private int encontrarIndice(String[] arr) {
		int num = -1;
		int i = 0;
		while (num < 0) {
			if (arr[i] == null) {
				num = i;
			}
		}
		return num;
	}

	private Boolean validDecimal(String num) {
		if (num.matches("^\\d*\\.?\\d+$")) {
			return Double.parseDouble(num) > 0;
		}
		return false;
	}

	private void registrarCategoria(String[] arr, String dato) {
		if (arr[arr.length - 1] != null) {
			System.out.println(style("ERROR: No hay mas cupos para esta categoria, intente en otra.", "error"));
			menuPasajeros();
		}
		// else: hay cupos identificar posicion vacia
		int iPasajeros = 0; // indice en el array principal
		while (pasajerosTodos[iPasajeros] != null) {
			iPasajeros++;
		}
		int iTipo = 0; // indice en el array del tipo de pasajero
		while (arr[iTipo] != null) {
			iTipo++;
		}
		// es null, esta vacio
		pasajerosTodos[iPasajeros] = dato;
		arr[iTipo] = dato;
		String mensaje = "El pasajero <<" + dato + ">> ha sido registrado correctamente. ";
		System.out.println(style(mensaje, "success"));
		menuPasajeros();
	}

}
