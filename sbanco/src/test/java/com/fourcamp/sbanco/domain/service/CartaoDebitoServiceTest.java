package com.fourcamp.sbanco.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.cartao.AtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CadastroCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DetalhaCartaoDebito;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.CartaoDebitoRepository;
import com.fourcamp.sbanco.infra.exceptions.CartaoBloqueadoException;
import com.fourcamp.sbanco.infra.exceptions.CartaoNaoExisteException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para o serviço de cartões de débito")
class CartaoDebitoServiceTest {

	@InjectMocks
	private CartaoDebitoService cartaoService;
	@Mock
	private CartaoDebitoRepository cartaoRepository;
	@Mock
	private ContaService contaService;
	@Mock
	private TransacaoService transacaoService;
	@Mock
	private ExtratoBancarioService extratoService;
	
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
		Long id = c1.cartaoDebito.getNumero();
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoDebito));
		
		// ação
		DetalhaCartaoDebito retorno = cartaoService.getById(id);

		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoDebito);
	}
	
	@Test
	@DisplayName("Buscar cartão pelo número, número não cadastrado.")
	void getById_BuscarCartaoPeloNumero_NumeroNaoCadastrado() {
		// cenário
		Long id = c1.cartaoDebito.getNumero();
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.getById(id));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Emitir cartão de débito com sucesso.")
	void emitirCartaoDeDebito_EmitirCartaoDebito_ComSucesso() {
		// cenário
		c1.cc.setCartaoDeDebito(null);
		CadastroCartaoDebito dados = c1.cadastroCartaoDebito;
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c1.cc);
		
		// ação
		CartaoDebitoDTO retorno = cartaoService.emitirCartaoDeDebito(dados);

		//verificação
		assertThat(retorno).isEqualTo(c1.cartaoDebito);
		
		assertThat(c1.cc.getCartaoDeDebito()).isNotNull();
		
		assertThat(c1.cc.getCartaoDeDebito()).isEqualTo(retorno);
	}
	
	@Test
	@DisplayName("Desbloquear cartão de débito com sucesso.")
	void desbloquearCartao_DesbloquearCartaoDebito_ComSucesso() {
		// cenário
		c1.cartaoDebito.setBloqueado(true);
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoDebito));
		
		// ação
		DetalhaCartaoDebito retorno = cartaoService.desbloquearCartao(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoDebito);
		
		assertThat(c1.cartaoDebito.isBloqueado()).isFalse();
	}
	
	@Test
	@DisplayName("Desbloquear cartão de débito, número do cartão inválido.")
	void desbloquearCartao_DesbloquearCartaoDebito_CartaoNaoExiste() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.desbloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Desbloquear cartão de débito, senha inválida.")
	void desbloquearCartao_DesbloquearCartaoDebito_SenhaInvalida() {
		// cenário
		AtualizarCartao dados = new AtualizarCartao(null, 0, "");
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoDebito));
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.desbloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Bloquear cartão de débito com sucesso.")
	void bloquearCartao_BloquearCartaoDebito_ComSucesso() {
		// cenário
		c1.cartaoDebito.setBloqueado(false);
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoDebito));
		
		// ação
		DetalhaCartaoDebito retorno = cartaoService.bloquearCartao(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoDebito);
		
		assertThat(c1.cartaoDebito.isBloqueado()).isTrue();
	}
	
	@Test
	@DisplayName("Bloquear cartão de débito, número do cartão inválido.")
	void bloquearCartao_BloquearCartaoDebito_CartaoNaoExiste() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.bloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Bloquear cartão de débito, senha inválida.")
	void BloquearCartao_BloquearCartaoDebito_SenhaInvalida() {
		// cenário
		AtualizarCartao dados = new AtualizarCartao(null, 0, "");
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cartaoDebito));
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.bloquearCartao(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Renovar o limite diário de transação com cartão de débito com sucesso.")
	void renovarLimite_RenovarLimiteCartaoDebito_ComSucesso() {
		// cenário
		CartaoDebitoDTO cartaoDebito = c1.cartaoDebito;
		cartaoDebito.setUltimaRenovacaoLimiteDisponivel(LocalDate.of(1900, 1, 1));
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartaoDebito));
		
		// ação
		DetalhaCartaoDebito retorno = cartaoService.renovarLimite(cartaoDebito.getNumero());
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoDebito);
		
		assertThat(cartaoDebito.getUltimaRenovacaoLimiteDisponivel()).isEqualTo(LocalDate.now());
	}
	
	@Test
	@DisplayName("Renovar o limite diário de transação com cartão de débito sem sucesso.")
	void renovarLimite_RenovarLimiteCartaoDebito_SemSucesso() {
		// cenário
		CartaoDebitoDTO cartaoDebito = c1.cartaoDebito;
		cartaoDebito.setUltimaRenovacaoLimiteDisponivel(LocalDate.now());
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartaoDebito));
		
		// ação
		DetalhaCartaoDebito retorno = cartaoService.renovarLimite(cartaoDebito.getNumero());
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoCartaoDebito);
		
		assertThat(cartaoDebito.getUltimaRenovacaoLimiteDisponivel()).isEqualTo(LocalDate.now());
	}
	
	@Test
	@DisplayName("Renovar o limite diário de transação com cartão de débito, numero do cartão é inválido.")
	void renovarLimite_RenovarLimiteCartaoDebito_CartaoNaoCadastrado() {
		// cenário
		CartaoDebitoDTO cartaoDebito = c1.cartaoDebito;
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.renovarLimite(cartaoDebito.getNumero()));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
	}
	
	@Test
	@DisplayName("Pagar com débito com sucesso.")
	void pagarComDebito_PagarComDebito_ComSucesso() {
		// cenário
		BigDecimal saldoInicial = new BigDecimal("110");
		c1.cc.setSaldo(saldoInicial);
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		cartao.setBloqueado(false);
		cartao.setLimiteDiario(new BigDecimal("1000"));
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		BigDecimal valorPagamento = new BigDecimal(dados.valorPagamento());
		BigDecimal valorTaxa = new BigDecimal(dados.valorPagamento()).multiply(c1.cc.getCliente().getCategoria().getTaxaUtilizacaoCartaoDeDebito());
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_DEBITO, c1.cc, c2.cc, valorPagamento);
		TransacaoDTO taxaOperacao = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_DEBITO, c1.cc, valorTaxa);
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		lista.add(new DetalhamentoTransacao(transacao));
		lista.add(new DetalhamentoTransacao(taxaOperacao));
		
		// ação
		List<DetalhamentoTransacao> retorno = cartaoService.pagarComDebito(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(lista);
		
		assertThat(cartao.getLimiteDisponivel()).isEqualTo(cartao.getLimiteDiario().subtract(valorPagamento).subtract(valorTaxa));
		
		assertThat(c1.cc.getSaldo()).isEqualTo(saldoInicial.subtract(valorPagamento).subtract(valorTaxa));
	}
	
	@Test
	@DisplayName("Pagar com débito, senha incorreta.")
	void pagarComDebito_PagarComDebito_SenhaIncorreta() {
		// cenário
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 457);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComDebito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
		
	}
	
	@Test
	@DisplayName("Pagar com débito, cartao bloqueado.")
	void pagarComDebito_PagarComDebito_CartaoBloqueado() {
		// cenário
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		cartao.setBloqueado(true);
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComDebito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoBloqueadoException.class).hasMessageContaining("Cartão bloqueado, operação cancelada.");
		
	}
	@Test
	@DisplayName("Pagar com débito, limite diário zerado.")
	void pagarComDebito_PagarComDebito_SemLimite() {
		// cenário
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		cartao.setLimiteDiario(BigDecimal.ZERO);
		cartao.setBloqueado(false);
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(cartao));
		when(contaService.getByNumeroDaConta(ArgumentMatchers.any())).thenReturn(c2.cc);
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComDebito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("Não há limite disponível para essa transação.");
		
	}
	
	@Test
	@DisplayName("Pagar com débito, cartão não cadastrado.")
	void pagarComDebito_PagarComDebito_CartaoNaoCadastrado() {
		// cenário
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		PagarComCartao dados = new PagarComCartao(cartao.getNumero(), c2.cc.getNumeroDaConta(), "100", 456);
		
		when(cartaoRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> cartaoService.pagarComDebito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(CartaoNaoExisteException.class).hasMessageContaining("Não existe um cartão com este número.");
		
	}
	
}

