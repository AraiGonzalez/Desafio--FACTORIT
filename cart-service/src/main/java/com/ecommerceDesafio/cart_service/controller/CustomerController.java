package com.ecommerceDesafio.cart_service.controller;

import com.ecommerceDesafio.cart_service.model.Customer;
import com.ecommerceDesafio.cart_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Gestión de clientes y estado VIP")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/vip")
    @Operation(summary = "Listar clientes VIP actuales")
    public ResponseEntity<List<Customer>> getVipCustomers() {
        return ResponseEntity.ok(customerService.getVipCustomers());
    }

    @PostMapping("/recalculate-vip")
    @Operation(summary = "Recalcular estado VIP para un mes dado")
    public ResponseEntity<Void> recalculateVip(
            @RequestParam int year,
            @RequestParam int month) {
        customerService.recalculateAllVipStatuses(year, month);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vip/new")
    @Operation(summary = "Clientes que PASARON a ser VIP en un mes dado")
    public ResponseEntity<List<Customer>> getNewVipInMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(customerService.getNewVipInMonth(year, month));
    }

    @GetMapping("/vip/lost")
    @Operation(summary = "Clientes que DEJARON de ser VIP en un mes dado")
    public ResponseEntity<List<Customer>> getLostVipInMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(customerService.getLostVipInMonth(year, month));
    }

}
