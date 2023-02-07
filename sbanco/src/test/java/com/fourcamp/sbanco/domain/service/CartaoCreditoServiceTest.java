package com.fourcamp.sbanco.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.cartao.AtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.NumeroESenhaCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaocredito.AtualizarCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CadastroCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DetalhaCartaoCredito;
import com.fourcamp.sbanco.domain.dto.fatura.PagarFatura;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.CartaoCreditoRepository;
import com.fourcamp.sbanco.infra.exceptions.CartaoBloqueadoException;
import com.fourcamp.sbanco.infra.exceptions.CartaoNaoExisteException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para o serviço de cartões de crédito")
class CartaoCreditoServiceTest {

	@InjectMocks
	private CartaoCreditoService cartaoService;
	@Mock
	private CartaoCreditoRepository cartaoRepository;
	@Mock
	private ContaService contaService;
	@Mock
	private TransacaoService transacaoService;
	@Mock
	private ExtratoBancarioService extratoService;
	@Mock
	private FaturaService faturaService;
	
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
	@DisplayName("Buscar cartão pelo número com sucesso.")
	void getById_BuscarCartaoPeloNumero_ComSucesso() {
		// cenário
		Long id = c1.cartaoCredito.getNumero();
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoCredito));
		
		// ação
		DetalhaCartaoCredito retorno = cartaoService.getById(id);

		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoCredito);
	}
	
	@Test
	@DisplayName("Buscar cartão pelo número, número não cadastrado.")
	void getById_BuscarCartaoPeloNumero_NumeroNaoCadastrado() {
		// cenário
		Long id = c1.cartaoCredito.getNumero();
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.getById(id));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Emitir cartão de crédito com sucesso.")
	void emitirCartaoDeCredito_EmitirCartaoCredito_ComSucesso() {
		// cenário
		c1.cc.setCartaoDeCredito(null);
		CadastroCartaoCredito dados = c1.cadastroCartaoCredito;
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c1.cc);
		
		// ação
		CartaoCreditoDTO retorno = cartaoService.emitirCartaoDeCredito(dados);

		//verificação
		assertThat(retorno).isEqualTo(c1.cartaoCredito);
		
		assertThat(c1.cc.getCartaoDeCredito()).isNotNull();
		
		assertThat(c1.cc.getCartaoDeCredito()).isEqualTo(retorno);
	}
	
	@Test
	@DisplayName("Desbloquear cartão de crédito com sucesso.")
	void desbloquearCartao_DesbloquearCartaoCredito_ComSucesso() {
		// cenário
		c1.cartaoCredito.setBloqueado(true);
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoCredito));
		
		// ação
		DetalhaCartaoCredito retorno = cartaoService.desbloquearCartao(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoCredito);
		
		assertThat(c1.cartaoCredito.isBloqueado()).isFalse();
	}
	
	@Test
	@DisplayName("Desbloquear cartão de crédito, número do cartão inválido.")
	void desbloquearCartao_DesbloquearCartaoCredito_CartaoNaoExiste() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.desbloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Desbloquear cartão de crédito, senha inválida.")
	void desbloquearCartao_DesbloquearCartaoCredito_SenhaInvalida() {
		// cenário
		AtualizarCartao dados = new AtualizarCartao(null, 0, "");
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoCredito));
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.desbloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Bloquear cartão de crédito com sucesso.")
	void bloquearCartao_BloquearCartaoCredito_ComSucesso() {
		// cenário
		c1.cartaoCredito.setBloqueado(false);
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoCredito));
		
		// ação
		DetalhaCartaoCredito retorno = cartaoService.bloquearCartao(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoCredito);
		
		assertThat(c1.cartaoCredito.isBloqueado()).isTrue();
	}
	
	@Test
	@DisplayName("Bloquear cartão de crédito, número do cartão inválido.")
	void bloquearCartao_BloquearCartaoCredito_CartaoNaoExiste() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.bloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Bloquear cartão de crédito, senha inválida.")
	void BloquearCartao_BloquearCartaoCredito_SenhaInvalida() {
		// cenário
		AtualizarCartao dados = new AtualizarCartao(null, 0, "");
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoCredito));
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.bloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Renovar o limite diário de transação com cartão de crédito com sucesso.")
	void renovarLimite_RenovarLimiteCartaoCredito_ComSucesso() {
		// cenário
		AtualizarCartaoCredito dados = c1.atualizarCartaoCredito;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoCredito));
		
		// ação
		DetalhaCartaoCredito retorno = cartaoService.atualizarCartao(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoCredito);
		
	}
	
	@Test
	@DisplayName("Pagar com crédito com sucesso.")
	void pagarComCredito_PagarComCredito_ComSucesso() {
		// cenário
		BigDecimal limiteInicial = new BigDecimal("110");
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		cartao.setBloqueado(false);
		cartao.setLimiteCredito(limiteInicial);
		cartao.getFatura().setLimiteDisponivel(limiteInicial);
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		BigDecimal valorPagamento = new BigDecimal(dados.valorPagamento());
		BigDecimal valorTaxa = new BigDecimal(dados.valorPagamento()).multiply(c1.cc.getCliente().getCategoria().getTaxaUtilizacaoCartaoDeCredito());
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_CREDITO, c1.cc, c2.cc, valorPagamento);
		TransacaoDTO taxaOperacao = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_CREDITO, c1.cc, valorTaxa);
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		lista.add(new DetalhamentoTransacao(transacao));
		lista.add(new DetalhamentoTransacao(taxaOperacao));
		
		// ação
		List<DetalhamentoTransacao> retorno = cartaoService.pagarComCredito(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Pagar com crédito, senha incorreta.")
	void pagarComCredito_PagarComCredito_SenhaIncorreta() {
		// cenário
		BigDecimal limiteInicial = new BigDecimal("110");
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		cartao.setBloqueado(false);
		cartao.setLimiteCredito(limiteInicial);
		cartao.getFatura().setLimiteDisponivel(limiteInicial);
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 457);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComCredito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
		
	}
	
	@Test
	@DisplayName("Pagar com crédito, cartao bloqueado.")
	void pagarComCredito_PagarComCredito_CartaoBloqueado() {
		// cenário
		BigDecimal limiteInicial = new BigDecimal("110");
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		cartao.setBloqueado(true);
		cartao.setLimiteCredito(limiteInicial);
		cartao.getFatura().setLimiteDisponivel(limiteInicial);
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComCredito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoBloqueadoException.class).hasMessageContaining("Cartão bloqueado, operação cancelada.");
		
	}
	@Test
	@DisplayName("Pagar com crédito, limite zerado.")
	void pagarComCredito_PagarComCredito_SemLimite() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		cartao.setBloqueado(false);
		cartao.setLimiteCredito(BigDecimal.ZERO);
		cartao.getFatura().setLimiteDisponivel(BigDecimal.ZERO);
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComCredito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("Não há limite disponível para essa transação.");
		
	}
	
	@Test
	@DisplayName("Pagar com crédito, cartão não cadastrado.")
	void pagarComCredito_PagarComCredito_CartaoNaoCadastrado() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComCredito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
		
	}
	
	@Test
	@DisplayName("Exibir fatura cartão de crédito.")
	void exibirFatura_PagarComCredito_ComSucesso() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		cartao.setBloqueado(false);
		cartao.setLimiteCredito(new BigDecimal("1000"));
		cartao.getFatura().setLimiteDisponivel(new BigDecimal("1000"));
		NumeroESenhaCartao dados = c1.numeroESenhaCartaoCredito;
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		
		// ação
		List<DetalhamentoTransacao> retorno = cartaoService.exibirFatura(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Exibir fatura cartão de crédito.")
	void exibirFatura_PagarComCredito_CartaoNaoCadastrado() {
		// cenário
		NumeroESenhaCartao dados = c1.numeroESenhaCartaoCredito;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.exibirFatura(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Pagar fatura cartão de crédito.")
	void pagarFaturaComSaldo_PagarFatura_ComSucesso() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		cartao.setBloqueado(false);
		cartao.setLimiteCredito(new BigDecimal("1000"));
		cartao.getFatura().setLimiteDisponivel(new BigDecimal("1000"));
		cartao.getFatura().setValorFatura(new BigDecimal("100"));
		PagarFatura dados = new PagarFatura(cartao.getNumero(), "100", "123");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_FATURA, c1.cc, new BigDecimal(dados.valorPagamento()));
		DetalhamentoTransacao esperado = new DetalhamentoTransacao(transacao);
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		
		// ação
		DetalhamentoTransacao retorno = cartaoService.pagarFaturaComSaldo(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Pagar fatura cartão de crédito, cartão nãoc adastrado.")
	void pagarFaturaComSaldo_PagarFatura_CartaoNaoCadastrado() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		PagarFatura dados = new PagarFatura(cartao.getNumero(), "100", "123");
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarFaturaComSaldo(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
}

