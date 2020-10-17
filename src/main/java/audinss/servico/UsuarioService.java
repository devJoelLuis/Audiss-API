package audinss.servico;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import audinss.entidades.Cliente;
import audinss.entidades.Permissao;
import audinss.entidades.Usuario;
import audinss.repository.ClienteRepository;
import audinss.repository.UsuarioRepository;
import audinss.response.Response;

@Service
public class UsuarioService {
	
	
  @Autowired
  private UsuarioRepository repo;
  

  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  
  @Autowired
  private ClienteRepository repoCli;
  
  private String idUsuarioLogado = "";
  
  private List<String> permissoesUsuario = new ArrayList<>();
  
  
  //cadastrar
  public Response<Usuario> cadastrar(Usuario u) {
	  Response<Usuario> response = new Response<>();
	  try {
		if (u == null) {
			throw new Exception("o usuário está nulo!!!");
		}
		validarUsuario(u);
		u.setSenha(encoder.encode(u.getSenha()));
		response.setDados(repo.save(u));
		return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar cadastrar um novo usuário: "+ e.getMessage());
		return response;
	}
  }
  
  


private void validarUsuario(Usuario u) throws Exception {
	
	List<String> permissoesString = new ArrayList<>();
	for (Permissao p: u.getPermissoes()) {
		permissoesString.add(p.getPermissao());
	}
	
	if (!permissoesString.contains("ROLE_ADMIN")) {
	  if (u.getClientes() == null) {
		throw new Exception("o cliente não foi informado!");
	   }
	}
	Long count = repo.countByEmail(u.getEmail());
	if (count > 0) {
		throw new Exception("já existe um usuário com o e-mail "+ u.getEmail()+" cadastrado no banco de dados!!!");	
	}
 }



//editar
  public Response<Usuario> editar(Usuario u) {
	  Response<Usuario> response = new Response<>();
	  try {
		  if (u == null) {
				throw new Exception("o usuário está nulo!!!");
			}
		 Optional<Usuario> userOp = repo.findById(u.getId());
		 if (!userOp.isPresent()) {
			 throw new Exception("não foi possível encontrar um usuário com o id "+ u.getId());
		 }
		
		 Usuario userBanco = userOp.get();
		 pegarNomeEIdUsuarioLogado();
		 if (idUsuarioLogado == null || permissoesUsuario == null || permissoesUsuario.size() == 0) {
			 throw new Exception("não foi possível as permissões do usuário logado!!!");
		 }
		 if (!permissoesUsuario.contains("ROLE_ADMIN")) {
			 //VERIFICA SE O USUÁRIO QUE ESTA REQUISITANDO É O MESMO DO BANCO
			 int idlogado = Integer.valueOf(idUsuarioLogado);
			 if (userBanco.getId() != idlogado) {
				 throw new Exception("sem permissão para carregar o usuário requisitado!!!"); 
			 }
		 }
		 String senhaTemp = userBanco.getSenha();
		 if (permissoesUsuario.contains("ROLE_ADMIN")) {
			 // admin pode alterar todas propriedades
			 BeanUtils.copyProperties(u, userBanco);
		 } else {
			 // outros usuários só podem alterar a senha, email e o nome
			userBanco.setEmail(u.getEmail());
			userBanco.setNome(u.getNome());
		 }
		
		 //todas permissões podem trocar a senha
		 if (u.getSenha() != null && !u.getSenha().equals("")) {
			 userBanco.setSenha(encoder.encode(u.getSenha()));
		 } else {
			 userBanco.setSenha(senhaTemp);
		 }
		 
		 response.setDados(repo.save(userBanco));
		 return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar editar um usuário: "+ e.getMessage());
		return response;
	}
  }
  
  
  
  // excluir
  public Response<String> excluir(int idUser) {
	  Response<String> response = new Response<>();
	  try {
		 Optional<Usuario> userOp = repo.findById(idUser);
		 if (!userOp.isPresent()) {
			 throw new Exception("não foi possível encontrar um usuário com o id "+ idUser);
		 }
		
		 Usuario userBanco = userOp.get();  
		 repo.delete(userBanco);
		response.setDados("ok");
		return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar excluir um usuário: "+ e.getMessage());
		return response;
	}
  }
  
  
  
  // get id
  public Response<Usuario> getById(int idUser) {
	  Response<Usuario> response = new Response<>();
	  try {

			pegarNomeEIdUsuarioLogado();
			if (permissoesUsuario == null || permissoesUsuario.size() == 0) {
				throw new Exception("não foi possível encontrar as permissões do usuário logado");
			}
			if (idUsuarioLogado == null) {
				throw new Exception("não foi possível encontrar o id do usuário logado");
			}
			if (!permissoesUsuario.contains("ROLE_ADMIN")) {
				//COMPARA O ID SOLICITADO COM O DO USUÁRIO LOGADO
				int idUserLogadoInt = Integer.valueOf(idUsuarioLogado);
				if (idUserLogadoInt != idUser) {
					throw new Exception("você não tem permissão para acessar o usuário com id: "+ idUser);
				}
			}
			  Optional<Usuario> userOp = repo.findById(idUser);
				 if (!userOp.isPresent()) {
					 throw new Exception("não foi possível encontrar um usuário com o id "+ idUser);
				 }
				Usuario userBanco = userOp.get();  
			 response.setDados(userBanco);
			 return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar consultar pelo id do usuário: "+ e.getMessage());
		return response;
	}
  }
  
  
  // get by email
  public Response<Usuario> getByEmail(String email) {
	  Response<Usuario> response = new Response<>();
	  try {
		  
		  Optional<Usuario> userOp = repo.findByEmail(email);
			 if (!userOp.isPresent()) {
				 throw new Exception("não foi possível encontrar um usuário com o email "+ email);
			 }
			
			 pegarNomeEIdUsuarioLogado();
			 if (idUsuarioLogado == null || permissoesUsuario == null || permissoesUsuario.size() == 0) {
				 throw new Exception("não foi possível encontrar o usuário logado!!!");
			 }
			 Usuario userBanco = userOp.get();  
			 if (!permissoesUsuario.contains("ROLE_ADMIN")) {
				 //VERIFICA SE O USUÁRIO QUE ESTA REQUISITANDO É O MESMO DO BANCO
				 int idlogado = Integer.valueOf(idUsuarioLogado);
				 if (userBanco.getId() != idlogado) {
					 throw new Exception("sem permissão para carregar o usuário requisitado!!!"); 
				 }
			 }
			 
			 
			 response.setDados(userBanco);
			 return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar consultar pelo id do usuário: "+ e.getMessage());
		return response;
	}
  }
  
  
  
  
  // get all 
  public Response<Page<Usuario>> getAll(int page, int size, String nome) {
	  Response<Page<Usuario>> response = new Response<>();
	  try {
		   Pageable pageable = PageRequest.of(page, size);
		   Page<Usuario> usuarios = null;
		   if (nome.equals("")) {
			  usuarios  = repo.findAllByOrderByNomeAsc(pageable); 
		   } else {
			   usuarios = repo.findByNomeContainingOrderByNomeAsc(nome, pageable);
		   }
		   
		   response.setDados(usuarios);
		   return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar consultar todos os usuários: "+ e.getMessage());
		return response;
	}
  }
  
  
  
  
  // get all por id cliente
  public Response<List<Usuario>> getAllIdCliente(int idcliente) {
	  Response<List<Usuario>> response = new Response<>();
	  try {
		  Optional<Cliente> cliOp = repoCli.findById(idcliente);
		  if (!cliOp.isPresent()) {
				 throw new Exception("não foi possível encontrar um cliente com o id "+ idcliente);
			 }
		  List<Usuario> usuarios = repo.findByClientesOrderByNomeAsc(cliOp.get());
		   response.setDados(usuarios);
		return response;
	} catch (Exception e) {
		response.getErros().add("Ocorreu o seguinte erro ao tentar consultar todos os usuários pelo id do cliente: "+ e.getMessage());
		return response;
	}
  }
  
  
  
  
  //PEGAR USUÁRIO LOGADO FAZENDO REQUISIÇÕES NA API
	@SuppressWarnings("unchecked")
	public void pegarNomeEIdUsuarioLogado() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();        
		Object details = authentication.getDetails();   
		if ( details instanceof OAuth2AuthenticationDetails ) {
		    OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)details;
			Map<String, Object> decodedDetails = (Map<String, Object>)oAuth2AuthenticationDetails.getDecodedDetails();
		    System.out.println(decodedDetails);
		    idUsuarioLogado = decodedDetails.get("iduser").toString();
		    permissoesUsuario = (List<String>) decodedDetails.get("authorities");
          
		}
     }
  

}// fecha classe
