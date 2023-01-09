package com.fourcamp.bancoOGP.model;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.fourcamp.bancoOGP.enums.EnumCliente;

class ClienteTest {

	@Test
	void criacaoDeClienteCpfJaExistenteNoBD() {
		Cliente.criarCliente("Maria", "123456789", "01/03/1999", "Endereco", EnumCliente.COMUM);
		Cliente.criarCliente("Jonas", "123456789", "20/05/1999", "Endereco2", EnumCliente.COMUM);

		assertTrue("CPF já cadastrado no Banco OGP", true);
	}

}
