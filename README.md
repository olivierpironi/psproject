<h1 align="center"> BANCO OGP </h1>

<h3 align="center"> 

![STATUS-FINALIZADO-green](https://user-images.githubusercontent.com/78506173/217345734-032f35d8-51ec-4585-8a97-2f3008f771f5.svg)

 </h3>
 
 <h3 align="center"> 
 
![coverage-80%-green](https://user-images.githubusercontent.com/78506173/217345748-7ba64526-bc18-4380-b778-f71f1fc08154.svg)
![tests-118 passes, 0 failed-blue](https://user-images.githubusercontent.com/78506173/217345757-c23dbe91-b543-497e-9c59-4485cf8eaf6c.svg)

</h3>


<h6 align="center"> 
 
![API-REST-orange](https://user-images.githubusercontent.com/78506173/213749530-c3e29755-63f2-43d8-860a-81c23fd3dec9.svg)
![-JAVA-yellowgreen](https://user-images.githubusercontent.com/78506173/213751115-c49e5daa-86bf-437f-9e81-22f53927f2c1.svg)
![-SPRINGBOOT (DATA, SECURITY)-lightgrey](https://user-images.githubusercontent.com/78506173/213750766-ca4acb29-0a33-4dc2-b524-d46c18ed71db.svg)
![-MySQL-blue](https://user-images.githubusercontent.com/78506173/213750497-5c0353b9-2e9c-4c2c-bc82-d612eaf6285d.svg)
![-MAVEN-9cf](https://user-images.githubusercontent.com/78506173/213756699-a20538f4-da6b-4a2a-b432-ad869d95f687.svg)
![-LOMBOK-yellow](https://user-images.githubusercontent.com/78506173/213756863-ea1b36f6-8dca-4762-813d-f1f09054ae58.svg)
![-JUNIT 5-red](https://user-images.githubusercontent.com/78506173/217346917-a1e18286-1d1b-428a-88cc-41858c8a3510.svg)

</h6>

As únicas requisições que estão abertas para utilização sem a necessidade de login são as requisições de cadastro de clientes e de contas, pode-se fazer login com qualquer tipo de conta cadastrada. Todas as contas tem sua senha encriptada ao ser armazenada no banco de dados. Desenvolvido utilizando uma adaptação da arquitetura clean architeture, o banco conta com as seguintes funcionalidades:

- `Conta Poupança`: 
  - Rendimento mensal através da poupança.
  - Depósito; 
  - Saque; 
  - Operação pix, cadastro e emissão de chavespix; 
  - Recebe pagamentos de cartões.
 
- `Conta Corrente`: 
  - Tarifa mensal de utilização da conta.
  - Depósito; 
  - Saque; 
  - Operação pix, cadastro e emissão de chavespix;
  - Recebe pagamentos de cartões;
  - Possibilidade de emissão de cartões de débito e crédito.
 
- `Cartão de Débito`: 
   - Bloqueio e desbloqueio de cartão; 
   - Troca de senha; 
   - Calcula taxa de utilização do cartão.
   - Pagar utilizando o saldo da conta corrente;
   - Taxação da operação de pagamento;
   - Limite diário de transação.
  
- `Cartão de Crédito`:
   - Bloqueio e desbloqueio de cartão; 
   - Troca de senha; 
   - Calcula taxa de utilização do cartão.
   - Pagar com sistema de crédito; 
   - Taxação da operação de pagamento;
   - Emitir fatura; 
   - Pagar fatura utilizando saldo da conta corrente.

🛠️Possíveis implementações:
 - Sistema de logs;
 - Sistema de roles utilizando spring secutiry;
 - Documentação com Swagger Fox.


 📁 Arquivo JSON para consumo da API

[BANCOOGP.postman_collection.zip](https://github.com/olivierpironi/psproject/files/10593788/BANCOOGP.postman_collection.zip)


