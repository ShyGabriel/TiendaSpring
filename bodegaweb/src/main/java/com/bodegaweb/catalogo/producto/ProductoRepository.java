package com.bodegaweb.catalogo.producto;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoriaId(Long categoriaId);

    @Query(value = """
            select p from Producto p
            left join fetch p.categoria
            where (:categoriaId is null or p.categoria.id = :categoriaId)
              and (:activo is null or p.activo = :activo)
              and (
                    :q is null
                    or lower(p.nombre) like lower(concat('%', :q, '%'))
                    or lower(p.sku)    like lower(concat('%', :q, '%'))
              )
            """,
            countQuery = """
            select count(p) from Producto p
            where (:categoriaId is null or p.categoria.id = :categoriaId)
              and (:activo is null or p.activo = :activo)
              and (
                    :q is null
                    or lower(p.nombre) like lower(concat('%', :q, '%'))
                    or lower(p.sku)    like lower(concat('%', :q, '%'))
              )
            """)
    Page<Producto> buscar(@Param("categoriaId") Long categoriaId,
                          @Param("activo") Boolean activo,
                          @Param("q") String q,
                          Pageable pageable);
}
