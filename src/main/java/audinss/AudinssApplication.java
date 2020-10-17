package audinss;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import audinss.entidades.Cliente;
import audinss.repository.ClienteRepository;
import audinss.repository.InitialSqlInserts;

@SpringBootApplication
public class AudinssApplication implements CommandLineRunner {
	
	
	//@Autowired
	//private UsuarioRepository repo;
	
	//@Autowired
	//private PermissaoRepository repoPer;
	
	//@Autowired
	//private TaxaSelicRepository repoSelic;
	
	
	@Autowired
	private ClienteRepository repoCli;
	
	@Autowired
	private InitialSqlInserts inicialInserts;
	
	
	

	public static void main(String[] args) {
		SpringApplication.run(AudinssApplication.class, args);
	}

	@Override
	public void run(String... args) {
		
		try {
			
			Cliente cli1 = new Cliente();
			Cliente cli2 = new Cliente();
			
			//Usuario user1 = new Usuario();
			//Usuario user2 = new Usuario();
			
			/*
			Permissao p1 = new Permissao();
			Permissao p2 = new Permissao();
			
			
			
			p1.setPermissao("ROLE_ADMIN");
			p2.setPermissao("ROLE_USER");
			
			if (repoPer.existsByPermissao(p1.getPermissao())) {
				p1 = repoPer.findByPermissao(p1.getPermissao());
			} else {
				p1 = repoPer.save(p1);
			}
			
			
			if (repoPer.existsByPermissao(p2.getPermissao())) {
				p2 = repoPer.findByPermissao(p2.getPermissao());
			} else {
				p2 = repoPer.save(p2);
			}
			*/
			
			cli1.setNome("PREFEITURA MUNICIPAL DA ESTÂNCIA DE SOCORRO");
			cli1.setCnpj("999999999999999999999");
			cli1.setInicio(LocalDate.now());
			cli1.setFim(LocalDate.now());
			cli1.setLogotipo("data:application/octet-stream;base64,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");
			
			cli2.setNome("PREFEITURA DE LINDOIA");
			cli2.setCnpj("1111111111111111111111");
			cli2.setInicio(LocalDate.now());
			cli2.setFim(LocalDate.now());
			cli2.setLogotipo("data:application/octet-stream;base64,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");
			
			if (repoCli.existsByNome(cli1.getNome()))
			{
				cli1 = repoCli.findByNome(cli1.getNome());
			} else {
				cli1 = repoCli.save(cli1);
			}
			
			
			if (repoCli.existsByNome(cli2.getNome()))
			{
				cli2 = repoCli.findByNome(cli2.getNome());
			} else {
				cli2 = repoCli.save(cli2);
			}
			
			
			//user1.setEmail("joel.luis.pinto@gmail.com");
			//user1.setNome("Joel Luis Pinto");
			//user1.setSenha("$2a$10$28OA8jubS7g9ji2Hx7UeR.qcEXCv9McU5B2JJZqDr5kXjcmsJfrSi");
			
			/*
			user1.getPermissoes().add(p1);
			
			user2.setEmail("user");
			user2.setNome("Usuário Normal");
			user2.setSenha("$2a$10$2zi4v/0COqNsgywde/YYfuczQmJ19m2VASecSiunfYP/QL6uHJ/VO");
			user2.getPermissoes().add(p2);
			user2.getClientes().add(cli2);
		     */
			//if (!repo.existsByEmail(user1.getEmail()))
			//     repo.save(user1);
			/*
			if (!repo.existsByEmail(user2.getEmail()))
				repo.save(user2);
			*/
			
			
			
			/*
			//################################ converter taxa selic txt e gravar no banco
			final String CAMINHO = 	"c:\\selic\\selic_janeiro_de_2013_a_fevereiro_de_2020.txt"; // O CAMINHO DO ARQUIVO QUE CONTEM AS TAXAS
			final int ANOINICIO = 2013; // O PRIMEIRO ANO DO ARQUIVO
			
			try (BufferedReader reader = new BufferedReader(new FileReader(CAMINHO))) {
				int mes = 1;
				String line;
				while ((line = reader.readLine()) != null) {
					//System.out.println(line);
					String[] taxas = line.split("%");
					int anoInicio = ANOINICIO;
					for (String tx: taxas) {
						System.out.println("ano " + anoInicio + ", mês "+ mes + ", taxa "+ tx.trim().replace(",", "."));
						TaxaSelic selic = new TaxaSelic();
						selic.setAno(anoInicio);
						selic.setMes(mes);
						selic.setTaxa(Double.valueOf(tx.trim().replace(",", ".")));
						repoSelic.save(selic);
						
						anoInicio += 1;
					}
					mes += 1;
					
				}

			}

			*/
			
			inicialInserts.inserts();
			
		} catch (Exception e) {
			System.out.println("Erro: "+e.getMessage());
		}
		
	}

}
