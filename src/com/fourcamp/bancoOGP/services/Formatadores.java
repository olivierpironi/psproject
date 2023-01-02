package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formatadores utilizados para deixar o código mais legível.
 * 
 * @author Olivier Gomes Pironi
 *
 */
public class Formatadores {
	private Formatadores() {
	}

	public static final String dataEHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

	public static BigDecimal arredonda(BigDecimal bd) {
		return bd.setScale(2, RoundingMode.HALF_EVEN);
	}

}
