package audinss.servico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import audinss.DTO.FolhaCorrecaoLista;
import audinss.entidades.Cliente;
import audinss.entidades.FolhaCorrecao;
import audinss.entidades.Legislacao;
import audinss.entidades.Movimento;
import audinss.entidades.VerbaPadrao;
import audinss.repository.ClienteRepository;
import audinss.repository.FolhaCorrecaoRepository;
import audinss.repository.LegislacaoRepository;
import audinss.repository.MovimentoRepository;
import audinss.repository.VerbaPadraoRepository;
import audinss.response.Response;

@Service
public class FolhaCorrecaoService {
	
	@Autowired
	private FolhaCorrecaoRepository repo;

	@Autowired
	private VerbaPadraoRepository repoVer;
	
	@Autowired
	private LegislacaoRepository repoLe;
	
	@Autowired
	private ClienteRepository repoCli;
	
	@Autowired
	private MovimentoRepository repoMov;
	
	@Autowired
	private FolhaCorrecaoRepository repoFc;
	
	
	
	//cadastrar
	public Response<FolhaCorrecao> cadastrar(FolhaCorrecao fc) {
		Response<FolhaCorrecao> response = new Response<FolhaCorrecao>();
		try {
			
			Optional<VerbaPadrao> vOp =repoVer.findById(fc.getVerbaPadrao().getId());
			if(!vOp.isPresent()) {
				throw new Exception("não foi possível encontrar a verba com o id "+ fc.getVerbaPadrao().getId());
			}
			Optional<Legislacao> lOp = repoLe.findById(fc.getLegislacao().getId());
			if(!lOp.isPresent()) {
				throw new Exception("não foi possível encontrar a legislação com o id "+ fc.getVerbaPadrao().getId());
			}
			fc.setVerbaPadrao(vOp.get());
			fc.setLegislacao(lOp.get());
			response.setDados(repo.save(fc));
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar cadastrar uma folha de correção: "+ e.getMessage());
			return response;
		}
	}
	
	
	//alterar
	public Response<FolhaCorrecao> alterar(FolhaCorrecao fc) {
		Response<FolhaCorrecao> response = new Response<FolhaCorrecao>();
		try {
			fc.setConfigurado(true);
			Optional<FolhaCorrecao> fOp = repo.findById(fc.getId());
			if(!fOp.isPresent()) {
				throw new Exception("não foi possível encontrar a folha de correção com o id "+ fc.getId());
			}
			
			Optional<VerbaPadrao> vOp =repoVer.findById(fc.getVerbaPadrao().getId());
			if(!vOp.isPresent()) {
				throw new Exception("não foi possível encontrar a verba com o id "+ fc.getVerbaPadrao().getId());
			}
			Optional<Legislacao> lOp = repoLe.findById(fc.getLegislacao().getId());
			if(!lOp.isPresent()) {
				throw new Exception("não foi possível encontrar a legislação com o id "+ fc.getVerbaPadrao().getId());
			}
			FolhaCorrecao fcBanco = fOp.get();
			BeanUtils.copyProperties(fc, fcBanco);
			fcBanco.setLegislacao(lOp.get());
			fcBanco.setVerbaPadrao(vOp.get());
			response.setDados(repo.save(fcBanco));
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar alterar uma folha de correção: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	//excluir
	public Response<String> excluir(int id) {
		Response<String> response = new Response<>();
		try {
			
			Optional<FolhaCorrecao> fOp = repo.findById(id);
			if(!fOp.isPresent()) {
				throw new Exception("não foi possível encontrar a folha de correção com o id "+ id);
			}
						
			FolhaCorrecao fcBanco = fOp.get();
			repo.delete(fcBanco);
			response.setDados("ok");
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar excluir uma folha de correção: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	
	//getbyid
	public Response<FolhaCorrecao> getById(int id) {
		Response<FolhaCorrecao> response = new Response<FolhaCorrecao>();
		try {
			
			Optional<FolhaCorrecao> fOp = repo.findById(id);
			if(!fOp.isPresent()) {
				throw new Exception("não foi possível encontrar a folha de correção com o id "+ id);
			}
			FolhaCorrecao fcBanco = fOp.get();
			response.setDados(fcBanco);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar uma folha de correção por id: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	
	// get all legislacao id
	public Response<List<FolhaCorrecao>> getAllLegislacaoId(int idleg) {
		Response<List<FolhaCorrecao>> response = new Response<>();
		try {
			
			List<FolhaCorrecao> listaFc = repo.buscarPorLegislacaoId(idleg); 
			response.setDados(listaFc);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar pelo id da legislação: "+ e.getMessage());
			return response;
		}
	}
	
	
	// get all verbaPadrao id
		public Response<List<FolhaCorrecao>> getAllVerbaPadraoId(int idverba) {
			Response<List<FolhaCorrecao>> response = new Response<>();
			try {
				
				List<FolhaCorrecao> listaFc = repo.buscarPorVerbaPadraoId(idverba);
				response.setDados(listaFc);
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu um erro ao tentar consultar pelo id da VerbaPadrão: "+ e.getMessage());
				return response;
			}
		}

		
		
		
       // get all por id da legislacao e do cliente
		public Response<List<FolhaCorrecao>> getAllLegislacaoIdClienteId(int idleg, int idcliente) {
			Response<List<FolhaCorrecao>> response = new Response<>();
			try {
				if (idleg == 0) {
					throw new Exception("o id da legislação não foi informado corretamente");
				}
				if (idcliente == 0) {
					throw new Exception("o id do cliente não foi informado corretamente");
				}
				List<FolhaCorrecao> listaFc = repo.buscarPorLegislacaoIdClienteId(idleg, idcliente); 
				response.setDados(listaFc);
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu um erro ao tentar consultar pelo id da legislação e do cliente: "+ e.getMessage());
				return response;
			}
		}

		
		
		public Response<Boolean> verificarExisteFcClienteId(int clienteid) {
			Response<Boolean> response = new Response<>();
			try {
				Boolean teste = repo.existsByClienteId(clienteid);
				response.setDados(teste);
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu seguinte erro ao tentar verificar as FC: "+ e.getMessage());
				return response;
			}
		}
		
		
		
		
		// cadastrar todas as verbas da folha de correcao
		@Transactional
		public Response<List<FolhaCorrecao>> cadastrarLista(FolhaCorrecaoLista fcs) {
			 Response<List<FolhaCorrecao>> response = new Response<>();
			 try {
				if (fcs == null) {
					throw new Exception("a lista de correção está nula");
				}
				Legislacao l = new Legislacao();
				Cliente cli = new Cliente();
				// mes da entrega do requerimento ao tribunal de contas
				LocalDate dataEntrega = LocalDate.of(1991, 1, 1);
				// ano da entrega do requerimento
				int anoEntrega = 0;
				int count = 1;
	            List<FolhaCorrecao> folhas = new ArrayList<>();
				for (FolhaCorrecao fc: fcs.getFcs()) {
					//seta todas as fc como configurada.
					fc.setConfigurado(true);
					if (count == 1) {
						l = repoLe.findById(fc.getLegislacao().getId()).get();
						cli = repoCli.findById(fc.getCliente().getId()).get();
						  //se for uma legislação que seja válida a partir da data de entrega do requerimento
					     if (l.isExigeDataInicio()) {
					    	 dataEntrega = fc.getDataInicio();
					    	 anoEntrega = fc.getDataInicio().getYear();
					     }
						count -= 1;
					}
					 
					VerbaPadrao vb = repoVer.findById(fc.getVerbaPadrao().getId()).get();
					fc.setLegislacao(l);
					fc.setCliente(cli);
					fc.setVerbaPadrao(vb);
					folhas.add(repo.save(fc));
				}
				 Long ct = repoMov.countByAliquotasMesClienteIdAndLegislacao(cli.getId(), l.getLegislacao());
				 if (ct > 0) {
					 throw new Exception("a FC da legislação "+ l.getLegislacao() +" já se encontra cadastrada para o cliente "+cli.getNome());
				 }
			  	
				  
				       
		            List<Movimento> movimentos = repoMov.buscarMovimentoFp(anoEntrega, cli.getId(), "FP");
		            System.out.println(movimentos.size());
		            
						// cria movimentos de calculo da nova legislação cadastrada, caso aja fp já cadastrada
						if (movimentos.size() > 0) {
						  for (Movimento m: movimentos) {	
							
								//verificar se a legislacao exige data de entrada do processo
								if (l.isExigeDataInicio()) {
									LocalDate tempTest;
									if (m.getAliquotasMes().getMes() == 13) {
									  tempTest = LocalDate.of(m.getAliquotasMes().getAno(), 12, 1);
									} else {
									  tempTest = LocalDate.of(m.getAliquotasMes().getAno(), m.getAliquotasMes().getMes(), 1);
									}
									
									if (tempTest.isBefore(dataEntrega)) continue;
								}
							  
							double totalAliquota = m.getAliquotasMes().getInssRat() + m.getAliquotasMes().getInssTerceiros() +
									m.getAliquotasMes().getInssEmpresa();
							Integer idcliente = m.getAliquotasMes().getCliente().getId();
							
							FolhaCorrecao fc = repoFc.findByVerbaPadraoCodigoAndLegislacaoLegislacaoAndClienteId(m.getCodigo(), m.getLegislacao(), idcliente);
							
								Movimento mov = new Movimento();
								
									// verificar se é desconto ou provento
									if (m.getTipo().equals("PROVENTO")) {
										//verificar se a tributação da fc está diferente da fp
										if (fc.getFc().equals(m.getFp()) && m.getFp().equals("NT")) {
											mov.setAliquotasMes(m.getAliquotasMes());
											mov.setBaseCalcInss(0D);
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("NT");
											mov.setFp("NT");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("PROVENTO");
											mov.setValorInssFp(0D);
											mov.setValorPagar(0D);
											mov.setValorProvento(m.getValorProvento());
											mov.setValorRecuperar(0D);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
										
										if (fc.getFc().equals(m.getFp()) && m.getFp().equals("T")) {
											mov.setAliquotasMes(m.getAliquotasMes());
											
											mov.setBaseCalcInss(m.getValorProvento());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("T");
											mov.setFp("T");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("PROVENTO");
											if (m.getValorProvento() != 0D && totalAliquota != 0D) {
												mov.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
											} else {
												mov.setValorInssFp(0D);
											}								
											mov.setValorPagar(0D);
											mov.setValorProvento(m.getValorProvento());
											mov.setValorRecuperar(0D);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
										
											
											// gerar se a diferença é a pagar ou receber se FC T e Fp NT = pagar, se FC NT e FP T = receber
										
										// recuperar ou receber
										if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("NT") &&  m.getFp().equals("T")) {
											double valorInss = 0D;
											if (m.getValorProvento() != 0D && totalAliquota != 0D) {
												valorInss = (m.getValorProvento() * totalAliquota) / 100;
											} else {
												valorInss = 0D;
											}						
											mov.setAliquotasMes(m.getAliquotasMes());
											mov.setBaseCalcInss(m.getValorProvento());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("NT");
											mov.setFp("T");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("PROVENTO");
											mov.setValorInssFp(valorInss);		
											mov.setValorPagar(0D);
											mov.setValorProvento(m.getValorProvento());
											mov.setValorRecuperar(valorInss);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
										
										
										// valor pagar
										if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("T") &&  m.getFp().equals("NT")) {
											double valorInss = 0D;
											if (m.getValorProvento() != 0D && totalAliquota != 0D) {
												valorInss = (m.getValorProvento() * totalAliquota) / 100;
											} else {
												valorInss = 0D;
											}						
											mov.setAliquotasMes(m.getAliquotasMes());
											mov.setBaseCalcInss(m.getValorProvento());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("T");
											mov.setFp("NT");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("PROVENTO");
											mov.setValorInssFp(valorInss);		
											mov.setValorPagar(0D);
											mov.setValorProvento(m.getValorProvento());
											mov.setValorRecuperar(0D);
											mov.setValorPagar(valorInss);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
									   
									} else {
										//verificar se a tributação da fc está diferente da fp
										if (fc.getFc().equals(m.getFp()) && m.getFp().equals("NT")) {
											mov.setAliquotasMes(m.getAliquotasMes());
											mov.setBaseCalcInss(m.getBaseCalcInss());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("NT");
											mov.setFp("NT");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("DESCONTO");
											mov.setValorInssFp(0D);
											mov.setValorPagar(0D);
											mov.setValorProvento(0D);
											mov.setValorRecuperar(0D);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
										
										if (fc.getFc().equals(m.getFp()) && m.getFp().equals("T")) {
											mov.setAliquotasMes(m.getAliquotasMes());
											
											mov.setBaseCalcInss(m.getBaseCalcInss());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("T");
											mov.setFp("T");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("DESCONTO");
											if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
												mov.setValorInssFp((m.getBaseCalcInss() * totalAliquota) / 100);
											} else {
												mov.setValorInssFp(0D);
											}								
											mov.setValorPagar(0D);
											mov.setValorProvento(0D);
											mov.setValorRecuperar(0D);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
										
											
											// gerar se a diferença é a pagar ou receber se FC T e Fp NT = pagar, se FC NT e FP T = receber
										
										// recuperar ou receber
										if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("NT") &&  m.getFp().equals("T")) {
											double valorInss = 0D;
											if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
												valorInss = (m.getBaseCalcInss() * totalAliquota) / 100;
											} else {
												valorInss = 0D;
											}						
											mov.setAliquotasMes(m.getAliquotasMes());
											mov.setBaseCalcInss(m.getBaseCalcInss());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("NT");
											mov.setFp("T");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("DESCONTO");
											mov.setValorInssFp(valorInss);		
											mov.setValorPagar(valorInss);
											mov.setValorProvento(0D);
											mov.setValorRecuperar(0D);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
										
										
										// valor pagar
										if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("T") &&  m.getFp().equals("NT")) {
											double valorInss = 0D;
											if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
												valorInss = (m.getBaseCalcInss() * totalAliquota) / 100;
											} else {
												valorInss = 0D;
											}						
											mov.setAliquotasMes(m.getAliquotasMes());
											mov.setBaseCalcInss(m.getBaseCalcInss());
											mov.setCodigo(m.getCodigo());
											mov.setDescricao(m.getDescricao());
											mov.setFc("T");
											mov.setFp("NT");
											mov.setLegislacao(l.getLegislacao());
											mov.setTipo("DESCONTO");
											mov.setValorInssFp(valorInss);		
											mov.setValorPagar(0D);
											mov.setValorProvento(0D);
											mov.setValorRecuperar(valorInss);
											mov.setValorPagar(0D);
											if (l.isExigeDataInicio())
												mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
											repoMov.save(mov);
											continue;
										} 
									}
								
							
						  }
						}
						
				response.setDados(folhas);
				return response;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				response.getErros().add("Ocorreu um erro ao cadastrar a lista de folha de correção: "+ e.getMessage());
				return response;
			}
		}

		
		
		
		// alterar todas as verbas da folha de correção
		@Transactional
		public Response<List<FolhaCorrecao>> alterarLista(FolhaCorrecaoLista fcs) {
			 Response<List<FolhaCorrecao>> response = new Response<>();
			 try {
				if (fcs == null) {
					throw new Exception("a lista de correção está nula");
				}
				
	            List<FolhaCorrecao> folhasAlterada = new ArrayList<>();
				for (FolhaCorrecao fc: fcs.getFcs()) {
					Optional<FolhaCorrecao> fcOp = repo.findById(fc.getId());
					if (!fcOp.isPresent()) {
						throw new Exception("não foi possível encontrar um folha de correção com o id "+fc.getId());
					}
					FolhaCorrecao fcBanco = fcOp.get();
					BeanUtils.copyProperties(fc, fcBanco);
					folhasAlterada.add(repo.save(fcBanco));
				}
				List<FolhaCorrecao> folhasBanco = repo.saveAll(folhasAlterada);
				response.setDados(folhasBanco);
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu um erro ao alterar a lista de folha de correção: "+ e.getMessage());
				return response;
			}
		}
		
		
		
		
		
       // delete all 
		public Response<String> excluirFolha(int idcliente, int idleg) {
			Response<String> response = new Response<String>();
			try {
				if (idleg == 0) {
					throw new Exception("o id da legislação não foi informado corretamente");
				}
				if (idcliente == 0) {
					throw new Exception("o id do cliente não foi informado corretamente");
				}
				List<FolhaCorrecao> listaFc = repo.buscarPorLegislacaoIdClienteId(idleg, idcliente); 
				repo.deleteAll(listaFc);
				response.setDados("ok");
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu um erro ao tentar excluir a folha de correção: "+ e.getMessage());
				return response;
			}
			
		}

		
		
		
        // salva alteração de uma lisca de correções e tambem todos os movimentos do sistema
		@Transactional
		public Response<List<FolhaCorrecao>> alterarListaMovimentos(FolhaCorrecaoLista fcs) {
			 Response<List<FolhaCorrecao>> response = new Response<>();
			 try {
				if (fcs == null) {
					throw new Exception("a lista de correção está nula");
				}
				Integer idcliente = 0;
				
	            List<FolhaCorrecao> folhasAlterada = new ArrayList<>();
				for (FolhaCorrecao fc: fcs.getFcs()) {
					fc.setConfigurado(true);
					Optional<FolhaCorrecao> fcOp = repo.findById(fc.getId());
					if (!fcOp.isPresent()) {
						throw new Exception("não foi possível encontrar um folha de correção com o id "+fc.getId());
					}
					FolhaCorrecao fcBanco = fcOp.get();
					fcBanco.setConfigurado(true);
					
					if (fc.getFc().equals(fcBanco.getFc())) {
						folhasAlterada.add(fcBanco);
						// pula para o proximo loop se não tiver alterações
						continue;
					}
					
					
					BeanUtils.copyProperties(fc, fcBanco, "id");
					
					String legislacao = fcBanco.getLegislacao().getLegislacao().trim();
					String descricao = fc.getVerbaPadrao().getDescricao().trim();
					
					if (idcliente == 0) idcliente = fcBanco.getCliente().getId();// pega o id do cliente apenas uma vez no loop
					
					//buscar todos os movimentos pelo id do cliente e nome da legislacao
					List<Movimento> movimentos = repoMov.buscarMovimentoClienteIdLegislacaoDescricao(idcliente, legislacao, descricao);
					
					for (Movimento m: movimentos) {
						m.setFc(fcBanco.getFc());
						// recalcular verba
						if (!m.getLegislacao().equals("FP")) {
							Double totalAliquota = m.getAliquotasMes().getInssEmpresa() + m.getAliquotasMes().getInssRat() + m.getAliquotasMes().getInssTerceiros();
							if (m.getTipo().equals("PROVENTO")) {
								m.setBaseCalcInss(m.getValorProvento());
								
								if (m.getFc().equals("T") && m.getFp().equals("NT")) {
									if (m.getValorProvento() > 0 && totalAliquota > 0) {
										m.setValorRecuperar(0D);
										m.setValorPagar((m.getValorProvento() * totalAliquota) / 100);
										m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
									}
								}
								if (m.getFc().equals("NT") && m.getFp().equals("T")) {
									if (m.getValorProvento() > 0 && totalAliquota > 0) {
										m.setValorRecuperar((m.getValorProvento() * totalAliquota) / 100);
										m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
										m.setValorPagar(0D);
									}
								}
								if (m.getFc().equals("NT") && m.getFp().equals("NT")) {
									m.setValorPagar(0D);
									m.setValorRecuperar(0D);
									m.setBaseCalcInss(0D);
									m.setValorInssFp(0D);
								}
								if (m.getFc().equals("T") && m.getFp().equals("T")) {
									m.setValorPagar(0D);
									m.setValorRecuperar(0D);
									m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
								}
								
							} else if (m.getTipo().equals("DESCONTO")){
								m.setValorProvento(0D);
								if (m.getFc().equals("T") && m.getFp().equals("NT")) {
									if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
										m.setValorRecuperar((m.getValorProvento() * totalAliquota) / 100);
										m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
										m.setValorPagar(0D);
									}
								}
								if (m.getFc().equals("NT") && m.getFp().equals("T")) {
									if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
										m.setValorRecuperar(0D);
										m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
										m.setValorPagar((m.getValorProvento() * totalAliquota) / 100);
									}
								}
								if (m.getFc().equals("NT") && m.getFp().equals("NT")) {
									m.setValorPagar(0D);
									m.setValorRecuperar(0D);
									m.setValorInssFp(0D);
								}
								if (m.getFc().equals("T") && m.getFp().equals("T")) {
									m.setValorPagar(0D);
									m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
									m.setValorRecuperar(0D);
								}
							}
						}
						
						repoMov.save(m);
					}
					
					
					folhasAlterada.add(repo.save(fcBanco));
				}
				List<FolhaCorrecao> folhasBanco = repo.saveAll(folhasAlterada);
				response.setDados(folhasBanco);
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu um erro ao alterar a lista de folha de correção: "+ e.getMessage());
				return response;
			}
		}
		
		
		
		
	
	

}//fecha classe
