package com.fourcamp.sbanco.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.conta.AtualizarConta;
import com.fourcamp.sbanco.domain.dto.conta.CadastroConta;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.conta.DepositoESaqueConta;
import com.fourcamp.sbanco.domain.dto.conta.DetalhaConta;
import com.fourcamp.sbanco.domain.dto.conta.NumeroESenhaConta;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.dto.contapoupanca.ContaPoupancaDTO;
import com.fourcamp.sbanco.domain.dto.pix.CadastroChavePix;
import com.fourcamp.sbanco.domain.dto.pix.ChavePixDTO;
import com.fourcamp.sbanco.domain.dto.pix.DadosPix;
import com.fourcamp.sbanco.domain.dto.pix.DetalhamentoChavesPix;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.ContaRepository;
import com.fourcamp.sbanco.infra.exceptions.ContaNaoExisteException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para o serviço de contas")
class ContaServiceTest {

	
	@InjectMocks
	private ContaService contaService;
	@Mock
	private ContaRepository contaRepository;
	@Mock
	private ClienteService clienteService;
	@Mock
	private TransacaoService transacaoService;
	@Mock
	private ChavePixService chavePixService;
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
	@DisplayName("Cadastrar conta poupança com sucesso.")
	void cadastrarCP_CadastrarContaPoupanca_ComSucesso() {
		// cenário
		CadastroConta dados = c1.cadastroConta;
		ClienteDTO clienteEsperado = c1.cliente;
		when(clienteService.getByCpf(ArgumentMatchers.any())).thenReturn(clienteEsperado);
		when(contaRepository.save(ArgumentMatchers.any())).thenReturn(c1.cp);
		
		// ação
		ContaPoupancaDTO conta = contaService.cadastrarCP(dados);

		//verificação
		assertThat(conta).isNotNull();
				
		assertThat(conta.getNumeroDaConta()).isEqualTo(c1.cp.getNumeroDaConta());
	}
	
	@Test
	@DisplayName("Cadastrar conta corrente com sucesso.")
	void cadastrarCC_CadastrarContaCorrente_ComSucesso() {
		// cenário
		CadastroConta dados = c1.cadastroConta;
		ClienteDTO clienteEsperado = c1.cliente;
		when(clienteService.getByCpf(dados.cpfCliente())).thenReturn(clienteEsperado);
		when(contaRepository.save(c1.cc)).thenReturn(c1.cc);
		
		// ação
		ContaCorrenteDTO conta = contaService.cadastrarCC(dados);
		
		//verificação
		assertThat(conta).isNotNull();
		
		assertThat(conta.getNumeroDaConta()).isEqualTo(c1.cc.getNumeroDaConta());
	}
	
	@Test
	@DisplayName("Buscar conta pelo numero com sucesso.")
	void getByNumeroDaConta_BuscarContaPeloNumero_ComSucesso() {
		// cenário
		Long numeroDaConta = c1.cp.getNumeroDaConta();
		when(contaRepository.findById(numeroDaConta)).thenReturn(Optional.of(c1.cp));
		
		// ação
		ContaDTO retorno = contaService.getByNumeroDaConta(numeroDaConta);
		
		//verificação
		assertThat(retorno).isNotNull();
		
		assertThat(retorno.getNumeroDaConta()).isEqualTo(c1.cp.getNumeroDaConta());
	}
	
	@Test
	@DisplayName("Buscar conta pelo numero quando a conta não existe.")
	void getByNumeroDaConta_BuscarContaPeloNumero_ContaNaoExiste() {
		// cenário
		Long numeroDaConta = c1.cp.getNumeroDaConta();
		when(contaRepository.findById(numeroDaConta)).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> contaService.getByNumeroDaConta(numeroDaConta));
		
		//verificação
		 assertThat(e).isInstanceOf(ContaNaoExisteException.class).hasMessageContaining("Não existe uma conta com este número.");
	}
	
	@Test
	@DisplayName("Consultar conta pelo numero com sucesso.")
	void consultaByNumeroDaConta_BuscarContaPeloNumero_ComSucesso() {
		// cenário
		Long numeroDaConta = c1.cp.getNumeroDaConta();
		when(contaRepository.findById(numeroDaConta)).thenReturn(Optional.of(c1.cp));
		
		// ação
		DetalhaConta retorno = contaService.consultaByNumeroDaConta(numeroDaConta);
		
		//verificação
		assertThat(retorno).isEqualTo(c1.detalhamentoConta);
	}
	
	@Test
	@DisplayName("Consultar conta pelo numero quando a conta não existe.")
	void consultaByNumeroDaConta_BuscarContaPeloNumero_ContaNaoExiste() {
		// cenário
		Long numeroDaConta = c1.cp.getNumeroDaConta();
		when(contaRepository.findById(numeroDaConta)).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> contaService.consultaByNumeroDaConta(numeroDaConta));
		
		//verificação
		assertThat(e).isInstanceOf(ContaNaoExisteException.class).hasMessageContaining("Não existe uma conta com este número.");
	}
	
	@Test
	@DisplayName("Consultar todas as contas.")
	void getAll_BuscarTodasAsContas_ComSucesso() {
		// cenário
		List<ContaDTO> lista = new ArrayList<>();
		when(contaRepository.findAll()).thenReturn(lista);
		
		// ação
		List<ContaDTO> retorno = contaService.getAll();
		
		//verificação
		assertThat(retorno).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Efetuar depósito com sucesso.")
	void efetuarDeposito_Deposito_ComSucesso() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cc.getNumeroDaConta(), "100", "123");
		BigDecimal valorDeposito = new BigDecimal(dados.valor());
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.DEPOSITO, c1.cc, valorDeposito);
		DetalhamentoTransacao esperado = new DetalhamentoTransacao(transacao);
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cc));
		
		// ação
		DetalhamentoTransacao retorno = contaService.efetuarDeposito(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
		assertThat(c1.cc.getSaldo().setScale(0)).isEqualTo(valorDeposito);
		
	}
	
	@Test
	@DisplayName("Efetuar depósito senha incorreta.")
	void efetuarDeposito_Deposito_SenhaIncorreta() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cc.getNumeroDaConta(), "100", "124");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cc));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.efetuarDeposito(dados));
		
		//verificação
		 assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
		
	}
	
	@Test
	@DisplayName("Efetuar depósito valor negativo.")
	void efetuarDeposito_Deposito_ValorNegativo() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cc.getNumeroDaConta(), "-100", "123");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cc));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.efetuarDeposito(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("Só é possível adicionar ao saldo valores positivos.");
		
	}
	
	@Test
	@DisplayName("Efetuar saque com sucesso.")
	void efetuarSaque_Saque_ComSucesso() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cp.getNumeroDaConta(), "100", "123");
		BigDecimal bdSaque = new BigDecimal("100");
		c1.cp.setSaldo(bdSaque);
		
		BigDecimal rendimentoPoupanca = c1.cp.getCliente().getCategoria().getRendimentoPoupanca();
		BigDecimal saldoAposRendimento = bdSaque.multiply(rendimentoPoupanca);
		
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.SAQUE, c1.cp, bdSaque);
		DetalhamentoTransacao esperado = new DetalhamentoTransacao(transacao);
		
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		DetalhamentoTransacao retorno = contaService.efetuarSaque(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
		
		assertThat(c1.cp.getSaldo()).isEqualTo(saldoAposRendimento);
	}
	
	@Test
	@DisplayName("Efetuar saque senha incorreta.")
	void efetuarSaque_Saque_SenhaIncorreta() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cp.getNumeroDaConta(), "100", "124");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
				Throwable e = catchThrowable(() -> contaService.efetuarSaque(dados));
				
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Efetuar saque valor negativo.")
	void efetuarSaque_Saque_ValorNegativa() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cp.getNumeroDaConta(), "-100", "123");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.efetuarSaque(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("Só é possível subtrair do saldo valores positivos.");
	}
	
	@Test
	@DisplayName("Efetuar saque com saldo insuficiente.")
	void efetuarSaque_Saque_SaldoInsuficiente() {
		// cenário
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cp.getNumeroDaConta(), "100", "123");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.efetuarSaque(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("O saldo atual não é suficiente para efetuar essa transação.");
	}
	
	@Test
	@DisplayName("Exibir extrato com sucesso.")
	void exibirExtrato_ExibirExtratoBancario_ComSucesso() {
		// cenário
		NumeroESenhaConta dados = new NumeroESenhaConta(c1.cp.getNumeroDaConta(), "123");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		List<DetalhamentoTransacao> esperado = c1.cp.getExtrato().getHistoricoTransacoes().stream().map(DetalhamentoTransacao::new).collect(Collectors.toList());
		DetalhamentoTransacao detalhaSaldo = new DetalhamentoTransacao(new TransacaoDTO(EnumTransacao.SALDO, c1.cp, c1.cp.getSaldo().setScale(2)));
		esperado.add(detalhaSaldo);
		// ação
		List<DetalhamentoTransacao> retorno = contaService.exibirExtrato(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
		
		assertThat(retorno).containsSequence(detalhaSaldo);
	}
	
	@Test
	@DisplayName("Exibir extrato senha incorreta.")
	void exibirExtrato_ExibirExtratoBancario_SenhaIncorreta() {
		// cenário
		NumeroESenhaConta dados = new NumeroESenhaConta(c1.cp.getNumeroDaConta(), "124");
		when(contaRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cp));
		// ação
		Throwable e = catchThrowable(() -> contaService.exibirExtrato(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Trocar senha da conta.")
	void trocarSenha_TrocarSenhaConta_ComSucesso() {
		// cenário
		AtualizarConta dados = c1.dadosAtualizarConta;
		DetalhaConta esperado = c1.detalhamentoConta;
		when(contaRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cp));

		// ação
		DetalhaConta retorno = contaService.trocarSenha(dados);
		boolean senhasIguais = BCrypt.checkpw(dados.novaSenha(), c1.cp.getSenha());
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
		
		assertThat(senhasIguais).isTrue();
	}
	
	@Test
	@DisplayName("Trocar senha da conta, senha inválida.")
	void trocarSenha_TrocarSenhaConta_SenhaIncorreta() {
		// cenário
		AtualizarConta dados = new AtualizarConta(c1.cp.getNumeroDaConta(), "", "");
		when(contaRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.trocarSenha(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Cadastrar chave pix.")
	void cadastrarChavePix_CadastrarChavePix_ComSucesso() {
		// cenário
		CadastroChavePix dados = new CadastroChavePix(c1.cp.getNumeroDaConta(), "123", "chave");
		DetalhamentoChavesPix esperado = new DetalhamentoChavesPix(new ChavePixDTO(c1.cp, dados.chavepix()));
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		DetalhamentoChavesPix retorno = contaService.cadastrarChavePix(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Cadastrar chave pix, senha inválida.")
	void cadastrarChavePix_CadastrarChavePix_SenhaInvalida() {
		// cenário
		CadastroChavePix dados = new CadastroChavePix(c1.cp.getNumeroDaConta(), "124", "chave");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.cadastrarChavePix(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Exibir chaves pix.")
	void exibirChavesPix_ExibirChavePix_ComSucesso() {
		// cenário
		NumeroESenhaConta dados = c1.numeroESenhaConta;
		List<DetalhamentoChavesPix> lista = new ArrayList<>();
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		List<DetalhamentoChavesPix> retorno = contaService.exibirChavesPix(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Exibir chaves pix, senha inválida.")
	void exibirChavesPix_ExibirChavePix_SenhaInvalida() {
		// cenário
		NumeroESenhaConta dados = new NumeroESenhaConta(c1.cp.getNumeroDaConta(), "1234");
		when(contaRepository.findById(dados.numeroDaConta())).thenReturn(Optional.of(c1.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.exibirChavesPix(dados));
		
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Enviar pix.")
	void enviarPix_EnviarPix_ComSucesso() {
		// cenário
		BigDecimal saldoInicial = new BigDecimal("100");
		c1.cp.setSaldo(saldoInicial);
		Long numeroDaContaOrigem = c1.cp.getNumeroDaConta();
		Long numeroDaContaDestino = c2.cp.getNumeroDaConta();
		DadosPix dados = new DadosPix(numeroDaContaOrigem, numeroDaContaDestino, "123", "100");
		
		TransacaoDTO pixEnviado = new TransacaoDTO(EnumTransacao.PIX_ENVIADO, c1.cp, c2.cp, new BigDecimal(dados.valorPix()));
		DetalhamentoTransacao esperado = new DetalhamentoTransacao(pixEnviado);
		
		BigDecimal rendimentoPoupanca = c1.cp.getCliente().getCategoria().getRendimentoPoupanca();
		BigDecimal valorAposPix = saldoInicial.multiply(rendimentoPoupanca);
		
		when(contaRepository.findById(dados.numeroDaContaOrigem())).thenReturn(Optional.of(c1.cp));
		when(contaRepository.findById(dados.numeroDaContaDestino())).thenReturn(Optional.of(c2.cp));
		
		// ação
		DetalhamentoTransacao retorno = contaService.enviarPix(dados);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
		
		assertThat(c1.cp.getSaldo()).isEqualTo(valorAposPix);
		
		assertThat(c2.cp.getSaldo()).isEqualTo(saldoInicial);
	}
	
	@Test
	@DisplayName("Enviar pix, senha inválida.")
	void enviarPix_EnviarPix_SenhaInvalida() {
		// cenário
		Long numeroDaContaOrigem = c1.cp.getNumeroDaConta();
		Long numeroDaContaDestino = c2.cp.getNumeroDaConta();
		DadosPix dados = new DadosPix(numeroDaContaOrigem, numeroDaContaDestino, "124", "100");
		when(contaRepository.findById(dados.numeroDaContaOrigem())).thenReturn(Optional.of(c1.cp));
		when(contaRepository.findById(dados.numeroDaContaDestino())).thenReturn(Optional.of(c2.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.enviarPix(dados));
				
		//verificação
		assertThat(e).isInstanceOf(SenhaInvalidaException.class).hasMessageContaining("Senha inválida.");
	}
	
	@Test
	@DisplayName("Enviar pix, valor negativo.")
	void enviarPix_EnviarPix_ValorNegativa() {
		// cenário
		Long numeroDaContaOrigem = c1.cp.getNumeroDaConta();
		Long numeroDaContaDestino = c2.cp.getNumeroDaConta();
		DadosPix dados = new DadosPix(numeroDaContaOrigem, numeroDaContaDestino, "123", "-100");
		when(contaRepository.findById(dados.numeroDaContaOrigem())).thenReturn(Optional.of(c1.cp));
		when(contaRepository.findById(dados.numeroDaContaDestino())).thenReturn(Optional.of(c2.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.enviarPix(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("Só é possível subtrair do saldo valores positivos.");
	}
	
	@Test
	@DisplayName("Enviar pix, saldo insuficiente.")
	void enviarPix_EnviarPix_SaldoInsuficiente() {
		// cenário
		Long numeroDaContaOrigem = c1.cp.getNumeroDaConta();
		Long numeroDaContaDestino = c2.cp.getNumeroDaConta();
		c1.cp.setSaldo(BigDecimal.ZERO);
		DadosPix dados = new DadosPix(numeroDaContaOrigem, numeroDaContaDestino, "123", "100");
		when(contaRepository.findById(dados.numeroDaContaOrigem())).thenReturn(Optional.of(c1.cp));
		when(contaRepository.findById(dados.numeroDaContaDestino())).thenReturn(Optional.of(c2.cp));
		
		// ação
		Throwable e = catchThrowable(() -> contaService.enviarPix(dados));
		
		//verificação
		assertThat(e).isInstanceOf(TransacaoInvalidaException.class).hasMessageContaining("O saldo atual não é suficiente para efetuar essa transação.");
	}
	
}
