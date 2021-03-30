package com.erivan.produtos.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.erivan.produtos.model.entity.Produto;
import com.erivan.produtos.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@CrossOrigin("*") //recebe requisição de qualquer dominio
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository repository;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Produto save(@RequestBody Produto produto) {
		return repository.save(produto);
	}
	
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		repository.deleteById(id);
	}
	
	//exemplo pagina zero, 1 arquivo por pagina. ou pode enviar somente o /api/produtos que ele usa o padrão
    //http://localhost:8080/api/produtos?page=0&size=1
	@GetMapping
	public Page<Produto> list(
			@RequestParam(value = "page", defaultValue = "0")   Integer pagina, //pagina, por padrão retorna a pagina zero
	         @RequestParam(value = "size", defaultValue = "10")  Integer tamanhoPagina //tamanho da pagina, retorna a pagina com 10 registros
			){
		//ordena por nome
        Sort sort = Sort.by(Sort.Direction.ASC, "nome");
        //passa para o repository, para fazer a paginacao
        PageRequest pageRequest = PageRequest.of(pagina, tamanhoPagina, sort);
        return repository.findAll(pageRequest);
	}
	

	/*
	@GetMapping
	public List<Produto> list(){
		return repository.findAll();
	}
	*/
	
	@PutMapping("{id}/foto")
	public byte[] addPhoto(@PathVariable Long id, @RequestParam("foto") Part arquivo) {
		Optional<Produto> produto = repository.findById(id);
		//transforma em um array de bytes
	    return produto.map( c -> {
            try{
                InputStream is = arquivo.getInputStream();
                //array de bytes vai ser do tamanho do arquivo
                byte[] bytes = new byte[(int) arquivo.getSize()];
                //pega o inputStream e joga no array de bytes
                IOUtils.readFully(is, bytes);
                c.setFoto(bytes); //seta  a foto
                repository.save(c); //salva
                is.close(); //fecha o inputStream
                return bytes;
            }catch (IOException e){
                return null;
            }
        }).orElse(null);
	}

}
