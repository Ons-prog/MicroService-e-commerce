package fr.insa.ms.inventory_service.dao;

import fr.insa.ms.inventory_service.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ArticleDao extends JpaRepository<Article, Long> {

    // Réserve le stock : décrémente seulement si la quantité est suffisante.
    // Retourne 1 si la réservation a réussi, 0 sinon.
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Article a SET a.quantity = a.quantity - :qty " +
           "WHERE a.id = :id AND a.quantity >= :qty")
    int reserve(@Param("id") Long id, @Param("qty") int qty);
}