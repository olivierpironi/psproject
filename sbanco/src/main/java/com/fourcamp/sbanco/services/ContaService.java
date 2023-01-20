package com.fourcamp.sbanco.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.ChavePixDTO;
import com.fourcamp.sbanco.dto.ContaCorrenteDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.dto.ContaPoupancaDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.enums.EnumTransacao;
import com.fourcamp.sbanco.infra.exceptions.BOGPExceptions;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;
import com.fourcamp.sbanco.repository.ContaRepository;

@Service
public class ContaService {

	@Autowired
	private ContaRepository cRepository;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private ChavePixService chavePixService;
	@Autowired
	private ExtratoBancarioService extratoService;

	public void salvar(ContaDTO conta) {
		cRepository.save(conta);
	}

	public ContaDTO getByNumeroDaConta(Long numeroDaConta) {
		return cRepository.findById(numeroDaConta).get();
	}

	public List<ContaDTO> getAll() {
		return cRepository.findAll();
	}

	public void efetuarDeposito(ContaDTO conta, String deposito) {
		execTaxasERendimentos(conta);
		try {
//			checaSenha(senha);
			BigDecimal bdDeposito = new BigDecimal(deposito);
			checaEntradaSaldo(bdDeposito);
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.DEPOSITO, conta, bdDeposito);
			transacaoService.salvar(transacao);
			conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
			extratoService.registraTransacao(conta.getExtrato(), transacao);

		} catch (BOGPExceptions e) {
			e.msg();

		}
	}

	public void efetuarSaque(ContaDTO conta, String saque) {
		execTaxasERendimentos(conta);
		try {
//			checaSenha(senha);
			BigDecimal bdSaque = new BigDecimal(saque);
			checaSaidaSaldo(bdSaque);
			checaSaldoDisponivel(bdSaque, conta.getSaldo());
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.SAQUE, conta, bdSaque);
			transacaoService.salvar(transacao);
			conta.setSaldo(conta.getSaldo().subtract(transacao.getValor()));
			extratoService.registraTransacao(conta.getExtrato(), transacao);

		} catch (BOGPExceptions e) {
			e.msg();

		}
	}

	public List<TransacaoDTO> exibirExtrato(ContaDTO conta) {
		execTaxasERendimentos(conta);
		try {
//			checaSenha(senha);
			extratoService.exibe(conta.getExtrato());
			return conta.getExtrato().getHistoricoTransacoes();
		} catch (BOGPExceptions e) {
			e.msg();
		}
		return null;
	}

	public void trocarSenha(ContaDTO conta, String novaSenha) {
		execTaxasERendimentos(conta);
		try {
//			checaSenha(senhaAntiga);
			conta.setSenha(novaSenha);
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void cadastrarChavePix(Long numeroDaConta, String chavepix) {
		var conta = getByNumeroDaConta(numeroDaConta);
		execTaxasERendimentos(conta);
		ChavePixDTO cpix = new ChavePixDTO(conta, chavepix);
		chavePixService.salvar(cpix);

	}
//	protected void checaSenha(String senha) {
//		if (!senha.equals(this.senha)) {
//			throw new SenhaInvalidaException("\nSenha inválida.");
//		}
//	}

	public List<ChavePixDTO> exibirChavesPix(Long numeroDaConta) {
		var conta = getByNumeroDaConta(numeroDaConta);
		return chavePixService.getChavesByNumeroDaConta(conta);
	}

	public void enviarPix(Long numeroDaContaOrigem, Long numeroDaContaDestino, String valorPix) {
		var contaOrigem = getByNumeroDaConta(numeroDaContaOrigem);
		var contaDestino = getByNumeroDaConta(numeroDaContaDestino);
		execTaxasERendimentos(contaOrigem);
		try {
//				checaSenha(senha);
			BigDecimal bdPix = new BigDecimal(valorPix);
			checaSaidaSaldo(bdPix);
			checaSaldoDisponivel(bdPix, contaOrigem.getSaldo());
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PIX_ENVIADO, contaOrigem, contaDestino, bdPix);
			transacaoService.salvar(transacao);
			contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(transacao.getValor()));
			extratoService.registraTransacao(contaOrigem.getExtrato(), transacao);
			receberPix(contaDestino, transacao);

		} catch (BOGPExceptions e) {
			e.msg();
		}

	}

	public void receberPix(ContaDTO contaDestino, TransacaoDTO pix) {
		try {
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PIX_RECEBIDO, pix.getContaOrigem(), contaDestino,
					pix.getValor());
			transacaoService.salvar(transacao);
			contaDestino.setSaldo(contaDestino.getSaldo().add(transacao.getValor()));
			extratoService.registraTransacao(contaDestino.getExtrato(), transacao);

		} catch (BOGPExceptions e) {
			e.msg();
		}

	}

	public void receberTransacao(ContaDTO contaDestino, TransacaoDTO transacao) {
		if (transacao.getEnumTransacao().equals(EnumTransacao.PIX_ENVIADO))
			receberPix(contaDestino, transacao);
		if (transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_CREDITO)
				|| transacao.getEnumTransacao().equals(EnumTransacao.PAGAMENTO_DEBITO))
			receberPagamento(transacao.getContaOrigem(), contaDestino, transacao);

	}

	protected void receberPagamento(ContaDTO contaOrigem, ContaDTO contaDestino, TransacaoDTO transacao) {
		TransacaoDTO pagamento = new TransacaoDTO(EnumTransacao.PAGAMENTO_RECEBIDO, contaOrigem, contaDestino,
				transacao.getValor());
		transacaoService.salvar(pagamento);

		contaDestino.setSaldo(contaDestino.getSaldo().add(pagamento.getValor()));
		extratoService.registraTransacao(contaDestino.getExtrato(), pagamento);

	}

	protected void checaEntradaSaldo(BigDecimal bdDeposito) {
		if (bdDeposito.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("\nSó é possível adicionar ao saldo valores positivos.");
		}
	}

	protected void checaSaidaSaldo(BigDecimal bdSaque) {
		if (bdSaque.compareTo(new BigDecimal("0")) <= 0) {
			throw new TransacaoInvalidaException("\nSó é possível subtrair do saldo valores positivos.");
		}
	}

	protected void checaSaldoDisponivel(BigDecimal bdSaque, BigDecimal saldo) {
		if (saldo.compareTo(bdSaque) < 0) {
			throw new TransacaoInvalidaException("\nO saldo atual não é suficiente para efetuar essa transação.");
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
			salvar(cc);
		}
	}

}
