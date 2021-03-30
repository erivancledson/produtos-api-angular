package com.erivan.produtos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erivan.produtos.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{

}
