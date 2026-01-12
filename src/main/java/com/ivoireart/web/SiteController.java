package com.ivoireart.web;

import com.ivoireart.repository.ArtisanRepository;
import com.ivoireart.repository.ProductRepository;
import com.ivoireart.repository.CartItemRepository;
import com.ivoireart.repository.OrderRepository;
import com.ivoireart.model.Artisan;
import com.ivoireart.model.Product;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ivoireart.model.CartItem;
import com.ivoireart.model.Order;
import com.ivoireart.model.OrderItem;

import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SiteController {

    private final ProductRepository productRepository;
    private final ArtisanRepository artisanRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public SiteController(ProductRepository productRepository,
                          ArtisanRepository artisanRepository,
                          CartItemRepository cartItemRepository,
                          OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.artisanRepository = artisanRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("artisans", artisanRepository.findAll());
        return "index";
    }

    @GetMapping("/produits")
    public String produits(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "produits";
    }

    @GetMapping("/artisans")
    public String artisans(Model model) {
        model.addAttribute("artisans", artisanRepository.findAll());
        return "artisans";
    }

    @GetMapping("/devenir-artisan")
    public String devenirArtisanForm(Model model) {
        if (!model.containsAttribute("artisan")) {
            model.addAttribute("artisan", new Artisan());
        }
        return "devenir-artisan";
    }

    @PostMapping("/devenir-artisan")
    public String devenirArtisanSubmit(@Valid Artisan artisan,
                                       BindingResult bindingResult,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("artisan", artisan);
            return "devenir-artisan";
        }
        artisanRepository.save(artisan);
        model.addAttribute("artisans", artisanRepository.findAll());
        model.addAttribute("success", "Votre profil artisan a ete cree et ajoute a la liste.");
        return "artisans";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/contact")
    public String sendContact(@RequestParam String nom,
                              @RequestParam String email,
                              @RequestParam String message,
                              Model model) {
        model.addAttribute("success", "Message envoye, merci !");
        model.addAttribute("nom", nom);
        model.addAttribute("email", email);
        model.addAttribute("message", message);
        return "contact";
    }

    @PostMapping("/panier/ajouter")
    public String ajouterAuPanier(@RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") Integer quantity,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/produits";
        }
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }
        String sessionId = session.getId();
        List<CartItem> items = cartItemRepository.findBySessionId(sessionId);
        CartItem existing = items.stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemRepository.save(existing);
        } else {
            cartItemRepository.save(new CartItem(sessionId, product, quantity));
        }
        redirectAttributes.addFlashAttribute("success", "Produit ajoute au panier.");
        return "redirect:/produits";
    }

    @GetMapping("/panier")
    public String panier(Model model, HttpSession session) {
        String sessionId = session.getId();
        List<CartItem> items = cartItemRepository.findBySessionId(sessionId);
        BigDecimal total = items.stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "panier";
    }

    @PostMapping("/panier/valider")
    @Transactional
    public String validerCommande(HttpSession session, RedirectAttributes redirectAttributes) {
        String sessionId = session.getId();
        List<CartItem> items = cartItemRepository.findBySessionId(sessionId);
        if (items.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Votre panier est vide.");
            return "redirect:/panier";
        }
        BigDecimal total = items.stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(sessionId, total);
        List<OrderItem> orderItems = items.stream()
                .map(ci -> new OrderItem(order, ci.getProduct(), ci.getQuantity(), ci.getProduct().getPrice()))
                .collect(Collectors.toList());
        order.getItems().addAll(orderItems);
        orderRepository.save(order);
        cartItemRepository.deleteBySessionId(sessionId);

        redirectAttributes.addFlashAttribute("success", "Commande validee. Merci pour votre achat !");
        return "redirect:/panier";
    }
}
