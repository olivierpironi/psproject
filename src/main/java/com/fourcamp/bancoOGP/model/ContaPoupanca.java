package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fourcamp.bancoOGP.enums.EnumConta;
import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.exceptions.DataBaseException;
import com.fourcamp.bancoOGP.interfaces.GerenciavelPeloSistema;
import com.fourcamp.bancoOGP.interfaces.RentavelOuTributavel;
import com.fourcamp.bancoOGP.services.DataBase;

/**
 * Conta Poupança tem as funções:
 * - Rendimento mensal através da poupança.
 * 
 * Um mesmo cliente só pode ter uma única conta poupança em nosso banco.
 * 
 * @author Olivier Gomes Pironi
 *
 */
public class ContaPoupanca extends Conta implements RentavelOuTributavel, GerenciavelPeloSistema {

	protected LocalDate ultimoRendimento = LocalDate.of(2023, 1, 1);
	public ContaPoupanca(EnumConta tipoDeConta, Cliente cliente, String senha) {
		super(tipoDeConta, cliente, senha);

	}
	

	public static void criarContaPoupanca(Cliente cliente, String senha) {
		try {
			checaCPByCpf(cliente);
			ContaPoupanca conta = new ContaPoupanca(EnumConta.CONTA_POUPANCA,cliente, senha);
			DataBase.getListaDeContas().add(conta);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	@Override
	public void execRendimentosETaxas() {
		if(ultimoRendimento.until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {
			BigDecimal rendimentos = this.saldo.multiply(this.getCliente().getCategoria().getRendimentoPoupanca());
			Transacao transacao = new Transacao(EnumTransacao.RENDIMENTO_POUPANCA, this, rendimentos);
			this.saldo = this.saldo.add(transacao.getValor());
			extrato.registraTransacao(transacao);
			ultimoRendimento = LocalDate.now();
		}

	}

	@Override
	public void executarDiariamente() {
		execRendimentosETaxas();
		
	}


	private static void checaCPByCpf(Cliente cliente) {
		if (DataBase.getCPByCpf(cliente.getCpf()) != null) {
			throw new DataBaseException("\nO cliente já tem uma Conta Poupança no Banco OGP");
		}
	}
}
