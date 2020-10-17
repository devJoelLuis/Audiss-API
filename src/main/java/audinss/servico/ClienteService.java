package audinss.servico;

import java.time.LocalDate;
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
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import audinss.DTO.ClienteDTO;
import audinss.DTO.ClienteDashDto;
import audinss.entidades.Cliente;
import audinss.entidades.Usuario;
import audinss.repository.ClienteRepository;
import audinss.repository.UsuarioRepository;
import audinss.response.Response;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;

	@Autowired
	private UsuarioRepository repoUser;
	
    private String idUsuarioLogado = "";
    private List<String> permissoesUsuario;
	
	
	// cadastrar novo cliente
	public Response<Cliente> cadastrar(Cliente c) {
		Response<Cliente> response = new Response<Cliente>();
		try {
			
			Cliente cliCadastrado = repo.save(c);
			response.setDados(cliCadastrado);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar cadastrar um cliente: "+e.getMessage());
			return response;
		}
		
	}
	
	
	
	//editar cliente
	public Response<Cliente> alterar(Cliente c) {
		Response<Cliente> response = new Response<Cliente>();
		try {
			if (c == null) {
				throw new Exception("o cliente está nulo!!!");
			}
			Optional<Cliente> cBancoOp = repo.findById(c.getId());
			if (!cBancoOp.isPresent()) {
				throw new Exception("não foi possível encontrar um cliente com id "+ c.getId());
			}
			Cliente cBanco = cBancoOp.get();
			BeanUtils.copyProperties(c, cBanco);
			Cliente cliCadastrado = repo.save(cBanco);
			response.setDados(cliCadastrado);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar editar um cliente: "+e.getMessage());
			return response;
		}
		
	}
	
	
	//excluir cliente
	public Response<String> excluir(Integer id) {
		Response<String> response = new Response<>();
		try {
			if (id == null) {
				throw new Exception("o id está nulo!!!");
			}
			Optional<Cliente> cBancoOp = repo.findById(id);
			if (!cBancoOp.isPresent()) {
				throw new Exception("não foi possível encontrar um cliente com id "+ id);
			}
			Cliente cBanco = cBancoOp.get();
		    repo.delete(cBanco);
			response.setDados("ok");
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar excluir um cliente: "+e.getMessage());
			return response;
		}
		
	}
	
	
	
	
	
	//consultar todos
	public Response<Page<Cliente>> getAll(int page, int size, String param) {
		Response<Page<Cliente>> response = new Response<>();
		try {
			
			Pageable pageable = PageRequest.of(page, size);
			Page<Cliente> pc = null;
			if (param.contentEquals("")) {
				pc = repo.findAllByOrderByNomeAsc(pageable);
			} else {
				
				pc = repo.findByNomeContainingIgnoreCaseOrderByNomeAsc(pageable, param);
				
				if (pc.getTotalElements() < 1) {
					pc = repo.findByCnpjContainingIgnoreCaseOrderByNomeAsc(pageable, param);
				}
				
			}//fecha else	
			response.setDados(pc);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar consultar todos os clientes: "+e.getMessage());
			return response;
		}
	}
	
	
	
	
	
	
	
	//consultar por id
	public Response<Cliente> getById (Integer id) {
		Response<Cliente> response = new Response<Cliente>();
		try {
			if (id == null) {
				throw new Exception("o id está nulo!!!");
			}
			Optional<Cliente> cBancoOp = repo.findById(id);
			if (!cBancoOp.isPresent()) {
				throw new Exception("não foi possível encontrar um cliente com id "+ id);
			}
			Cliente cli = cBancoOp.get();
			response.setDados(cli);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar consultar um cliente por id: "+e.getMessage());
			return response;
		}
		
	}
	



	public Response<List<ClienteDTO>> getAllDto() {
		Response<List<ClienteDTO>> response = new Response<>();
		try {
			pegarNomeEIdUsuarioLogado();
			LocalDate dtnow = LocalDate.now();
			if (permissoesUsuario == null) {
				throw new Exception("não foi possível carregar a lista de permissões do usuário logado!");	
			}
			if (permissoesUsuario.size() == 0) {
				throw new Exception("não foi possível carregar a lista de permissões do usuário logado!");	
			}
			List<Cliente> clist = repo.findByIdGreaterThanOrderByNomeAsc(0);
			List<ClienteDTO> clistDto = new ArrayList<>();
			ClienteDTO cdto = null;
			if (permissoesUsuario.contains("ROLE_ADMIN")) {
				for (Cliente c : clist) {
					cdto = new ClienteDTO(c.getId(), c.getNome());
					clistDto.add(cdto);
				}
			} else {
				for (Cliente c : clist) {
					if (c.getFim().isAfter(dtnow) || c.getFim().isEqual(dtnow)) {
					cdto = new ClienteDTO(c.getId(), c.getNome());
					clistDto.add(cdto);
					}
				}
			}
			
			response.setDados(clistDto);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar carregar a lista de clientes: "+e.getMessage());
			return response;
		}
	}



	public Response<List<ClienteDTO>> getAllDtoUser(int iduser) {
		Response<List<ClienteDTO>> response = new Response<>();
		try {
			
			pegarNomeEIdUsuarioLogado();
			LocalDate dtnow = LocalDate.now();
			
			if (this.idUsuarioLogado == null || this.idUsuarioLogado.equals("")) {
				throw new Exception("não foi possível identificar o usuário logado no sistema");
			}
			int idUlogado = Integer.parseInt(this.idUsuarioLogado);
			if (iduser != idUlogado) {
				throw new Exception("não foi possível confirmar a autenticidade do usuário logado!!!");
			}
			
			Optional<Usuario> userOp = repoUser.findById(iduser);
			if (!userOp.isPresent()) {
				throw new Exception("não foi possível encontrar um usuário com o id "+ iduser);
			}
			
			Usuario userBanco = userOp.get();
			if (userBanco.getClientes() == null) {
				throw new Exception("o usuário "+ userBanco.getNome() +" não possui clientes vinculados");
			}
			if (userBanco.getClientes().size() == 0) {
				throw new Exception("o usuário "+ userBanco.getNome() +" não possui clientes vinculados");
			}
			List<Cliente> clientes = userBanco.getClientes();
			List<ClienteDTO> clientesDto = new ArrayList<ClienteDTO>();
			for (Cliente c: clientes) {
				if (c.getFim().isAfter(dtnow) || c.getFim().isEqual(dtnow)) {
				ClienteDTO cdto = new ClienteDTO();
				cdto.setId(c.getId());
				cdto.setNome(c.getNome());
				clientesDto.add(cdto);
			  }
			}
			response.setDados(clientesDto);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao carregar o cliente pelo id do usuário: "+e.getMessage());
			return response;
		}
	}
	
	
	
	 // utilizado pelo dashboard para exibir os clientes do usuário
		public Response<List<ClienteDashDto>> getAllDashDtoUser() {
			Response<List<ClienteDashDto>> response = new Response<List<ClienteDashDto>>();
			try {
				pegarNomeEIdUsuarioLogado();
				if (idUsuarioLogado == null) {
					throw new Exception("não foi possível identificar o id do usuário logado no sistema!!!");
				}
				//transformar o id string em int
				int idUserLogado = Integer.valueOf(idUsuarioLogado);
				Optional<Usuario> userOp = repoUser.findById(idUserLogado);
				if (!userOp.isPresent()) {
					throw new Exception("não foi possível encontrar um usuário com o id "+ idUserLogado);
				}
				
				Usuario userBanco = userOp.get();
				if (userBanco.getClientes() == null) {
					throw new Exception("o usuário "+ userBanco.getNome() +" não possui clientes vinculados");
				}
				if (userBanco.getClientes().size() == 0) {
					throw new Exception("o usuário "+ userBanco.getNome() +" não possui clientes vinculados");
				}
				List<Cliente> clientes = userBanco.getClientes();
				List<ClienteDashDto> clientesDto = new ArrayList<>();
				for (Cliente c: clientes) {
					ClienteDashDto cdto = new ClienteDashDto();
					cdto.setId(c.getId());
					cdto.setNome(c.getNome());
					cdto.setInicio(c.getInicio());
					cdto.setFim(c.getFim());
					cdto.setObs(c.getObs());
					cdto.setDatServidor(LocalDate.now());
					clientesDto.add(cdto);
				}
				response.setDados(clientesDto);
				return response;
				
			} catch (Exception e) {
				response.getErros().add("Ocorreu um erro ao carregar os clientes do usuário para o dashboard: "+e.getMessage());
				return response;
			}
			
		}
		
		
		public Response<Boolean> getLicencaIsValid(int idcli) {
			Response<Boolean> response = new Response<>();
			try {
				boolean comPermissao = false;
				boolean licenca = false;
				pegarNomeEIdUsuarioLogado();
				Cliente cli = new Cliente();
				int idUserLogado = Integer.parseInt(this.idUsuarioLogado);
				Usuario user = repoUser.findById(idUserLogado)
						.orElseThrow(() -> new Exception("não foi possível encontrar um usuário logado com o id: "+ idUserLogado));
				for (Cliente c: user.getClientes()) {
					if (c.getId() == idcli) {
						comPermissao = true;
						cli = c;
					}
				}
				if (!comPermissao) {
					throw new Exception("Você não tem permissão para acessar esse recurso!");
				}
				//verificar a validade da licença
				if (cli.getFim().isAfter(LocalDate.now())) {
					licenca = true;
				} else {
					licenca = false;
				}
				response.setDados(licenca);
				return response;
				
			} catch (Exception e) {
				response.getErros().add("Ocorreu o seguinte erro ao tentar verificar a licença: "+e.getMessage());
				return response;
			}
		}
	
	
	
	
	 //PEGAR USUÁRIO LOGADO FAZENDO REQUISIÇÕES NA API
	@SuppressWarnings("unchecked")
	public void pegarNomeEIdUsuarioLogado() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();        
		Object details = authentication.getDetails();   
		if ( details instanceof OAuth2AuthenticationDetails ){
		    OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)details;
			Map<String, Object> decodedDetails = (Map<String, Object>)oAuth2AuthenticationDetails.getDecodedDetails();
		    System.out.println(decodedDetails);
		    idUsuarioLogado = decodedDetails.get("iduser").toString();
		    permissoesUsuario = (List<String>) decodedDetails.get("authorities");
		}  
       }



	


   

     
  

	
	
	
	

}// fecha classe
