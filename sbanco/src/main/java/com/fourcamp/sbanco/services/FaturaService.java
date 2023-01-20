package com.fourcamp.sbanco.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.CartaoCreditoDTO;
import com.fourcamp.sbanco.dto.FaturaDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.enums.EnumTransacao;
import com.fourcamp.sbanco.infra.Formatadores;
import com.fourcamp.sbanco.repository.FaturaRepository;

@Service
public class FaturaService {

	@Autowired
	private FaturaRepository repository;

	public void salvar(FaturaDTO fatura) {
		repository.save(fatura);
	}

	public void pagamentoFatura(CartaoCreditoDTO cartao, TransacaoDTO transacao) {
		FaturaDTO fatura = cartao.getFatura();
		BigDecimal valor = transacao.getValor();

		fatura.setLimiteDisponivel(fatura.getLimiteDisponivel().add(valor));
		fatura.setValorFatura(fatura.getValorFatura().subtract(valor));

		fatura.getHistoricoTransacoes().add(transacao);
	}

	public void registraTransacao(CartaoCreditoDTO cartao, TransacaoDTO transacao) {
		FaturaDTO fatura = cartao.getFatura();

		BigDecimal valor = transacao.getValor();

		fatura.setLimiteDisponivel(fatura.getLimiteDisponivel().subtract(valor));
		fatura.setValorFatura(fatura.getValorFatura().add(valor));

		fatura.getHistoricoTransacoes().add(transacao);
	}

	public void registraTransacao(CartaoCreditoDTO cartao, TransacaoDTO transacao, TransacaoDTO taxaOperacao) {
		FaturaDTO fatura = cartao.getFatura();

		if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_CREDITO)
				|| transacao.getEnumTransacao().equals(EnumTransacao.MENSALIDADE_SEGURO)) {
			BigDecimal valor = transacao.getValor().add(taxaOperacao.getValor());

			fatura.setLimiteDisponivel(fatura.getLimiteDisponivel().subtract(valor));
			fatura.setValorFatura(fatura.getValorFatura().add(valor));
			fatura.getHistoricoTransacoes().add(taxaOperacao);

		}
		fatura.getHistoricoTransacoes().add(transacao);
	}

	public void exibe(CartaoCreditoDTO cartao) {
		FaturaDTO fatura = cartao.getFatura();
		System.out.println("\n**********FATURA CARTÃO DE CRÉDITO**********" + "\n                  BANCO OGP");
		System.out.println(cartao);
		fatura.getHistoricoTransacoes().stream().forEach(System.out::println);
		System.out.println(
				"\nFATURA ATUAL........................ + R$" + Formatadores.arredonda(fatura.getValorFatura()));
		System.out.println(
				"CRÉDITO DISPONÍVEL.................. + R$" + Formatadores.arredonda(fatura.getLimiteDisponivel()));
		System.out.println("**********************************************");

	}

}
