package fr.insa.ms.inventory_service.controller;

import fr.insa.ms.inventory_service.dao.ArticleDao;
import fr.insa.ms.inventory_service.model.Article;
import fr.insa.ms.inventory_service.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryResource {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    @SuppressWarnings("removal")
    private RestTemplate restTemplate;

    // Gestion du stock (CRUD) 

    @GetMapping("/articles")
    public List<Article> getAll() {
        return articleDao.findAll();
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<Article> getOne(@PathVariable("id") Long id) {
        return articleDao.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/articles")
    public Article add(@RequestBody Article article) {
        article.setId(null);
        return articleDao.save(article);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable("id") Long id, @RequestBody Article article) {
        if (!articleDao.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        article.setId(id);
        return ResponseEntity.ok(articleDao.save(article));
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (!articleDao.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        articleDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Vérification / réservation du stock 

    @GetMapping("/articles/{id}/check/{qty}")
    public boolean check(@PathVariable("id") Long id, @PathVariable("qty") int qty) {
        return articleDao.findById(id)
                .map(a -> a.getQuantity() >= qty)
                .orElse(false);
    }

    @PutMapping("/articles/{id}/reserve/{qty}")
    public boolean reserve(@PathVariable("id") Long id, @PathVariable("qty") int qty) {
        return articleDao.reserve(id, qty) == 1;
    }

    // Commandes en attente d'approvisionnement 

    @GetMapping("/pending-orders")
    public Order[] pendingOrders() {
        return restTemplate.getForObject(
                "http://order-service/orders/status/EN_ATTENTE_APPRO", Order[].class);
    }
}