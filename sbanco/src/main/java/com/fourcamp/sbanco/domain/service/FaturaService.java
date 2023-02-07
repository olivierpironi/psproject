package com.fourcamp.sbanco.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.fatura.FaturaDTO;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;

@Service
public class FaturaService {

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

}
