package com.fourcamp.sbanco.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.fatura.FaturaDTO;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.FaturaRepository;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para o serviço de cartões de fatura")
class FaturaServiceTest {

	@InjectMocks
	private FaturaService faturaService;
	@Mock
	private FaturaRepository repository;
	
	private static ContaFactory c1;
	private static ContaFactory c2;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		c1 = new ContaFactory().fabricarC1();
		c2 = new ContaFactory().fabricarC2();
		c1.cp.setSaldo(BigDecimal.ZERO);
		c2.cp.setSaldo(BigDecimal.ZERO);
		c1.cc.setSaldo(BigDecimal.ZERO);
		c2.cc.setSaldo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("Pagar fatura com sucesso.")
	void pagamentoFatura_PagarFatura_ComSucesso() {
		// cenário
		FaturaDTO fatura = c1.cartaoCredito.getFatura();
		BigDecimal limiteInicial = new BigDecimal("0");
		fatura.setLimiteDisponivel(limiteInicial);
		BigDecimal valorFatura = new BigDecimal("100");
		fatura.setValorFatura(valorFatura);
		BigDecimal valorPagamento = valorFatura;
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_FATURA, c1.cc, c2.cc, valorPagamento);
		// ação
		faturaService.pagamentoFatura(c1.cartaoCredito, transacao);

		//verificação
		assertThat(fatura.getLimiteDisponivel()).isEqualTo(valorPagamento);
		
		assertThat(fatura.getValorFatura()).isEqualTo(valorFatura.subtract(valorPagamento));
	}
	
	@Test
	@DisplayName("Registra transação com sucesso.")
	void registraTransacao_RegistraTransacao_ComSucesso() {
		// cenário
		FaturaDTO fatura = c1.cartaoCredito.getFatura();
		BigDecimal limiteInicial = new BigDecimal("100");
		fatura.setLimiteDisponivel(limiteInicial);
		BigDecimal valorFatura = new BigDecimal("0");
		fatura.setValorFatura(valorFatura);
		BigDecimal valorPagamento = limiteInicial;
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_CREDITO, c1.cc, c2.cc, valorPagamento);
		// ação
		faturaService.registraTransacao(c1.cartaoCredito, transacao);
		
		//verificação
		assertThat(fatura.getLimiteDisponivel()).isEqualTo(limiteInicial.subtract(valorPagamento));
		
		assertThat(fatura.getValorFatura()).isEqualTo(valorPagamento);
		
		assertThat(fatura.getHistoricoTransacoes()).contains(transacao);
	}
	
	@Test
	@DisplayName("Registra transação com taxa com sucesso.")
	void registraTransacao_RegistraTransacaoComTaxa_ComSucesso() {
		// cenário
		FaturaDTO fatura = c1.cartaoCredito.getFatura();
		BigDecimal limiteInicial = new BigDecimal("100.5");
		fatura.setLimiteDisponivel(limiteInicial);
		BigDecimal valorFatura = new BigDecimal("0");
		fatura.setValorFatura(valorFatura);
		BigDecimal valorPagamento = limiteInicial;
		BigDecimal valorTaxa = new BigDecimal("0.5");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_CREDITO, c1.cc, c2.cc, valorPagamento);
		TransacaoDTO taxa = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_CREDITO, c1.cc, c2.cc, valorTaxa);
		// ação
		faturaService.registraTransacao(c1.cartaoCredito, transacao, taxa);
		
		//verificação
		assertThat(fatura.getLimiteDisponivel()).isEqualTo(limiteInicial.subtract(valorPagamento).subtract(valorTaxa));
		
		assertThat(fatura.getValorFatura()).isEqualTo(valorPagamento.add(valorTaxa));
		
		assertThat(fatura.getHistoricoTransacoes()).contains(transacao);
		
		assertThat(fatura.getHistoricoTransacoes()).contains(taxa);
	}
	
	@Test
	@DisplayName("Registra transação de mensalidade do seguro sucesso.")
	void registraTransacao_RegistraTransacaoMensalidadeSeguro_ComSucesso() {
		// cenário
		FaturaDTO fatura = c1.cartaoCredito.getFatura();
		BigDecimal limiteInicial = new BigDecimal("100.5");
		fatura.setLimiteDisponivel(limiteInicial);
		BigDecimal valorFatura = new BigDecimal("0");
		fatura.setValorFatura(valorFatura);
		BigDecimal valorPagamento = limiteInicial;
		BigDecimal valorTaxa = new BigDecimal("0.5");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.MENSALIDADE_SEGURO, c1.cc, c2.cc, valorPagamento);
		TransacaoDTO taxa = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_CREDITO, c1.cc, c2.cc, valorTaxa);
		// ação
		faturaService.registraTransacao(c1.cartaoCredito, transacao, taxa);
				
		//verificação
		assertThat(fatura.getLimiteDisponivel()).isEqualTo(limiteInicial.subtract(valorPagamento).subtract(valorTaxa));
				
		assertThat(fatura.getValorFatura()).isEqualTo(valorPagamento.add(valorTaxa));
				
		assertThat(fatura.getHistoricoTransacoes()).contains(transacao);
				
		assertThat(fatura.getHistoricoTransacoes()).contains(taxa);
			}

}
