package com.fourcamp.sbanco.infra.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

/**
 * Formatadores utilizados para deixar o código mais legível.
 *
 * @author Olivier Gomes Pironi
 *
 */
public class Formatadores {

	public static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private Formatadores() {
	}

	public static BigDecimal arredonda(BigDecimal bd) {
		return bd.setScale(2, RoundingMode.HALF_EVEN);
	}

}
