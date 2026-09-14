package com.bodegaweb.catalogo.categoria;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    List<Categoria> findByCategoriaPadreIsNullOrderByNombreAsc();

    List<Categoria> findByCategoriaPadreIdOrderByNombreAsc(Long categoriaPadreId);

    boolean existsByCategoriaPadreId(Long categoriaPadreId);
}
