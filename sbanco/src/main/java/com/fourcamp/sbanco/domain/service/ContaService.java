package com.fourcamp.sbanco.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.conta.DadosAtualizarConta;
import com.fourcamp.sbanco.domain.dto.conta.DadosCadastroConta;
import com.fourcamp.sbanco.domain.dto.conta.DadosNumeroESenhaConta;
import com.fourcamp.sbanco.domain.dto.conta.DepositoESaqueConta;
import com.fourcamp.sbanco.domain.dto.conta.DetalhamentoDadosConta;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.dto.contapoupanca.ContaPoupancaDTO;
import com.fourcamp.sbanco.domain.dto.pix.ChavePixDTO;
import com.fourcamp.sbanco.domain.dto.pix.DadosCadastroChavePix;
import com.fourcamp.sbanco.domain.dto.pix.DadosPix;
import com.fourcamp.sbanco.domain.dto.pix.DetalhamentoChavesPix;
import com.fourcamp.sbanco.domain.dto.role.AssociaRoles;
import com.fourcamp.sbanco.domain.dto.role.Role;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.AssociaRolesRepository;
import com.fourcamp.sbanco.domain.repository.ContaRepository;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;

import jakarta.validation.Valid;

@Service
@Transactional
public class ContaService {

	@Autowired
	private ContaRepository contaRepository;
	@Autowired
	private ClienteService clienteService;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private ChavePixService chavePixService;
	@Autowired
	private ExtratoBancarioService extratoService;
	@Autowired
	private AssociaRolesRepository assoRepo;

	public ContaPoupancaDTO cadastrarCP(@Valid DadosCadastroConta dados) {
		ClienteDTO cliente = clienteService.getByCpf(dados.cpfCliente());
		ContaPoupancaDTO conta = contaRepository.save(new ContaPoupancaDTO(cliente, dados.senha()));
		Role role = new Role("CP");
		assoRepo.save(new AssociaRoles(conta, role));
		conta.getRoles().add(role);
		return conta;
	}
	public ContaCorrenteDTO cadastrarCC(@Valid DadosCadastroConta dados) {
		ClienteDTO cliente = clienteService.getByCpf(dados.cpfCliente());
		ContaCorrenteDTO conta = contaRepository.save(new ContaCorrenteDTO(cliente, dados.senha()));
		assoRepo.save(new AssociaRoles(conta, new Role("CC")));
		return conta;
	}

	public ContaDTO getByNumeroDaConta(Long numeroDaConta) {
		return contaRepository.findById(numeroDaConta).get();
	}
	
	public DetalhamentoDadosConta consultaByNumeroDaConta(Long numeroDaConta) {
		return new DetalhamentoDadosConta(contaRepository.findById(numeroDaConta).get());
	}
	

	public List<ContaDTO> getAll() {
		return contaRepository.findAll();
	}

	public DetalhamentoTransacao efetuarDeposito(DepositoESaqueConta dados) {
		var conta = getByNumeroDaConta(dados.numeroDaConta());
		BigDecimal bdDeposito = new BigDecimal(dados.valor());
		execTaxasERendimentos(conta);
		checaSenha(conta, dados.senha());
		checaEntradaSaldo(bdDeposito);
//		contaRepository.save(conta);
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.DEPOSITO, conta, bdDeposito);
//		transacaoService.salvar(transacao);
		
		conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
		extratoService.registraTransacao(conta.getExtrato(), transacao);
		
		return new DetalhamentoTransacao(transacao);
	}

	public DetalhamentoTransacao efetuarSaque(DepositoESaqueConta dados) {
		var conta = getByNumeroDaConta(dados.numeroDaConta());
		BigDecimal bdSaque = new BigDecimal(dados.valor());
		execTaxasERendimentos(conta);
		checaSenha(conta, dados.senha());
		checaSaidaSaldo(bdSaque);
		checaSaldoDisponivel(bdSaque, conta.getSaldo());

		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.SAQUE, conta, bdSaque);
		transacaoService.salvar(transacao);

		conta.setSaldo(conta.getSaldo().subtract(transacao.getValor()));
		extratoService.registraTransacao(conta.getExtrato(), transacao);

		return new DetalhamentoTransacao(transacao);
	}

	public List<DetalhamentoTransacao> exibirExtrato(DadosNumeroESenhaConta dados) {
		var conta = getByNumeroDaConta(dados.numeroDaConta());
		execTaxasERendimentos(conta);
		checaSenha(conta, dados.senha());
		extratoService.exibe(conta.getExtrato());
		
		return conta.getExtrato().getHistoricoTransacoes().stream().map(DetalhamentoTransacao::new).toList();
	}

	public DetalhamentoDadosConta trocarSenha(DadosAtualizarConta dados) {
		var conta = getByNumeroDaConta(dados.numeroDaConta());
		execTaxasERendimentos(conta);
		checaSenha(conta, dados.senhaAntiga());
		conta.setSenha(dados.novaSenha());
		
		return new DetalhamentoDadosConta(conta);
	}

	public DetalhamentoChavesPix cadastrarChavePix(DadosCadastroChavePix dados) {
		var conta = getByNumeroDaConta(dados.numeroDaConta());
		execTaxasERendimentos(conta);
		checaSenha(conta, dados.senha());
		
		ChavePixDTO cpix = new ChavePixDTO(conta, dados.chavepix());
		chavePixService.salvar(cpix);
		
		return new DetalhamentoChavesPix(cpix);

	}

	public List<DetalhamentoChavesPix> exibirChavesPix(DadosNumeroESenhaConta dados) {
		var conta = getByNumeroDaConta(dados.numeroDaConta());
		checaSenha(conta, dados.senha());
		
		return chavePixService.getChavesByNumeroDaConta(conta).stream().map(DetalhamentoChavesPix::new).toList();
	}

	public DetalhamentoTransacao enviarPix(DadosPix dados) {
		//Checagens
		var contaOrigem = getByNumeroDaConta(dados.numeroDaContaOrigem());
		var contaDestino = getByNumeroDaConta(dados.numeroDaContaDestino());
		BigDecimal bdPix = new BigDecimal(dados.valorPix());
		execTaxasERendimentos(contaOrigem);
		checaSenha(contaOrigem, dados.senha());
		checaSaidaSaldo(bdPix);
		checaSaldoDisponivel(bdPix, contaOrigem.getSaldo());
		//Operação
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PIX_ENVIADO, contaOrigem, contaDestino, bdPix);
		transacaoService.salvar(transacao);
		
		contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(transacao.getValor()));
		extratoService.registraTransacao(contaOrigem.getExtrato(), transacao);
		receberTransacao(contaDestino, transacao);
		
		return new DetalhamentoTransacao(transacao); 
	}

	protected void receberTransacao(ContaDTO contaDestino, TransacaoDTO transacao) {
		TransacaoDTO tr = new TransacaoDTO(EnumTransacao.PIX_RECEBIDO, transacao.getContaOrigem(), contaDestino, transacao.getValor());
		
		if(transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_DEBITO) || transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_CREDITO)) {
			tr.setEnumTransacao(EnumTransacao.PAGAMENTO_RECEBIDO);
		}
		transacaoService.salvar(tr);
		contaDestino.setSaldo(contaDestino.getSaldo().add(tr.getValor()));
		extratoService.registraTransacao(contaDestino.getExtrato(), tr);

	}

	protected void checaSenha(ContaDTO conta, String senha) {
		if (!BCrypt.checkpw(senha, conta.getSenha())) {
			throw new SenhaInvalidaException("Senha inválida.");
		}
	}

	private void checaEntradaSaldo(BigDecimal bdDeposito) {
		if (bdDeposito.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("Só é possível adicionar ao saldo valores positivos.");
		}
	}

	protected void checaSaidaSaldo(BigDecimal bdSaque) {
		if (bdSaque.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("Só é possível subtrair do saldo valores positivos.");
		}
	}

	protected void checaSaldoDisponivel(BigDecimal bdSaque, BigDecimal saldo) {
		if (saldo.compareTo(bdSaque) < 0) {
			throw new TransacaoInvalidaException("O saldo atual não é suficiente para efetuar essa transação.");
		}
	}

	private void execTaxasERendimentos(ContaDTO conta) {
		if (conta instanceof ContaPoupancaDTO)
			execRendimentos((ContaPoupancaDTO) conta);
		if (conta instanceof ContaCorrenteDTO)
			execTaxas((ContaCorrenteDTO) conta);
	}

	private void execRendimentos(ContaPoupancaDTO cp) {
		if (cp.getUltimoRendimento().until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {
			BigDecimal rendimentos = cp.getSaldo().multiply(cp.getCliente().getCategoria().getRendimentoPoupanca());
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.RENDIMENTO_POUPANCA, cp, rendimentos);
			cp.setSaldo(cp.getSaldo().add(transacao.getValor()));

			extratoService.registraTransacao(cp.getExtrato(), transacao);

			cp.setUltimoRendimento(LocalDate.now());
		}
	}

	private void execTaxas(ContaCorrenteDTO cc) {
		if (cc.getUltimaCobranca().until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {
			BigDecimal taxa = cc.getSaldo().multiply(cc.getCliente().getCategoria().getTaxaContaCorrente());
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.TARIFA_CONTACORRENTE, cc, taxa);
			cc.setSaldo(cc.getSaldo().subtract(transacao.getValor()));
			extratoService.registraTransacao(cc.getExtrato(), transacao);
			cc.setUltimaCobranca(LocalDate.now());
			System.err.println(taxa + "\n" + transacao + "\n" + cc.getSaldo());
			contaRepository.save(cc);
		}
	}

}
