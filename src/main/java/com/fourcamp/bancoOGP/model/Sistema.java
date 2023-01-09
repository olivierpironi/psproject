package com.fourcamp.bancoOGP.model;

import java.util.Timer;
import java.util.TimerTask;

import com.fourcamp.bancoOGP.interfaces.GerenciavelPeloSistema;
import com.fourcamp.bancoOGP.services.DataBase;

/**
 * A classe Sistema executa diaramente tudo que está dentro do método
 * {@link GerenciavelPeloSistema#executarDiariamente() executarDiariamente} das
 * classes que implementam a interface GerenciavelPeloSistema.
 * 
 * @author Olivier Gomes Pironi
 */
public class Sistema {

	private Sistema() {
		super();
	}

	public static void temporizador() {
		int intervalo = 1000 * 60 * 60 * 24; // intervalo de 24hrs a cada execução.
		int delay = 1000 * 3; // delay inicial de 3 seg.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (Conta conta : DataBase.getListaDeContas()) {
					if (conta instanceof GerenciavelPeloSistema) {

						((GerenciavelPeloSistema) conta).executarDiariamente();
					}

				}
				for (Cartao cartao : DataBase.getListaDeCartoes()) {
					if (cartao instanceof GerenciavelPeloSistema) {

						((GerenciavelPeloSistema) cartao).executarDiariamente();
					}

				}

			}
		}, delay, intervalo);

	}
}
