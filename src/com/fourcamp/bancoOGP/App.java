import services.DataBase;

public class App {

	public static void main(String[] args) {
		DataBase.starter();

		DataBase.getCPByCpf("11203406673").efetuarDeposito("-50", "123");
		DataBase.getCPByCpf("11203406673").efetuarDeposito("0", "123");
		DataBase.getCPByCpf("11203406673").efetuarDeposito("50", "123");
		DataBase.getCPByCpf("11203406673").efetuarDeposito("50", "124");
		DataBase.getCPByCpf("11203406673").efetuarSaque("60.1", "123");
		DataBase.getCPByCpf("11203406673").efetuarSaque("-50", "123");
		DataBase.getCPByCpf("11203406673").efetuarSaque("0", "123");
		DataBase.getCPByCpf("11203406673").efetuarSaque("10", "123");
		DataBase.getCPByCpf("11203406673").efetuarSaque("10", "1235");
		DataBase.getCPByCpf("11203406673").exibirExtrato("123");
		DataBase.getCPByCpf("11203406673").trocarSenha("1234", "555");
		DataBase.getCPByCpf("11203406673").trocarSenha("1234", "123");

	}

}
